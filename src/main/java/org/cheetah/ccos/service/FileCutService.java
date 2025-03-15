package org.cheetah.ccos.service;

import org.cheetah.ccos.cli.FileOperationParams;
import org.cheetah.ccos.model.FilePart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileCutService {

    @Autowired
    private FileMetadataService metadataService;

    public void cutFile(FileOperationParams params) {
        try {
            Path inputFile = params.getInputFile();
            Path outputDir = params.getOutputDirectory();
            long chunkSize = params.getChunkSize();
            long fileSize = Files.size(inputFile);

            // Calcolo del numero totale di parti
            long totalParts = (fileSize + chunkSize - 1) / chunkSize;
            int paddingLength = String.valueOf(totalParts).length();

            // Calcolo dell'hash del nome del file
            String fileHash = generateFileHash(inputFile.getFileName().toString());
            List<FilePart> parts = new ArrayList<>();

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile.toFile()))) {
                byte[] buffer = new byte[(int) chunkSize];
                int bytesRead;
                int partCounter = 1;

                while ((bytesRead = bis.read(buffer)) > 0) {
                    String paddedPart = String.format("%0" + paddingLength + "d", partCounter);
                    String newFileName = paddedPart + "_" + fileHash + ".part";
                    Path newFile = outputDir.resolve(newFileName);

                    try (FileOutputStream out = new FileOutputStream(newFile.toFile())) {
                        out.write(buffer, 0, bytesRead);
                        parts.add(new FilePart(newFileName, bytesRead));
                        System.out.println("Creato: " + newFile);
                    }

                    partCounter++;
                }
            }

            // Salvataggio dei metadati in MongoDB
            metadataService.saveFileMetadata(fileHash, inputFile.getFileName().toString(), fileSize, parts);

            System.out.println("Operazione di cut completata e metadati salvati su MongoDB!");

        } catch (IOException e) {
            System.err.println("Errore durante lo split del file: " + e.getMessage());
        }
    }

    private String generateFileHash(String fileName) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(fileName.getBytes());
            BigInteger bigInt = new BigInteger(1, hashBytes);
            return bigInt.toString(16);
        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione dell'hash: " + e.getMessage());
        }
    }
}