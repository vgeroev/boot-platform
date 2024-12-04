package org.vmalibu.module.security.service.user;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.security.database.dao.UserDAO;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

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
    @Transactional
    public @NonNull UserDTO create(@NonNull String username, @NonNull String password) throws PlatformException {
        checkNotEmpty(DBUser.DB_USERNAME, username);
        checkNotEmpty(DBUser.DB_PASSWORD, password);

        checkUsernameUniqueness(username);

        DBUser user = new DBUser();
        user.setUsername(username);
        user.setPassword(password);

        return UserDTO.from(userDAO.save(user));
    }

    private void checkUsernameUniqueness(String username) throws PlatformException {
        Optional<DBUser> byUsername = userDAO.findByUsername(username);
        if (byUsername.isPresent()) {
            throw GeneralExceptionFactory.buildNotUniqueDomainObjectException(DBUser.class, DBUser.DB_USERNAME, username);
        }
    }

    private void checkNotEmpty(String field, String value) throws PlatformException {
        if (!StringUtils.hasText(value)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBUser.class, field);
        }
    }
}
