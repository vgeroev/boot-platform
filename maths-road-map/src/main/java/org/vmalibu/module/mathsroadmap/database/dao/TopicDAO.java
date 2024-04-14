package org.vmalibu.module.mathsroadmap.database.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBTopic;
import org.vmalibu.modules.database.repository.DomainObjectRepository;

@Repository
public interface TopicDAO extends DomainObjectRepository<DBTopic> {

    @Modifying
    @Query("update DBTopic t set t.title = :title where t.id = :id")
    int updateTitle(@Param("id") long id, @Param("title") String title);

    @Modifying
    @Query("update DBTopic t set t.body = :body where t.id = :id")
    int updateBody(@Param("id") long id, @Param("body") String body);

}
