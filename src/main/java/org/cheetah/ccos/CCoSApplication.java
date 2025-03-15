package org.cheetah.ccos;

import org.cheetah.ccos.cli.CommandLineProcessor;
import org.cheetah.ccos.service.FileCutService;
import org.cheetah.ccos.service.FileStitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CCoSApplication implements ApplicationRunner {

    @Autowired
    private CommandLineProcessor processor;

    @Autowired
    private FileCutService fileCutService;

    @Autowired
    private FileStitchService fileStitchService;

    public static void main(String[] args) {
        SpringApplication.run(CCoSApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        processor.processArgs(args).ifPresent(params -> {
            String operation = params.getOperation();

            switch (operation.toLowerCase()) {
                case "cut":
                    fileCutService.cutFile(params);
                    break;

                case "stitch":
                    fileStitchService.stitchFile(params);
                    break;

                default:
                    System.err.println("Operazione non supportata: '" + operation + "'. Operazioni supportate: 'cut' e 'stitch'.");
                    break;
            }
        });
    }
}