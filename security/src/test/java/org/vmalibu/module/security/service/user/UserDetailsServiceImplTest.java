package org.vmalibu.module.security.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.security.authorization.source.AppUserSource;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Test Case: Loading user by username when there is no such user. Awaiting UsernameNotFoundException")
    void loadUserByUsernameWhenThereIsNoSuchUserTest() {
        when(userService.findWithPrivileges(anyString())).thenReturn(null);

        Assertions.assertThatThrownBy(
                () -> userDetailsService.loadUserByUsername(RandomStringUtils.randomAlphabetic(10))
        ).isExactlyInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Test Case: Loading user by username. Awaiting correct user")
    void loadUserByUsernameTest() {
        long userId = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        UserDTO userDTO = mock(UserDTO.class);
        when(userDTO.id()).thenReturn(userId);
        when(userDTO.username()).thenReturn(username);
        when(userDTO.password()).thenReturn(password);
        when(userService.findWithPrivileges(username)).thenReturn(new UserWithPrivilegesDTO(userDTO, Map.of()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Assertions.assertThat(userDetails).isNotNull()
                .returns(username, UserDetails::getUsername)
                .returns(password, UserDetails::getPassword)
                .isInstanceOfSatisfying(
                        AppUserSource.class,
                        userSource -> Assertions.assertThat(userSource).returns(userId, AppUserSource::getId)
                );
    }
}
