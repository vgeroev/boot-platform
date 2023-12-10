package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.database.repository.PaginatedDomainObjectRepository;
import org.vmalibu.modules.database.domainobject.DomainObject;

import java.util.function.Function;

public class DefaultDomainObjectPagination<Domain extends DomainObject, Dto> extends DomainObjectPagination<Domain, Dto> {

    public DefaultDomainObjectPagination(@NonNull PaginatedDomainObjectRepository<Domain> dao,
                                         @NonNull Function<Domain, Dto> mapper) {
        super(((spec, pageRequest) -> {
            if (spec != null) {
                return dao.findAll(spec, pageRequest);
            } else {
                return dao.findAll(pageRequest);
            }
        }), mapper);
    }
}
