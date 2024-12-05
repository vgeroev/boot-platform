package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.vmalibu.module.security.configuration.authorized.flow.JwtAuthFlow.AUTHORIZATION_HEADER;

@ExtendWith(MockitoExtension.class)
class JwtAuthFlowTest {

    @Test
    @DisplayName("Test Case: Request has not authorization header. Awaiting false")
    void supportsWhenRequestHasNotAuthorizationHeaderTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(ArgumentMatchers.matches(AUTHORIZATION_HEADER))).thenReturn(null);

        Assertions.assertThat(new JwtAuthFlow().supports(request)).isFalse();
    }

    @Test
    @DisplayName("Test Case: Request has invalid bearer token. Awaiting false")
    void supportsWhenRequestHasInvalidBearerTokenTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(ArgumentMatchers.matches(AUTHORIZATION_HEADER))).thenReturn("Beeareeear token");

        Assertions.assertThat(new JwtAuthFlow().supports(request)).isFalse();
    }

    @Test
    @DisplayName("Test Case: Request has valid bearer token. Awaiting true")
    void supportsWhenRequestHasValidBearerTokenTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(ArgumentMatchers.matches(AUTHORIZATION_HEADER))).thenReturn("Bearer token");

        Assertions.assertThat(new JwtAuthFlow().supports(request)).isTrue();
    }
}
