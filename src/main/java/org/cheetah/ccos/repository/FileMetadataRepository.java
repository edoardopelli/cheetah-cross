package org.cheetah.ccos.repository;

import java.util.Optional;

import org.cheetah.ccos.model.FileMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    boolean existsByHash(String hash);

	Optional<FileMetadata> findByHash(String hash);
}