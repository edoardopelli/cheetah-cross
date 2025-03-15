package org.cheetah.ccos.cli;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class FileOperationParams {
    private String operation;
    private Path inputFile;
    private long chunkSize;
    private Path outputDirectory;
    private String inputFolder;
}	