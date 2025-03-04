package org.vmalibu.module.security.service.user;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.access.struct.AccessOpCollection;
import org.vmalibu.module.security.database.dao.AccessRoleDAO;
import org.vmalibu.module.security.database.dao.UserDAO;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.*;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final AccessRoleDAO accessRoleDAO;

    @Override
    @Transactional(readOnly = true)
    public @Nullable UserDTO findById(long id) {
        return userDAO.findById(id).map(UserDTO::from).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable UserDTO findByUsername(@NonNull String username) {
        return userDAO.findByUsername(username).map(UserDTO::from).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable UserWithPrivilegesDTO findWithPrivileges(long id) {
        return getUserWithPrivileges(() -> userDAO.findWithPrivileges(id));
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable UserWithPrivilegesDTO findWithPrivileges(@NonNull String username) {
        return getUserWithPrivileges(() -> userDAO.findWithPrivileges(username));
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull UserDTO create(@NonNull String username, @NonNull String password) throws PlatformException {
        checkNotEmpty(DBUser.DB_USERNAME, username);
        checkNotEmpty(DBUser.DB_PASSWORD, password);

        checkUsernameUniqueness(username);

        DBUser user = new DBUser();
        user.setUsername(username);
        user.setPassword(password);

        return UserDTO.from(userDAO.save(user));
    }

    @Transactional(rollbackFor = PlatformException.class)
    @Override
    public void addAccessRoles(long id, @NonNull Set<@NonNull Long> accessRoleIds) throws PlatformException {
        if (accessRoleIds.isEmpty()) {
            return;
        }

        DBUser user = userDAO.findWithAccessRoles(id)
                .orElseThrow(() -> GeneralExceptionFactory.buildNotFoundDomainObjectException(DBUser.class, id));
        for (Long accessRoleId : accessRoleIds) {
            DBAccessRole accessRole = accessRoleDAO.checkExistenceAndGet(accessRoleId);
            user.getAccessRoles().add(accessRole);
        }
    }

    @Transactional(rollbackFor = PlatformException.class)
    @Override
    public void removeAccessRoles(long id, @NonNull Set<@NonNull Long> accessRoleIds) throws PlatformException {
        if (accessRoleIds.isEmpty()) {
            return;
        }

        DBUser user = userDAO.findWithAccessRoles(id)
                .orElseThrow(() -> GeneralExceptionFactory.buildNotFoundDomainObjectException(DBUser.class, id));
        user.getAccessRoles().removeIf(role -> accessRoleIds.contains(role.getId()));
    }

    private void checkUsernameUniqueness(String username) throws PlatformException {
        boolean exists = userDAO.isExistByUsername(username);
        if (exists) {
            throw GeneralExceptionFactory.buildNotUniqueDomainObjectException(DBUser.class, DBUser.DB_USERNAME, username);
        }
    }

    private void checkNotEmpty(String field, String value) throws PlatformException {
        if (!StringUtils.hasText(value)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBUser.class, field);
        }
    }

    private UserWithPrivilegesDTO getUserWithPrivileges(Supplier<Optional<DBUser>> userSupplier) {
        Optional<DBUser> oUser = userSupplier.get();
        if (oUser.isEmpty()) {
            return null;
        }

        DBUser user = oUser.get();
        Map<String, Set<AccessOp>> privileges = mergePrivileges(user.getAccessRoles());

        return new UserWithPrivilegesDTO(UserDTO.from(user), privileges);
    }

    private Map<String, Set<AccessOp>> mergePrivileges(Set<DBAccessRole> accessRoles) {
        Map<String, AccessOpCollection> mergedPrivileges = new HashMap<>();
        for (DBAccessRole accessRole : accessRoles) {
            for (Map.Entry<String, Set<AccessOp>> privilege : accessRole.getPrivileges().entrySet()) {
                mergedPrivileges.compute(privilege.getKey(), (key, accessOpCollection) -> {
                    accessOpCollection = Objects.requireNonNullElseGet(accessOpCollection, AccessOpCollection::new);
                    return accessOpCollection.addOps(privilege.getValue().toArray(new AccessOp[0]));
                });
            }
        }

        Map<String, Set<AccessOp>> result = new HashMap<>(mergedPrivileges.size());
        mergedPrivileges.forEach((k, v) -> result.put(k, v.toOps()));

        return result;
    }
}
