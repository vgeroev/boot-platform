package org.vmalibu.module.security.authentication.jwt;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.vmalibu.module.security.access.AbstractPrivilege;
import org.vmalibu.module.security.access.AccessOp;
import org.vmalibu.module.security.access.PrivilegeAuthority;
import org.vmalibu.module.security.service.privilege.PrivilegeGetter;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.module.security.utils.JwtUtils;

import java.util.*;

@Component
public class JwtAuthenticationManager implements AuthenticationManager {

    public static final String PRIVILEGE_ACCESS_OP_DELIMITER = "-";
    public static final String AUTHORITIES_CLAIM_NAME = "privileges";

    private final PrivilegeGetter privilegeGetter;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserService userService;

    @Autowired
    public JwtAuthenticationManager(@Value("${jwkSetUri}") String jwkUri,
                                    @NonNull PrivilegeGetter privilegeGetter,
                                    @NonNull UserService userService) {
        this.privilegeGetter = privilegeGetter;
        this.jwtAuthenticationProvider = buildJwtAuthenticationProvider(buildJwtDecoder(jwkUri));
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication newAuthentication = jwtAuthenticationProvider.authenticate(authentication);
        if (newAuthentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Map<String, Set<AccessOp>> privilegePretenders = parsePrivilegePretenders(jwtAuthenticationToken);

            Set<PrivilegeAuthority> privilegeAuthorities = new HashSet<>();
            for (AbstractPrivilege privilege : privilegeGetter.getAvailablePrivileges()) {
                Set<AccessOp> accessOps = privilegePretenders.get(privilege.getKey());
                if (accessOps != null) {
                    privilegeAuthorities.add(new PrivilegeAuthority(privilege.getKey(), accessOps.toArray(new AccessOp[0])));
                }
            }

            Jwt jwt = jwtAuthenticationToken.getToken();
            String username = Objects.requireNonNull(JwtUtils.retrieveUsername(jwt));
            UserDTO userDTO = userService.findByUsername(username);
            if (userDTO == null) {
                throw new AuthenticationServiceException("There is no user with such username: " + username);
            }

            return new JwtAuthenticationTokenExt(
                    jwt,
                    privilegeAuthorities,
                    jwtAuthenticationToken.getName(),
                    userDTO.id(),
                    username
            );
        } else {
            throw new AuthenticationServiceException("Unknown authentication token: " + newAuthentication);
        }
    }

    private AccessOp parseAccessOp(String op) {
        for (AccessOp value : AccessOp.values()) {
            if (value.toString().equals(op)) {
                return value;
            }
        }

        return null;
    }

    private Map<String, Set<AccessOp>> parsePrivilegePretenders(JwtAuthenticationToken jwtAuthenticationToken) {
        Map<String, Set<AccessOp>> tokenPrivilegeAuthorities = new HashMap<>();
        for (GrantedAuthority grantedAuthority : jwtAuthenticationToken.getAuthorities()) {
            String authority = grantedAuthority.getAuthority();
            if (StringUtils.hasText(authority)) {
                int delimiterIndex = authority.indexOf(PRIVILEGE_ACCESS_OP_DELIMITER);
                if (delimiterIndex > -1 && delimiterIndex + 1 <= authority.length()) {
                    String key = authority.substring(0, delimiterIndex);
                    String op = authority.substring(delimiterIndex + 1);

                    AccessOp accessOp = parseAccessOp(op);
                    if (accessOp != null) {
                        tokenPrivilegeAuthorities.compute(key, (k, authorities) -> {
                            if (authorities == null) {
                                authorities = new HashSet<>();
                            }
                            authorities.add(accessOp);
                            return authorities;
                        });
                    }
                }
            }
        }

        return tokenPrivilegeAuthorities;
    }


    private JwtDecoder buildJwtDecoder(String jwkUri) {
        return NimbusJwtDecoder.withJwkSetUri(jwkUri).build();
    }

    private JwtAuthenticationProvider buildJwtAuthenticationProvider(JwtDecoder jwtDecoder) {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);
        provider.setJwtAuthenticationConverter(getJwtAuthenticationConverter());
        return provider;
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> getJwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_CLAIM_NAME);
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
