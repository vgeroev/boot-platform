package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface AuthFlow {

    boolean supports(@NonNull HttpServletRequest request);
}
