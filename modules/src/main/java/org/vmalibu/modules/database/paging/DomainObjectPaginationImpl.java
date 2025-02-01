package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;

import java.util.function.Function;

public class DomainObjectPaginationImpl<D extends DomainObject<?>, V> extends AbstractDomainObjectPagination<D, D, V> {

    public DomainObjectPaginationImpl(@NonNull PaginatedDomainObjectRepository<?, D> dao,
                                      @NonNull Function<D, V> mapper) {
        super(dao, mapper);
    }

    @Override
    protected @NonNull Page<D> getResult(@Nullable Specification<D> specification, @NonNull PageRequest pageRequest) {
        if (specification != null) {
            return dao.findAll(specification, pageRequest);
        } else {
            return dao.findAll(pageRequest);
        }
    }
}
