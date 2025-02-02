package org.vmalibu.module.security.authorization.manager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.access.struct.PrivilegeAuthority;
import org.vmalibu.module.security.authorization.controller.ControllerAuthDetails;
import org.vmalibu.module.security.authorization.controller.ControllerDetails;
import org.vmalibu.module.security.authorization.controller.ControllerMappingInfoGetter;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeJoinType;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Component
public class PrivilegeAuthorizationManager implements CustomAuthorizationManager {

    private static final AuthorizationDecision ACCESS_GRANTED = new AuthorizationDecision(true);
    private static final AuthorizationDecision ACCESS_DENIED = new AuthorizationDecision(false);

    private final ControllerMappingInfoGetter controllerMappingInfoGetter;

    @Autowired
    public PrivilegeAuthorizationManager(ControllerMappingInfoGetter controllerMappingInfoGetter) {
        this.controllerMappingInfoGetter = controllerMappingInfoGetter;
    }

    @Override
    public @Nullable AuthorizationDecision check(@NonNull Supplier<Authentication> authenticationSupplier,
                                                 @NonNull HttpServletRequest request) {
        Authentication authentication = authenticationSupplier.get();
        if (!authentication.isAuthenticated()) {
            return ACCESS_DENIED;
        }

        ControllerDetails controllerDetails = controllerMappingInfoGetter.getControllersDetails(request);
        if (controllerDetails == null) {
            log.warn("Failed to resolve controller for request: {}. Abstaining from decision...", request.getRequestURI());
            return null;
        }

        ControllerAuthDetails controllerAuthDetails = controllerDetails.getAuthDetails();
        Map<String, Set<AccessOp>> controllerPrivileges = controllerAuthDetails.privileges();
        if (controllerPrivileges.isEmpty()) {
            return ACCESS_GRANTED;
        }

        Map<String, Set<AccessOp>> privileges = checkAndRetrieveSource(authentication);
        return resolveAuthorizationDecision(privileges, controllerPrivileges, controllerAuthDetails.joinType());
    }

    private Map<String, Set<AccessOp>> checkAndRetrieveSource(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            throw new IllegalStateException("Authorities collection is null");
        }

        Map<String, Set<AccessOp>> privileges = new HashMap<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (!(grantedAuthority instanceof PrivilegeAuthority privilegeAuthority)) {
                throw new IllegalStateException("Granted authority is not PrivilegeAuthority: " + grantedAuthority);
            }
            privileges.put(
                    privilegeAuthority.getKey(),
                    privilegeAuthority.getAccessOps()
            );
        }

        return privileges;
    }

    private AuthorizationDecision resolveAuthorizationDecision(Map<String, Set<AccessOp>> privileges,
                                                               Map<String, Set<AccessOp>> controllerPrivileges,
                                                               PrivilegeJoinType privilegeJoinType) {
        return switch (privilegeJoinType) {
            case AND -> resolveByAndJoinType(privileges, controllerPrivileges);
            case OR -> resolveByOrJoinType(privileges, controllerPrivileges);
        };
    }

    private AuthorizationDecision resolveByAndJoinType(Map<String, Set<AccessOp>> privileges,
                                                       Map<String, Set<AccessOp>> controllerPrivileges) {
        for (Map.Entry<String, Set<AccessOp>> entry : controllerPrivileges.entrySet()) {
            Set<AccessOp> sourceOpCollection = privileges.get(entry.getKey());
            if (sourceOpCollection == null) {
                return ACCESS_DENIED;
            }

            if (!sourceOpCollection.containsAll(entry.getValue())) {
                return ACCESS_DENIED;
            }
        }

        return ACCESS_GRANTED;
    }

    private AuthorizationDecision resolveByOrJoinType(Map<String, Set<AccessOp>> privileges,
                                                      Map<String, Set<AccessOp>> controllerPrivileges) {
        for (Map.Entry<String, Set<AccessOp>> entry : controllerPrivileges.entrySet()) {
            Set<AccessOp> sourceOpCollection = privileges.get(entry.getKey());
            if (sourceOpCollection != null && sourceOpCollection.containsAll(entry.getValue())) {
                return ACCESS_GRANTED;
            }
        }

        return ACCESS_DENIED;
    }
}
