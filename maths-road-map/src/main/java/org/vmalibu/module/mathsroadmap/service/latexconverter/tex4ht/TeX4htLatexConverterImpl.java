package org.vmalibu.module.mathsroadmap.service.latexconverter.tex4ht;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TeX4htLatexConverterImpl implements TeX4htLatexConverter {

    @Override
    public void toHtml(@NonNull String latex,
                       @NonNull Path destinationDir,
                       @Nullable String configuration) throws PlatformException {
        String tempFilesPostfix = UUID.randomUUID().toString();
        Path latexTempFile = createTempFile("latex-", tempFilesPostfix + ".tex", latex);
        Path configurationTempFile = configuration != null
                        ? createTempFile("tex4ht-conf-", tempFilesPostfix + ".cfg", configuration)
                        : null;
        try {
            exec(destinationDir, latexTempFile, configurationTempFile);
        } catch (IOException e) {
            log.error("Failed to convert latex: {}", latex, e);
            throw GeneralExceptionFactory.buildIOErrorException(e);
        } finally {
            try {
                Files.deleteIfExists(latexTempFile);
            } catch (IOException e) {
                log.error("Failed to delete latex temp file: " + latexTempFile, e);
            }

            if (configurationTempFile != null) {
                try {
                    Files.deleteIfExists(configurationTempFile);
                } catch (IOException e) {
                    log.error("Failed to delete configuration temp file: " + configurationTempFile, e);
                }
            }
        }
    }

    private static Path createTempFile(String prefix, String postfix, String data) throws PlatformException {
        try {
            Path tempFile = Files.createTempFile(prefix, postfix);
            Files.writeString(tempFile, data);
            return tempFile;
        } catch (IOException e) {
            log.error("Failed to create temp file", e);
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }
    }

    private static void exec(Path destinationDir,
                             Path latexPath,
                             Path configurationPath) throws IOException, PlatformException {
        String command = getCommand(destinationDir, latexPath, configurationPath);
        log.debug("Running make4ht command {} ...", command);
        Process process = Runtime.getRuntime().exec(command);
        try {
            process.waitFor(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

        String cmdOutput;
        try (InputStreamReader isr = new InputStreamReader(process.getInputStream())) {
            cmdOutput = CharStreams.toString(isr);
        }

        int exitValue = process.exitValue();
        if (exitValue != 0) {
            throw MathsRoadMapExceptionFactory.buildInvalidLatexSyntaxException(cmdOutput);
        }
    }

    private static String getCommand(Path destinationDir, Path latexPath, Path configurationPath) {
        StringBuilder sb = new StringBuilder("make4ht -B %s -j index ".formatted(destinationDir));
        if (configurationPath != null) {
            sb.append("-c ").append(configurationPath).append(" ");
        }

        return sb.append(latexPath).toString();
    }
}
