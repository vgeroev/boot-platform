package org.vmalibu.module.mathsroadmap.service.pdflatexconverter;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class PdfLatexConverterImpl implements PdfLatexConverter {

    @Override
    public byte @NonNull [] toPdf(@NonNull String latex) throws PlatformException {
        Path rawLatexPath = createRawLatexTempFile();
        String jobName = getJobName();
        String directory = getTempDirectory();
        Path directoryPath = Path.of(directory);
        try {
            Files.writeString(rawLatexPath, latex);

            int exitValue = exec(directory, jobName, rawLatexPath);
            if (exitValue != 0) {
                throw MathsRoadMapExceptionFactory.buildInvalidLatexSyntaxException(latex);
            }

            String pdfFilename = getPdfFilename(jobName);
            return Files.readAllBytes(directoryPath.resolve(pdfFilename));
        } catch (IOException e) {
            log.error("Failed to convert latex: {}", latex, e);
            throw GeneralExceptionFactory.buildIOErrorException(e);
        } finally {
            try {
                Files.deleteIfExists(rawLatexPath);
            } catch (IOException e) {
                log.error("Failed to delete latex file: " + rawLatexPath, e);
            } finally {
                removeJobFilesSilently(directoryPath, jobName);
            }
        }
    }

    private static Path createRawLatexTempFile() throws PlatformException {
        try {
            return Files.createTempFile("latex-raw-", "");
        } catch (IOException e) {
            log.error("Failed to create latex temp file", e);
            throw GeneralExceptionFactory.buildIOErrorException(e);
        }
    }

    private static int exec(String directoryPath, String jobName, Path rawLatexPath) throws IOException {
        Process process = Runtime.getRuntime().exec(getCommand(directoryPath, jobName, rawLatexPath));
        try {
            process.waitFor(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

        return process.exitValue();
    }

    private static String getCommand(String directoryPath, String jobName, Path rawLatexPath) {
        return "pdflatex -interaction=nonstopmode -output-directory=%s -jobname=%s %s"
                .formatted(directoryPath, jobName, rawLatexPath.toAbsolutePath());
    }

    private static String getJobName() {
       return "latex-pdf-" + UUID.randomUUID();
    }

    private static String getPdfFilename(String jobName) {
        return "%s.pdf".formatted(jobName);
    }

    private static String getTempDirectory() {
       return System.getProperty("java.io.tmpdir");
    }

    private static void removeJobFilesSilently(Path directoryPath, String jobName) {
        Set<Path> jobFiles;
        try (Stream<Path> list = Files.list(directoryPath)) {
            jobFiles = list
                    .filter(f -> f.toFile().getName().startsWith(jobName))
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Failed to get job files (jobname: {})", jobName, e);
            return;
        }

        for (Path jobFile : jobFiles) {
            try {
                Files.deleteIfExists(jobFile);
            } catch (IOException e) {
                log.error("Failed to delete job file: {}", jobFile, e);
            }
        }
    }
}
