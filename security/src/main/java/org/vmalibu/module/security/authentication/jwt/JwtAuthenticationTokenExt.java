package org.vmalibu.module.security.authentication.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

@Getter
public class JwtAuthenticationTokenExt extends JwtAuthenticationToken {

    private final long userId;
    private final String username;

    public JwtAuthenticationTokenExt(Jwt jwt,
                                     Collection<? extends GrantedAuthority> authorities,
                                     String name,
                                     long userId,
                                     String username) {
        super(jwt, authorities, name);
        this.userId = userId;
        this.username = username;
    }

}
