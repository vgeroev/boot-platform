package org.vmalibu.module.security.argumentresolver;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.vmalibu.module.security.authorization.source.UserSource;

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

    private UserSource getUserSource(Authentication authentication) {
        return new UserSource() {
            @Override
            public @NonNull String getUserId() {
                return authentication.getName();
            }

            @Override
            public @NonNull Authentication getAuthentication() {
                return authentication;
            }
        };
    }
}
