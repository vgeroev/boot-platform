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
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.security.BaseTestClass;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.access.struct.PrivilegeAuthority;
import org.vmalibu.module.security.authorization.source.AppUserSource;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.when;

public class AuthorizedSecurityConfigurationTest extends BaseTestClass {

    public static final String AUTHORIZED_CONTROLLER_PATH = "/authorized/__test_path__";
    public static final String PRIVILEGED_ONE_AND_CONTROLLER_PATH = "/authorized/__privileged_one_and_test_path__";
    public static final String PRIVILEGED_MULTIPLE_AND_CONTROLLER_PATH = "/authorized/__privileged_multiple_and_test_path__";
    public static final String PRIVILEGED_ONE_OR_CONTROLLER_PATH = "/authorized/__privileged_one_or_test_path__";
    public static final String PRIVILEGED_MULTIPLE_OR_CONTROLLER_PATH = "/authorized/__privileged_multiple_or_test_path__";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;
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
        AppUserSource userSource = new AppUserSource(id, username, encodedPassword, List.of());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);

        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(AUTHORIZED_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

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

    @Test
    @DisplayName("Test Case: Testing access to controller when user has sufficient privileges - JoinType=AND, only one privilege.")
    void privilegedAndOneWhenUserIsAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(new PrivilegeAuthority(UserPrivilege.INSTANCE.getKey(), AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE))
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_ONE_AND_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .contains(username)
                .contains(encodedPassword)
                .contains(String.valueOf(id));
    }

    @Test
    @DisplayName("Test Case: Testing access to controller when user does not have sufficient privileges - JoinType=AND, only one privilege.")
    void privilegedAndOneWhenUserIsNotAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(new PrivilegeAuthority(UserPrivilege.INSTANCE.getKey(), AccessOp.WRITE))
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_ONE_AND_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Test Case: Testing access to controller when user has sufficient privileges - JoinType=AND, multiple privileges.")
    void privilegedAndMultipleWhenUserIsAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(
                        new PrivilegeAuthority(UserPrivilege.INSTANCE.getKey(), AccessOp.READ, AccessOp.DELETE),
                        new PrivilegeAuthority(AccessRolePrivilege.INSTANCE.getKey(), AccessOp.WRITE, AccessOp.READ)
                )
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_MULTIPLE_AND_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .contains(username)
                .contains(encodedPassword)
                .contains(String.valueOf(id));
    }

    @Test
    @DisplayName("Test Case: Testing access to controller when user does not have sufficient privileges - JoinType=AND, multiple privileges.")
    void privilegedAndMultipleWhenUserIsNotAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(new PrivilegeAuthority(UserPrivilege.INSTANCE.getKey(), AccessOp.READ, AccessOp.WRITE))
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_MULTIPLE_AND_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Test Case: Testing access to controller when user has sufficient privileges - JoinType=OR, only one privilege.")
    void privilegedOrOneWhenUserIsAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(new PrivilegeAuthority(UserPrivilege.INSTANCE.getKey(), AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE))
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_ONE_OR_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .contains(username)
                .contains(encodedPassword)
                .contains(String.valueOf(id));
    }

    @Test
    @DisplayName("Test Case: Testing access to controller when user does not have sufficient privileges - JoinType=OR, only one privilege.")
    void privilegedOrOneWhenUserIsNotAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(new PrivilegeAuthority(UserPrivilege.INSTANCE.getKey(), AccessOp.READ))
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_ONE_OR_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Test Case: Testing access to controller when user has sufficient privileges - JoinType=OR, multiple privileges.")
    void privilegedOrMultipleWhenUserIsAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(
                        new PrivilegeAuthority(AccessRolePrivilege.INSTANCE.getKey(), AccessOp.WRITE, AccessOp.READ)
                )
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_MULTIPLE_OR_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody())
                .contains(username)
                .contains(encodedPassword)
                .contains(String.valueOf(id));
    }

    @Test
    @DisplayName("Test Case: Testing access to controller when user does not have sufficient privileges - JoinType=OR, multiple privileges.")
    void privilegedOrMultipleWhenUserIsNotAuthorizedTest() {
        long id = ThreadLocalRandom.current().nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String encodedPassword = passwordEncoder.encode(password);

        AppUserSource userSource = new AppUserSource(
                id,
                username,
                encodedPassword,
                List.of(
                        new PrivilegeAuthority(UserPrivilege.INSTANCE.getKey(), AccessOp.READ),
                        new PrivilegeAuthority(AccessRolePrivilege.INSTANCE.getKey(), AccessOp.DELETE)
                )
        );
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userSource);
        HttpHeaders headers = sessionAuth(username, password);

        String endPointUrl = getUrl(PRIVILEGED_MULTIPLE_OR_CONTROLLER_PATH);
        ResponseEntity<String> response = restTemplate.exchange(endPointUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    private HttpHeaders sessionAuth(String username, String password) {
        String payload = "{ \"username\":\"%s\", \"password\":\"%s\" }".formatted(username, password);

        String loginUrl = getUrl(AuthorizedSecurityConfiguration.PATH_LOGIN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getHeaders()).containsKey(HttpHeaders.SET_COOKIE);

        List<String> setCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        Assertions.assertThat(setCookie).isNotNull();

        String session = setCookie.get(0);
        HttpHeaders responseHttpHeaders = new HttpHeaders();
        responseHttpHeaders.add(HttpHeaders.COOKIE, session);

        return responseHttpHeaders;
    }

    private String getUrl(String path) {
        return "http://localhost:%d".formatted(port) + path;
    }
}
