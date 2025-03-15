package org.cheetah.ccos.service;

import org.cheetah.ccos.cli.FileOperationParams;
import org.cheetah.ccos.model.FileMetadata;
import org.cheetah.ccos.model.FilePart;
import org.cheetah.ccos.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class FileStitchService {

    @Autowired
    private FileMetadataRepository repository;

    public void stitchFile(FileOperationParams params) {
        Path inputFolder = Path.of(params.getInputFolder());
        Path outputDir = params.getOutputDirectory();

        // Prendere il primo file e estrarre l'hash
        File[] partFiles = Objects.requireNonNull(inputFolder.toFile().listFiles((dir, name) -> name.endsWith(".part")));
        if (partFiles.length == 0) {
            throw new RuntimeException("Nessuna parte trovata nella directory di input.");
        }

        Arrays.sort(partFiles);
        String firstFileName = partFiles[0].getName();
        String hash = extractHashFromFileName(firstFileName);

        System.out.println("Hash rilevato dal primo file: " + hash);

        // Recupero dei metadati usando il metodo corretto findByHash
        FileMetadata metadata = repository.findByHash(hash).orElseThrow(() ->
                new RuntimeException("Nessun file trovato nel database con hash: " + hash));

        // Verifica delle parti
        if (partFiles.length != metadata.getParts().size()) {
            throw new RuntimeException("Il numero di parti non corrisponde a quello registrato nel DB.");
        }

        long totalSize = 0;
        for (FilePart part : metadata.getParts()) {
            File partFile = new File(inputFolder.toFile(), part.getName());
            if (!partFile.exists() || partFile.length() != part.getSize()) {
                throw new RuntimeException("Errore nella verifica della parte: " + part.getName());
            }
            totalSize += partFile.length();
        }

        if (totalSize != metadata.getOriginalFileSize()) {
            throw new RuntimeException("La somma delle parti non corrisponde alla dimensione del file originale.");
        }

        // Ricucitura delle parti
        Path stitchedFile = outputDir.resolve(metadata.getOriginalFileName());
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(stitchedFile.toFile()))) {
            Arrays.stream(partFiles).forEach(part -> {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(part))) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Errore nella lettura della parte: " + part.getName(), e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Errore nella scrittura del file finale: " + e.getMessage());
        }

        // Verifica finale della dimensione
        try {
            if (Files.size(stitchedFile) != metadata.getOriginalFileSize()) {
                throw new RuntimeException("La dimensione del file ricucito non corrisponde alla dimensione originale.");
            }
            System.out.println("✅ File ricucito correttamente: " + stitchedFile.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Errore nella verifica finale della dimensione del file ricucito.");
        }
    }

    private String extractHashFromFileName(String fileName) {
        String[] parts = fileName.split("_");
        return parts[1].replace(".part", "");
    }
}