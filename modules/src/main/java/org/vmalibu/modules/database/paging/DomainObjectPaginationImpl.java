package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;

import java.util.function.Function;

public class DomainObjectPaginationImpl<T extends DomainObject, V> extends AbstractDomainObjectPagination<T, T, V> {

    public DomainObjectPaginationImpl(@NonNull PaginatedDomainObjectRepository<T> dao,
                                      @NonNull Function<T, V> mapper) {
        super(dao, mapper);
    }

    @Override
    protected @NonNull Page<T> getResult(@Nullable Specification<T> specification, @NonNull PageRequest pageRequest) {
        if (specification != null) {
            return dao.findAll(specification, pageRequest);
        } else {
            return dao.findAll(pageRequest);
        }
    }
}
