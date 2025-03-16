package org.cheetah.ccos.service;

import org.cheetah.ccos.cli.FileOperationParams;
import org.cheetah.ccos.model.FilePart;
import org.cheetah.ccos.repository.FileMetadataRepository;
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

            // Generate hash from file content
            String fileHash = generateContentHash(inputFile);
            System.out.println("Generated Content Hash: " + fileHash);

            // Calculate total parts and padding
            long totalParts = (fileSize + chunkSize - 1) / chunkSize;
            int paddingLength = String.valueOf(totalParts).length();

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
                        System.out.println("Created part: " + newFileName);
                    }

                    partCounter++;
                }
            }

            // Save metadata to MongoDB
            metadataService.saveFileMetadata(fileHash, inputFile.getFileName().toString(), fileSize, parts);

            System.out.println("✅ Cut operation completed and metadata saved to MongoDB!");

        } catch (IOException e) {
            System.err.println("❌ Error during file splitting: " + e.getMessage());
        }
    }

    private String generateContentHash(Path file) {
        try (InputStream fis = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            byte[] bytes = digest.digest();
            BigInteger no = new BigInteger(1, bytes);
            return no.toString(16);
        } catch (Exception e) {
            throw new RuntimeException("Error generating content hash: " + e.getMessage());
        }
    }
}