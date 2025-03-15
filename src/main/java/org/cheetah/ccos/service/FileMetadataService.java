package org.cheetah.ccos.service;

import org.cheetah.ccos.model.FileMetadata;
import org.cheetah.ccos.model.FilePart;
import org.cheetah.ccos.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileMetadataService {

    @Autowired
    private FileMetadataRepository repository;

    public void saveFileMetadata(String hash, String originalFileName, long originalFileSize, List<FilePart> parts) {
        if (!repository.existsByHash(hash)) {
            FileMetadata metadata = new FileMetadata(null, hash, originalFileName, originalFileSize, parts);
            repository.save(metadata);
            System.out.println("File metadata salvato in MongoDB con hash: " + hash);
        } else {
            System.out.println("File con hash già presente nel database: " + hash);
        }
    }
}