package org.vmalibu.module.mathsroadmap.service.latexconverter.tex4ht;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.module.exception.PlatformException;

import java.nio.file.Path;

public interface TeX4htLatexConverter {

    void toHtml(@NonNull String latex,
                @NonNull Path destinationDir,
                @Nullable String configuration) throws PlatformException;

}
