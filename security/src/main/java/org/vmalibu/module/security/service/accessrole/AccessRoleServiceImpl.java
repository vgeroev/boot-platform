package org.vmalibu.module.security.service.accessrole;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.database.dao.AccessRoleDAO;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.module.security.database.domainobject.DBPrivilege;
import org.vmalibu.module.security.exception.SecurityExceptionFactory;
import org.vmalibu.module.security.service.privilege.PrivilegeGetter;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class AccessRoleServiceImpl implements AccessRoleService {

    private final AccessRoleDAO accessRoleDAO;
    private final PrivilegeGetter privilegeGetter;

    @Override
    @Transactional(readOnly = true)
    public @Nullable AccessRoleDTO findById(long id) {
        return accessRoleDAO.findById(id).map(AccessRoleDTO::from).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull AccessRoleDTO create(@NonNull String name) throws PlatformException {
        checkNotEmpty(name, DBAccessRole.Fields.name);
        checkNameUniqueness(name);

        DBAccessRole accessRole = new DBAccessRole();
        accessRole.setName(name);
        accessRoleDAO.save(accessRole);

        return AccessRoleDTO.from(accessRole);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull AccessRoleDTO update(long id,
                                         @NonNull OptionalField<String> name,
                                         @NonNull OptionalField<Map<String, Set<AccessOp>>> privileges) throws PlatformException {
        DBAccessRole accessRole = accessRoleDAO.checkExistenceAndGet(id);

        if (name.isPresent()) {
            String newName = name.get();
            checkNotEmpty(newName, DBAccessRole.Fields.name);
            checkNameUniqueness(newName);

            accessRole.setName(newName);
        }

        if (privileges.isPresent()) {
            Map<String, Set<AccessOp>> newPrivileges = privileges.get();
            if (newPrivileges == null) {
                throw GeneralExceptionFactory.buildEmptyValueException(DBAccessRole.class, DBAccessRole.Fields.privileges);
            }
            checkPrivileges(newPrivileges);

            Set<DBPrivilege> dbPrivileges = new HashSet<>(newPrivileges.size());
            for (Map.Entry<String, Set<AccessOp>> entry : newPrivileges.entrySet()) {
                dbPrivileges.add(new DBPrivilege(entry.getKey(), entry.getValue()));
            }

            accessRole.setPrivileges(dbPrivileges);
        }

        return AccessRoleDTO.from(accessRole);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void remove(long id) {
        accessRoleDAO.deleteById(id);
    }

    private void checkNameUniqueness(String name) throws PlatformException {
        boolean exists = accessRoleDAO.isExistByName(name);
        if (exists) {
            throw GeneralExceptionFactory.buildNotUniqueDomainObjectException(DBAccessRole.class, DBAccessRole.Fields.name, name);
        }
    }

    private void checkNotEmpty(String value, String fieldName) throws PlatformException {
        if (!StringUtils.hasText(value)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBAccessRole.class, fieldName);
        }
    }

    private void checkPrivileges(Map<String, Set<AccessOp>> privileges) throws PlatformException {
        Map<String, AbstractPrivilege> availablePrivileges = privilegeGetter.getAvailablePrivileges();
        for (Map.Entry<String, Set<AccessOp>> entry : privileges.entrySet()) {
            String key = entry.getKey();
            Set<AccessOp> accessOps = entry.getValue();

            if (accessOps.isEmpty()) {
                throw SecurityExceptionFactory.buildNoPrivilegeAccessOpsException(key);
            }

            AbstractPrivilege existingPrivilege = availablePrivileges.get(key);
            if (existingPrivilege == null) {
                throw SecurityExceptionFactory.buildInvalidPrivilegeKeyException(key);
            }

            if (!existingPrivilege.getAccessOpCollection().contains(accessOps)) {
                throw SecurityExceptionFactory.buildInvalidPrivilegeAccessOpsException();
            }
        }
    }
}
