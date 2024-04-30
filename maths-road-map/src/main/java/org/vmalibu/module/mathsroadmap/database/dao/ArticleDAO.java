package org.vmalibu.module.mathsroadmap.database.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.modules.database.repository.DomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

@Repository
public interface ArticleDAO extends DomainObjectRepository<DBArticle> {

    @NonNull
    default DBArticle checkExistenceAndGet(long id) throws PlatformException {
        return DomainObjectRepository.super.checkExistenceAndGet(id, DBArticle.class);
    }
}
