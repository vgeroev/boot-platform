package org.vmalibu.modules.database.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.utils.function.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Objects;
import java.util.Optional;

@NoRepositoryBean
public interface DomainObjectRepository<T extends DomainObject> extends JpaRepository<T, Long> {

    default @NonNull T checkExistenceAndGet(long id, @NonNull Class<T> entityClass) throws PlatformException {
        Optional<T> oDomainObject = findById(id);
        if (oDomainObject.isEmpty()) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(entityClass, id);
        }

        return oDomainObject.get();
    }

    default void checkUniqueness(@NonNull T domainObject,
                                 @NonNull Supplier<T> domainObjectSupplier,
                                 @NonNull Supplier<? extends PlatformException> exceptionSupplier) throws PlatformException {
        T anotherDomainObject = domainObjectSupplier.get();
        if (anotherDomainObject != null && !Objects.equals(domainObject.getId(), anotherDomainObject.getId())) {
            throw exceptionSupplier.get();
        }
    }
}
