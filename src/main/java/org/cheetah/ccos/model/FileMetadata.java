package org.cheetah.ccos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Document(collection = "file_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {

    @Id
    private String id;
    private String hash;
    private String originalFileName;
    private long originalFileSize;
    private List<FilePart> parts;



}