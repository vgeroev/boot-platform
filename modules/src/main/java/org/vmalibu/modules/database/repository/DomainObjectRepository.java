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
public interface DomainObjectRepository<I, D extends DomainObject<I>> extends JpaRepository<D, I> {

    default @NonNull D checkExistenceAndGet(@NonNull I id, @NonNull Class<D> entityClass) throws PlatformException {
        Optional<D> oDomainObject = findById(id);
        if (oDomainObject.isEmpty()) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(entityClass, id);
        }

        return oDomainObject.get();
    }

    default void checkUniqueness(@NonNull D domainObject,
                                 @NonNull Supplier<D> domainObjectSupplier,
                                 @NonNull Supplier<? extends PlatformException> exceptionSupplier) throws PlatformException {
        D anotherDomainObject = domainObjectSupplier.get();
        if (anotherDomainObject != null && !Objects.equals(domainObject.getId(), anotherDomainObject.getId())) {
            throw exceptionSupplier.get();
        }
    }
}
