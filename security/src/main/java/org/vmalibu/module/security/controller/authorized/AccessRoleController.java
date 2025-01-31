package org.vmalibu.module.security.controller.authorized;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.authorization.controller.privilege.AccessPermission;
import org.vmalibu.module.security.authorization.controller.privilege.PrivilegeAccess;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.module.security.service.accessrole.AccessRoleDTO;
import org.vmalibu.module.security.service.accessrole.AccessRoleService;
import org.vmalibu.module.security.service.accessrole.AccessRoleWithPrivilegesDTO;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(SecurityModuleConsts.REST_AUTHORIZED_PREFIX + "/access-role")
@Tag(name = "Access Roles", description = "Endpoints for managing access roles")
@AllArgsConstructor
public class AccessRoleController {

    private final AccessRoleService accessRoleService;

    @GetMapping("/{id}")
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = AccessRolePrivilege.class,
                    ops = { AccessOp.READ }
            )
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get access role by ID",
            description = "(AccessRolePrivilege: READ) Retrieves an access role based on the provided role ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access role retrieved successfully",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient privileges"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Access role not found"
                    )
            }
    )
    public AccessRoleWithPrivilegesDTO getAccessRole(@Parameter(description = "ID of the access role") @PathVariable("id") long id) {
        return accessRoleService.findWithPrivileges(id);
    }

    @PostMapping("/create")
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = AccessRolePrivilege.class,
                    ops = { AccessOp.WRITE }
            )
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new access role",
            description = "(AccessRolePrivilege: WRITE) Creates a new access role with the specified name.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Access role created successfully",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient privileges"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Invalid input - name is empty"
                    )
            }
    )
    public AccessRoleDTO create(@RequestBody CreateAccessRoleRequest request) throws PlatformException {
        if (!StringUtils.hasText(request.name)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBAccessRole.class, DBAccessRole.Fields.name);
        }

        return accessRoleService.create(request.name.trim());
    }

    @PatchMapping("/update/{id}")
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = AccessRolePrivilege.class,
                    ops = { AccessOp.WRITE }
            )
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update an existing access role",
            description = "(AccessRolePrivilege: WRITE) Updates an access role's name and privileges.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access role updated successfully",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient privileges"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "name/privileges is empty or there is no access role by such ID"
                    )
            }
    )
    public AccessRoleDTO update(@Parameter(description = "ID of the access role") @PathVariable("id") long id, @RequestBody UpdateAccessRoleRequest request) throws PlatformException {
        if (request.name.isPresent()) {
            String name = request.name.get();
            if (!StringUtils.hasText(name)) {
                throw GeneralExceptionFactory.buildEmptyValueException(DBAccessRole.class, DBAccessRole.Fields.name);
            }
            request.name = OptionalField.of(name.trim());
        }

        if (request.privileges.isPresent() && request.privileges.get() == null) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBAccessRole.class, DBAccessRole.Fields.privileges);
        }

        return accessRoleService.update(id, request.name, request.privileges);
    }

    @DeleteMapping("/remove/{id}")
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = AccessRolePrivilege.class,
                    ops = { AccessOp.DELETE }
            )
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete an access role",
            description = "(AccessRolePrivilege: DELETE) Removes an access role from the admin by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Access role removed successfully"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient privileges"
                    )
            }
    )
    public void remove(@Parameter(description = "ID of the access role") @PathVariable("id") long id) throws PlatformException {
        accessRoleService.remove(id);
    }

    @Data
    public static class CreateAccessRoleRequest {

        static final String JSON_NAME = "name";

        @Schema(description = "Name of the new access role", example = "Admin", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty(JSON_NAME)
        private String name;
    }

    @Data
    public static class UpdateAccessRoleRequest {

        static final String JSON_NAME = "name";
        static final String JSON_PRIVILEGES = "privileges";

        private OptionalField<String> name = OptionalField.empty();
        private OptionalField<Map<String, Set<AccessOp>>> privileges = OptionalField.empty();

        @Schema(
                description = "Updated name of the access role",
                example = "Admin",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                type = "string"
        )
        @JsonProperty(JSON_NAME)
        public void setName(String name) {
            this.name = OptionalField.of(name);
        }

        @JsonProperty(JSON_PRIVILEGES)
        @Schema(
                description = "Updated privileges for the access role",
                example = "{\"privilege_key\": [\"READ\",\"WRITE\"], \"another_privilege_key\": [\"EXECUTE\"]}",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        public void setPrivileges(Map<String, Set<AccessOp>> privileges) {
            this.privileges = OptionalField.of(privileges);
        }
    }
}
