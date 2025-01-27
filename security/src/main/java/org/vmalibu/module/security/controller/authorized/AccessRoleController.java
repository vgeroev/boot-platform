package org.vmalibu.module.security.controller.authorized;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(SecurityModuleConsts.REST_AUTHORIZED_PREFIX + "/access-role")
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
    public AccessRoleDTO getAccessRole(@PathVariable("id") long id) {
        return accessRoleService.findById(id);
    }

    @PostMapping("/create")
    @AccessPermission(
            values = @PrivilegeAccess(
                    privilege = AccessRolePrivilege.class,
                    ops = { AccessOp.WRITE }
            )
    )
    @ResponseStatus(HttpStatus.CREATED)
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
    public AccessRoleDTO update(@PathVariable("id") long id, @RequestBody UpdateAccessRoleRequest request) throws PlatformException {
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
    public void remove(@PathVariable("id") long id) throws PlatformException {
        accessRoleService.remove(id);
    }

    @Data
    public static class CreateAccessRoleRequest {

        static final String JSON_NAME = "name";

        @JsonProperty(JSON_NAME)
        private String name;
    }

    @Data
    public static class UpdateAccessRoleRequest {

        static final String JSON_NAME = "name";
        static final String JSON_PRIVILEGES = "privileges";

        private OptionalField<String> name = OptionalField.empty();
        private OptionalField<Map<String, Set<AccessOp>>> privileges = OptionalField.empty();

        @JsonProperty(JSON_NAME)
        public void setName(String name) {
            this.name = OptionalField.of(name);
        }

        @JsonProperty(JSON_PRIVILEGES)
        public void setPrivileges(Map<String, Set<AccessOp>> privileges) {
            this.privileges = OptionalField.of(privileges);
        }
    }
}
