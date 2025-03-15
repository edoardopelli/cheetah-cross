package org.cheetah.ccos.cli;

import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class CommandLineProcessor {

    public Optional<FileOperationParams> processArgs(ApplicationArguments args) {
        String operation = getOption(args, "operation");
        String outputPath = getOption(args, "output");

        if ("cut".equalsIgnoreCase(operation)) {
            String inputPath = getOption(args, "input");
            String sizeInput = getOption(args, "size");

            long sizeInBytes = parseSize(sizeInput);
            return Optional.of(new FileOperationParams(operation, Paths.get(inputPath), sizeInBytes, Paths.get(outputPath), null));
        } else if ("stitch".equalsIgnoreCase(operation)) {
            String inputFolder = getOption(args, "input");
            return Optional.of(new FileOperationParams(operation, null, 0, Paths.get(outputPath), inputFolder));
        }

        System.err.println("Operazione non supportata. Utilizzo: --operation=cut|stitch");
        return Optional.empty();
    }

    private String getOption(ApplicationArguments args, String optionName) {
        if (!args.containsOption(optionName)) {
            throw new IllegalArgumentException("Parametro mancante: --" + optionName);
        }
        return args.getOptionValues(optionName).get(0);
    }

    private long parseSize(String sizeInput) {
        if (sizeInput.toUpperCase().endsWith("KB")) {
            return Long.parseLong(sizeInput.replace("KB", "")) * 1024;
        } else if (sizeInput.toUpperCase().endsWith("MB")) {
            return Long.parseLong(sizeInput.replace("MB", "")) * 1024 * 1024;
        } else {
            throw new IllegalArgumentException("Formato di dimensione non valido. Usa solo 'KB' o 'MB'.");
        }
    }
}