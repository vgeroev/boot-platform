package org.vmalibu.module.mathsroadmap.service.latexconverter;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TeX4htConverter implements LatexConverter {

    @Override
    public void toHtml(@NonNull String latex,
                       @NonNull Path destinationDir) throws PlatformException {
        Path latexTempFile = createLatexTempFile();
        try {
            Files.writeString(latexTempFile, latex);
            int exitValue = exec(destinationDir, latexTempFile);
            if (exitValue != 0) {
                throw MathsRoadMapExceptionFactory.buildInvalidLatexSyntaxException(latex);
            }
        } catch (IOException e) {
            log.error("Failed to convert latex: {}", latex, e);
            throw GeneralExceptionFactory.buildIOErrorException(e);
        } finally {
            try {
                Files.deleteIfExists(latexTempFile);
            } catch (IOException e) {
                log.error("Failed to delete latex temp file: " + latexTempFile, e);
            }
        }
    }

    private static Path createLatexTempFile() throws PlatformException {
        try {
            return Files.createTempFile("latex-", ".tex");
        } catch (IOException e) {
            log.error("Failed to create latex temp file", e);
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }
    }

    private static int exec(Path destinationDir, Path latexPath) throws IOException {
        Process process = Runtime.getRuntime().exec(getCommand(destinationDir, latexPath));
        try {
            process.waitFor(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

        return process.exitValue();
    }

    private static String getCommand(Path destinationDir, Path latexPath) {
        return (
                "make4ht " +
//                "-d %s " +
                "-B %s " +
                "-j index " +
                "%s"
        ).formatted(
                destinationDir,
                latexPath
        );
    }
}
