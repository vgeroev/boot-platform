package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFlowRequestMatcherTest {

    @Mock
    private RequestMatcher requestMatcher;

    @Test
    @DisplayName("Test Case: There is multiple auth flows. Awaiting RequestRejectedException")
    void matchesWhenThereAreMultipleAuthFlowsTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthFlow flow1 = mock(AuthFlow1.class);
        when(flow1.supports(request)).thenReturn(true);

        AuthFlow flow2 = mock(AuthFlow2.class);
        when(flow2.supports(request)).thenReturn(false);

        AuthFlow flow3 = mock(AuthFlow3.class);
        when(flow3.supports(request)).thenReturn(true);

        AuthFlowRequestMatcher matcher = new AuthFlowRequestMatcher(
                requestMatcher, List.of(flow1, flow2, flow3), flow1.getClass(), false
        );
        Assertions.assertThatThrownBy(() -> matcher.matches(request))
                .isExactlyInstanceOf(RequestRejectedException.class);
    }

    @Test
    @DisplayName("Test Case: Request does not support auth flow. Awaiting false")
    void matchesWhenRequestDoesNotSupportAuthFlowTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthFlow flow1 = mock(AuthFlow1.class);
        when(flow1.supports(request)).thenReturn(false);

        AuthFlow flow2 = mock(AuthFlow2.class);
        when(flow2.supports(request)).thenReturn(false);

        AuthFlow flow3 = mock(AuthFlow3.class);
        when(flow3.supports(request)).thenReturn(true);

        AuthFlowRequestMatcher matcher = new AuthFlowRequestMatcher(
                requestMatcher, List.of(flow1, flow2, flow3), flow1.getClass(), false
        );
        Assertions.assertThat(matcher.matches(request)).isFalse();

        verify(requestMatcher, never()).matches(request);
    }

    @Test
    @DisplayName("Test Case: Request supports auth flow. Awaiting result from requestMatcher")
    void matchesWhenRequestSupportsAuthFlowTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthFlow flow1 = mock(AuthFlow1.class);
        when(flow1.supports(request)).thenReturn(true);

        AuthFlow flow2 = mock(AuthFlow2.class);
        when(flow2.supports(request)).thenReturn(false);

        AuthFlow flow3 = mock(AuthFlow3.class);
        when(flow3.supports(request)).thenReturn(false);

        boolean result = ThreadLocalRandom.current().nextBoolean();
        when(requestMatcher.matches(request)).thenReturn(result);

        AuthFlowRequestMatcher matcher = new AuthFlowRequestMatcher(
                requestMatcher, List.of(flow1, flow2, flow3), flow1.getClass(), false
        );
        Assertions.assertThat(matcher.matches(request)).isEqualTo(result);

        verify(requestMatcher, only()).matches(request);
    }

    @Test
    @DisplayName("Test Case: Request does not support any auth flow, but this auth flow is default. Awaiting result from requestMatcher")
    void matchesWhenRequestDoesNotSupportAnyAuthFlowByAuthFlowIsDefaultTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthFlow flow1 = mock(AuthFlow1.class);
        when(flow1.supports(request)).thenReturn(false);

        AuthFlow flow2 = mock(AuthFlow2.class);
        when(flow2.supports(request)).thenReturn(false);

        AuthFlow flow3 = mock(AuthFlow3.class);
        when(flow3.supports(request)).thenReturn(false);

        boolean result = ThreadLocalRandom.current().nextBoolean();
        when(requestMatcher.matches(request)).thenReturn(result);

        AuthFlowRequestMatcher matcher = new AuthFlowRequestMatcher(
                requestMatcher, List.of(flow1, flow2, flow3), flow1.getClass(), true
        );
        Assertions.assertThat(matcher.matches(request)).isEqualTo(result);

        verify(requestMatcher, only()).matches(request);
    }

    private interface AuthFlow1 extends AuthFlow { }

    private interface AuthFlow2 extends AuthFlow { }

    private interface AuthFlow3 extends AuthFlow { }

}
