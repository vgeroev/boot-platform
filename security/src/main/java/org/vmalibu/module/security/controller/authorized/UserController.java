package org.vmalibu.module.security.controller.authorized;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

@RequestMapping(SecurityModuleConsts.REST_AUTHORIZED_PREFIX + "/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/logged-in")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getLoggedInUser(UserSource userSource) throws PlatformException {
        long id = userSource.getId();
        UserDTO user = userService.findById(id);
        if (user == null) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBUser.class, id);
        }
        return user;
    }
}
