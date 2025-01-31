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
import org.vmalibu.module.security.exception.SecurityExceptionFactory;
import org.vmalibu.module.security.service.privilege.PrivilegeGetter;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

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
    @Transactional(readOnly = true)
    public @Nullable AccessRoleWithPrivilegesDTO findWithPrivileges(long id) {
        return accessRoleDAO.findWithPrivileges(id).map(AccessRoleWithPrivilegesDTO::from).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull AccessRoleDTO create(@NonNull String name) throws PlatformException {
        checkNotEmpty(name, DBAccessRole.Fields.name);
        checkNameUniqueness(name);

        DBAccessRole accessRole = new DBAccessRole();
        accessRole.setName(name);
        accessRole.setAdmin(false);
        accessRoleDAO.save(accessRole);

        return AccessRoleDTO.from(accessRole);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    // Need batching insert
    public @NonNull AccessRoleDTO update(long id,
                                         @NonNull OptionalField<String> name,
                                         @NonNull OptionalField<Map<String, Set<AccessOp>>> privileges) throws PlatformException {
        DBAccessRole accessRole = accessRoleDAO.checkExistenceAndGet(id);
        checkNotAdmin(accessRole);

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

            accessRole.setPrivileges(newPrivileges);
        }

        return AccessRoleDTO.from(accessRole);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void remove(long id) throws PlatformException {
        DBAccessRole accessRole = accessRoleDAO.checkExistenceAndGet(id);
        checkNotAdmin(accessRole);
        accessRoleDAO.delete(accessRole);
    }

    private void checkNotAdmin(DBAccessRole accessRole) throws PlatformException {
        if (accessRole.isAdmin()) {
            throw GeneralExceptionFactory.buildUnmodifiableDomainObjectException(DBAccessRole.class, accessRole.getId());
        }
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

            if (!existingPrivilege.getAccessOps().containsAll(accessOps)) {
                throw SecurityExceptionFactory.buildInvalidPrivilegeAccessOpsException();
            }
        }
    }
}
