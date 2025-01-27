package org.vmalibu.module.security.service.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.access.struct.PrivilegeAuthority;
import org.vmalibu.module.security.authorization.source.AppUserSource;

import java.util.*;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserWithPrivilegesDTO userWithPrivileges = userService.findWithPrivileges(username);
        if (userWithPrivileges == null) {
            throw new UsernameNotFoundException("User not found by username: " + username);
        }

        UserDTO user = userWithPrivileges.user();
        Collection<PrivilegeAuthority> authorities = toAuthorities(userWithPrivileges.privileges());
        return new AppUserSource(user.id(), user.username(), user.password(), authorities);
    }

    private Collection<PrivilegeAuthority> toAuthorities(Map<String, Set<AccessOp>> privileges) {
        List<PrivilegeAuthority> authorities = new ArrayList<>(privileges.size());
        for (Map.Entry<String, Set<AccessOp>> entry : privileges.entrySet()) {
            authorities.add(new PrivilegeAuthority(entry.getKey(), entry.getValue().toArray(new AccessOp[0])));
        }
        return authorities;
    }
}
