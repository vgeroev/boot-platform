package org.vmalibu.module.security.controller.authorized;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.authorization.controller.privilege.AccessPermission;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeAccess;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

@RestController
@RequestMapping(SecurityModuleConsts.REST_AUTHORIZED_PREFIX + "/user")
@Tag(name = "User Management", description = "Endpoints for managing users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/logged-in")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get the currently logged-in user",
            description = "Retrieves details of the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User details retrieved successfully",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public UserDTO getLoggedInUser(@Parameter(hidden = true) UserSource userSource) throws PlatformException {
        long id = userSource.getId();
        UserDTO user = userService.findById(id);
        if (user == null) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBUser.class, id);
        }
        return user;
    }

    @PatchMapping("/{id}/add-access-role/{accessRoleId}")
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = UserPrivilege.class,
                    ops = AccessOp.WRITE
            )
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Assign an access role to a user",
            description = "(UserPrivilege: WRITE) Grants a specified access role to the given user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access role added successfully"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient privileges"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User or access role not found"
                    )
            }
    )
    public void addAccessRole(@PathVariable("id") long id, @PathVariable("accessRoleId") long accessRoleId) throws PlatformException {
        userService.addAccessRole(id, accessRoleId);
    }

    @PatchMapping("/{id}/remove-access-role/{accessRoleId}")
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = UserPrivilege.class,
                    ops = AccessOp.WRITE
            )
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Remove an access role from a user",
            description = "(UserPrivilege: WRITE) Revokes a specified access role from the given user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access role removed successfully"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient privileges"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User or access role not found"
                    )
            }
    )
    public void removeAccessRole(@PathVariable("id") long id, @PathVariable("accessRoleId") long accessRoleId) throws PlatformException {
        userService.removeAccessRole(id, accessRoleId);
    }
}
