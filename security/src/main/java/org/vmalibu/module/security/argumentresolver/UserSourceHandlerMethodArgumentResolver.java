package org.vmalibu.module.security.argumentresolver;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.vmalibu.module.security.authorization.source.UserSource;

import java.util.Objects;

public class UserSourceHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UserSource.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserSource(authentication);
    }

    // TODO Kostyl' :) Need to be refactored.
    private UserSource getUserSource(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new IllegalStateException("Unexpected authentication " + authentication.getClass());
        }

        return new UserSource() {
            @Override
            public @NonNull String getUserId() {
                return authentication.getName();
            }

            @Override
            public @NonNull String getUsername() {
                return Objects.requireNonNull(
                        jwtAuthenticationToken.getTokenAttributes().get("preferred_username")
                ).toString();
            }

        };
    }
}
