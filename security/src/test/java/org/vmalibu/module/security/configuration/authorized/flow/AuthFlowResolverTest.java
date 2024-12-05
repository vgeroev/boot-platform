package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFlowResolverTest {

    @Test
    @DisplayName("Test Case: Resolving auth flows for request")
    void resolveAuthFlowsTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        AuthFlow flow1 = mock(AuthFlow1.class);
        when(flow1.supports(request)).thenReturn(true);

        AuthFlow flow2 = mock(AuthFlow2.class);
        when(flow2.supports(request)).thenReturn(false);

        AuthFlow flow3 = mock(AuthFlow3.class);
        when(flow3.supports(request)).thenReturn(true);

        Set<Class<? extends AuthFlow>> resolved = new AuthFlowResolver(List.of(flow1, flow2, flow3)).resolve(request);

        Assertions.assertThat(resolved).containsExactlyInAnyOrder(flow1.getClass(), flow3.getClass());
    }

    private interface AuthFlow1 extends AuthFlow { }

    private interface AuthFlow2 extends AuthFlow { }

    private interface AuthFlow3 extends AuthFlow { }
}
