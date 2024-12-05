package org.vmalibu.module.security.configuration.authorized;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.security.BaseTestClass;
import org.vmalibu.module.security.authorization.source.AppUserSource;
import org.vmalibu.module.security.service.user.UserService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.when;

public class AuthorizedSecurityConfigurationTest extends BaseTestClass {

    public static final String AUTHORIZED_CONTROLLER_PATH = "/authorized/__test_path__";

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private UserService userService;
    @MockBean
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Test Case: Trying to get data from protected end point without session. Awaiting HTTP status 403")
    void getDataFromProtectedEndPointTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String endPointUrl = getUrl(AUTHORIZED_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Test Case: Testing full session auth flow:" +
            " authorizing by username/password to generate session, getting data from protected end point, logging out")
    void sessionAuthFlowTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        String encodedPassword = passwordEncoder.encode(password);
        AppUserSource userSource = new AppUserSource(id, username, encodedPassword);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);

        String loginUrl = getUrl(AuthorizedSecurityConfiguration.PATH_LOGIN);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", username);
        body.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE)).isTrue();

        List<String> setCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        Assertions.assertThat(setCookie).isNotNull();

        String session = setCookie.get(0);
        headers.add(HttpHeaders.COOKIE, session);

        String endPointUrl = getUrl(AUTHORIZED_CONTROLLER_PATH);
        response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .contains(username)
                .contains(encodedPassword)
                .contains(String.valueOf(id));

        String logoutUrl = getUrl(AuthorizedSecurityConfiguration.PATH_LOGOUT);
        response = restTemplate.exchange(logoutUrl, HttpMethod.POST, new HttpEntity<>(headers), String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private String getUrl(String path) {
        return "http://localhost:%d".formatted(port) + path;
    }
}
