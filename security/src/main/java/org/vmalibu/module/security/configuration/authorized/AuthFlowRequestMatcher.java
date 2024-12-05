package org.vmalibu.module.security.configuration.authorized;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.vmalibu.module.security.configuration.authorized.flow.AuthFlow;
import org.vmalibu.module.security.configuration.authorized.flow.AuthFlowResolver;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class AuthFlowRequestMatcher implements RequestMatcher {

    private static final String RESOLVED_AUTH_FLOWS = AuthFlowRequestMatcher.class.getName().concat(".RESOLVED_AUTH_FLOWS");

    private final RequestMatcher requestMatcher;
    private final AuthFlowResolver authFlowResolver;
    private final Class<? extends AuthFlow> authFlow;
    private final boolean defaultAuthFlow;

    public AuthFlowRequestMatcher(@NonNull RequestMatcher requestMatcher,
                                  @NonNull List<AuthFlow> authFlows,
                                  @NonNull Class<? extends AuthFlow> authFlow,
                                  boolean defaultAuthFlow) {
        this.requestMatcher = requestMatcher;
        this.authFlowResolver = new AuthFlowResolver(authFlows);
        this.authFlow = authFlow;
        this.defaultAuthFlow = defaultAuthFlow;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Set<Class<? extends AuthFlow>> authFlows = (Set<Class<? extends AuthFlow>>) request.getAttribute(RESOLVED_AUTH_FLOWS);
        if (authFlows == null) {
            authFlows = authFlowResolver.resolve(request);
            request.setAttribute(RESOLVED_AUTH_FLOWS, authFlows);
        }

        if (defaultAuthFlow && authFlows.isEmpty()) {
            return requestMatcher.matches(request);
        }

        if (authFlows.size() > 1) {
            String flows = authFlows.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(","));
            String msg = "Multiple authentication flows are not allowed: %s".formatted(flows);
            throw new RequestRejectedException(msg);
        }

        if (!authFlows.contains(authFlow)) {
            return false;
        }

        return requestMatcher.matches(request);
    }

    @Override
    public String toString() {
        return "AuthFlowRequestMatcher{" +
                "authFlow=" + authFlow.getSimpleName() +
                '}';
    }
}
