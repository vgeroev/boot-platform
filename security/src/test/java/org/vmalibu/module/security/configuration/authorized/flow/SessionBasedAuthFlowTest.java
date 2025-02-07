package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionBasedAuthFlowTest {

    @Test
    @DisplayName("Test Case: Request has null instead of cookies or empty array. Awaiting false")
    void supportsWhenRequestHasNullInsteadOfCookiesTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(null);
        Assertions.assertThat(new SessionBasedAuthFlow("SESSION").supports(request)).isFalse();

        when(request.getCookies()).thenReturn(new Cookie[0]);
        Assertions.assertThat(new SessionBasedAuthFlow("SESSION").supports(request)).isFalse();

    }

    @Test
    @DisplayName("Test Case: Request has session cookies. Awaiting true")
    void supportsWhenRequestHasSessionCookiesTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(25));
        cookies[1] = new Cookie("SESSION", RandomStringUtils.randomAlphabetic(25));
        when(request.getCookies()).thenReturn(cookies);

        Assertions.assertThat(new SessionBasedAuthFlow("SESSION").supports(request)).isTrue();
    }

    @Test
    @DisplayName("Test Case: Request hasn't session cookies. Awaiting false")
    void supportsWhenRequestHasNotSessionCookiesTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("cookie1", RandomStringUtils.randomAlphabetic(25));
        cookies[1] = new Cookie("cookie2", RandomStringUtils.randomAlphabetic(25));
        when(request.getCookies()).thenReturn(cookies);

        Assertions.assertThat(new SessionBasedAuthFlow("SESSION").supports(request)).isFalse();
    }
}
