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
import org.vmalibu.module.security.authorization.source.builder.UserSourceBuilder;

import java.util.List;

public class UserSourceHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final List<UserSourceBuilder> builders;

    public UserSourceHandlerMethodArgumentResolver(@NonNull List<UserSourceBuilder> userSourceBuilders) {
        this.builders = List.copyOf(userSourceBuilders);
    }

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
        return buildUserSource(authentication);
    }

    private UserSource buildUserSource(Authentication authentication) {
        for (UserSourceBuilder builder : builders) {
            if (builder.supports(authentication)) {
                return builder.build(authentication);
            }
        }

        throw new IllegalStateException("Unexpected authentication: " + authentication.getClass());
    }
}
