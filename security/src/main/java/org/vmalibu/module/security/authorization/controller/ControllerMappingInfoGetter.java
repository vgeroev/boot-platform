package org.vmalibu.module.security.authorization.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.authorization.controller.privilege.AccessPermission;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeAccess;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeJoinType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ControllerMappingInfoGetter {

    private Map<Method, ControllerDetails> controllersDetails;

    private final List<RequestMappingHandlerMapping> handlerMappings;

    @Autowired
    public ControllerMappingInfoGetter(@NonNull List<RequestMappingHandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
        this.controllersDetails = new HashMap<>();
    }

    public @Nullable ControllerDetails getControllersDetails(@NonNull HttpServletRequest request) {
        Method handlerMethod = getHandlerMethod(request);
        if (handlerMethod == null) {
            return null;
        }
        return controllersDetails.get(handlerMethod);
    }

    private Method getHandlerMethod(HttpServletRequest request) {
        for (RequestMappingHandlerMapping handlerMapping : handlerMappings) {
            HandlerExecutionChain handler;
            try {
                handler = handlerMapping.getHandler(request);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

            if (handler != null) {
                if (handler.getHandler() instanceof HandlerMethod handlerMethod) {
                    return handlerMethod.getMethod();
                }
                return null;
            }
        }

        return null;
    }

    @PostConstruct
    private void init() {
        Map<Method, ControllerDetails> result = new HashMap<>();
        for (RequestMappingHandlerMapping handlerMapping : handlerMappings) {
            result.putAll(initFrom(handlerMapping));
        }
        controllersDetails = Collections.unmodifiableMap(result);
    }

    private Map<Method, ControllerDetails> initFrom(RequestMappingHandlerMapping handlerMapping) {
        Map<Method, ControllerDetails> result = new HashMap<>();

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();

            ControllerDetails.ControllerDetailsBuilder builder = ControllerDetails.builder();

            // Auth
            if (method.isAnnotationPresent(AccessPermission.class)) {
                AccessPermission accessPermissionAnn = method.getAnnotation(AccessPermission.class);

                List<BasicControllerAuth> basicControllersAuth = new ArrayList<>();
                for (PrivilegeAccess privilegeAccessAnn : accessPermissionAnn.values()) {
                    BasicControllerAuth basicControllerAuth = validateAndResolveAuthDetails(
                            method, privilegeAccessAnn.privilege(), privilegeAccessAnn.ops());
                    if (basicControllerAuth != null) {
                        basicControllersAuth.add(basicControllerAuth);
                    }
                }

                builder.authDetails(
                        new ControllerAuthDetails(
                                accessPermissionAnn.joinType(),
                                getPrivileges(basicControllersAuth)
                        )
                );
            } else {
                builder.authDetails(new ControllerAuthDetails(PrivilegeJoinType.AND, Map.of()));
            }

            result.put(method, builder.build());
        }

        return result;
    }

    private BasicControllerAuth validateAndResolveAuthDetails(
            Method method,
            Class<? extends AbstractPrivilege> privilegeClazz,
            AccessOp[] ops) {
        if (privilegeClazz != AbstractPrivilege.class) {
            int modifiers = privilegeClazz.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                throw new IllegalStateException("Cannot instantiate privilege: " + privilegeClazz.getSimpleName());
            }

            AbstractPrivilege privilege;
            try {
                Constructor<? extends AbstractPrivilege> constructor =
                        privilegeClazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                privilege = constructor.newInstance();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }

            if (!privilege.getAccessOps().containsAll(Set.of(ops))) {
                throw new IllegalStateException("Privilege " + privilege.getKey() + " doesn't have such access operations: "
                        + Arrays.toString(ops));
            }

            return new BasicControllerAuth(privilege, ops);
        } else if (ops != null && ops.length != 0) {
            throw new IllegalStateException("There is no privilege, but there are access operations (method = " + method.getName() + ")");
        } else {
            return null;
        }
    }

    private Map<String, Set<AccessOp>> getPrivileges(List<BasicControllerAuth> basicControllerAuths) {
        return basicControllerAuths.stream()
                .collect(Collectors.toMap(
                        BasicControllerAuth::getKey,
                        BasicControllerAuth::getAccessOps,
                        (ops1 , ops2) -> {
                            EnumSet<AccessOp> accessOps = EnumSet.noneOf(AccessOp.class);
                            accessOps.addAll(ops1);
                            accessOps.addAll(ops2);
                            return accessOps;
                        })
                );
    }

    @AllArgsConstructor
    @Getter
    private static class BasicControllerAuth {

        private AbstractPrivilege privilege;
        private AccessOp[] accessOps;

        public String getKey() {
            return privilege.getKey();
        }

        public Set<AccessOp> getAccessOps() {
            return Set.of(accessOps);
        }

    }

}
