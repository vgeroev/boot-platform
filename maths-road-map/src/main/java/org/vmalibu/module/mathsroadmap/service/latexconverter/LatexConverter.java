package org.vmalibu.module.mathsroadmap.service.latexconverter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.exception.PlatformException;

import java.nio.file.Path;

public interface LatexConverter {

    void toHtml(@NonNull String latex, @NonNull Path destinationDir) throws PlatformException;

}
