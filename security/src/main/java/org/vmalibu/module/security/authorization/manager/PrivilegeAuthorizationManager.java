package org.vmalibu.module.security.authorization.manager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.access.struct.AccessOpCollection;
import org.vmalibu.module.security.access.struct.PrivilegeAuthority;
import org.vmalibu.module.security.authorization.controller.ControllerAuthDetails;
import org.vmalibu.module.security.authorization.controller.ControllerDetails;
import org.vmalibu.module.security.authorization.controller.ControllerMappingInfoGetter;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeJoinType;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
@AllArgsConstructor
public class PrivilegeAuthorizationManager implements CustomAuthorizationManager {

    protected static final AuthorizationDecision ACCESS_GRANTED = new AuthorizationDecision(true);
    protected static final AuthorizationDecision ACCESS_DENIED = new AuthorizationDecision(false);

    private final ControllerMappingInfoGetter controllerMappingInfoGetter;
    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    public @Nullable AuthorizationDecision check(@NonNull Supplier<Authentication> authenticationSupplier,
                                                 @NonNull HttpServletRequest request) {
        Authentication authentication = authenticationSupplier.get();
        if (!authentication.isAuthenticated()) {
            return ACCESS_DENIED;
        }

        Map<String, AccessOpCollection> privileges = checkAndRetrieveSource(authentication);

        Method handlerMethod = getHandlerMethod(request);
        if (handlerMethod == null) {
            log.warn("Failed to resolve handler method for request: {}. Abstaining from decision...",
                    request.getRequestURI());
            return null;
        }

        ControllerDetails controllerDetails = controllerMappingInfoGetter.getControllersDetails().get(handlerMethod);
        ControllerAuthDetails controllerAuthDetails = controllerDetails.getAuthDetails();
        Map<String, AccessOpCollection> controllerPrivileges = controllerAuthDetails.getPrivileges();
        if (controllerPrivileges.isEmpty()) {
            return ACCESS_GRANTED;
        }

        return resolveAuthorizationDecision(privileges, controllerPrivileges, controllerAuthDetails.getJoinType());
    }

    private Map<String, AccessOpCollection> checkAndRetrieveSource(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            throw new IllegalStateException("Authorities collection is null");
        }

        Map<String, AccessOpCollection> privileges = new HashMap<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (!(grantedAuthority instanceof PrivilegeAuthority privilegeAuthority)) {
                throw new IllegalStateException("Granted authority is not PrivilegeAuthority: " + grantedAuthority);
            }
            privileges.put(
                    privilegeAuthority.getKey(),
                    new AccessOpCollection(privilegeAuthority.getAccessOps().toArray(new AccessOp[0]))
            );
        }

        return privileges;
    }

    private Method getHandlerMethod(HttpServletRequest request) {
        HandlerExecutionChain handler;
        try {
            handler = handlerMapping.getHandler(request);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        if (handler == null || !(handler.getHandler() instanceof HandlerMethod handlerMethod)) {
            return null;
        }

       return handlerMethod.getMethod();
    }

    private AuthorizationDecision resolveAuthorizationDecision(Map<String, AccessOpCollection> privileges,
                                                               Map<String, AccessOpCollection> controllerPrivileges,
                                                               PrivilegeJoinType privilegeJoinType) {
        return switch (privilegeJoinType) {
            case AND -> resolveByAndJoinType(privileges, controllerPrivileges);
            case OR -> resolveByOrJoinType(privileges, controllerPrivileges);
        };
    }

    private AuthorizationDecision resolveByAndJoinType(Map<String, AccessOpCollection> privileges,
                                                       Map<String, AccessOpCollection> controllerPrivileges) {
        for (Map.Entry<String, AccessOpCollection> entry : controllerPrivileges.entrySet()) {
            if (!privileges.containsKey(entry.getKey())) {
                return ACCESS_DENIED;
            }

            AccessOpCollection sourceOpCollection = privileges.get(entry.getKey());
            if (!sourceOpCollection.contains(entry.getValue())) {
                return ACCESS_DENIED;
            }
        }

        return ACCESS_GRANTED;
    }

    private AuthorizationDecision resolveByOrJoinType(Map<String, AccessOpCollection> privileges,
                                                      Map<String, AccessOpCollection> controllerPrivileges) {
        for (Map.Entry<String, AccessOpCollection> entry : controllerPrivileges.entrySet()) {
            if (privileges.containsKey(entry.getKey())) {
                AccessOpCollection sourceOpCollection = privileges.get(entry.getKey());
                if (sourceOpCollection.contains(entry.getValue())) {
                    return ACCESS_GRANTED;
                }
            }
        }

        return ACCESS_DENIED;
    }
}
