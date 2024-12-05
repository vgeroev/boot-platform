package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthFlowResolver {

    private final List<AuthFlow> authFlows;

    public AuthFlowResolver(@NonNull List<@NonNull AuthFlow> authFlows) {
        this.authFlows = authFlows;
    }

    public @NonNull Set<Class<? extends AuthFlow>> resolve(@NonNull HttpServletRequest request) {
        Set<Class<? extends AuthFlow>> supports = new HashSet<>();
        for (AuthFlow authFlow : authFlows) {
            if (authFlow.supports(request)) {
                supports.add(authFlow.getClass());
            }
        }
        return supports;
    }

}
