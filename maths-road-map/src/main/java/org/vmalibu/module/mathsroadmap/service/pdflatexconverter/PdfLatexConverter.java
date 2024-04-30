package org.vmalibu.module.mathsroadmap.service.pdflatexconverter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.exception.PlatformException;

public interface PdfLatexConverter {

    byte @NonNull [] toPdf(@NonNull String latex) throws PlatformException;
}
