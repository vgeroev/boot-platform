package org.vmalibu.module.security.authorization.manager;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationManager;

public interface CustomAuthorizationManager extends AuthorizationManager<HttpServletRequest> {
}
