package org.vmalibu.module.mathsroadmap.database.dao;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBTopicNode;
import org.vmalibu.modules.database.repository.DomainObjectRepository;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Optional;

@Repository
public interface TopicNodeDAO extends DomainObjectRepository<DBTopicNode> {

    @EntityGraph(value = DBTopicNode.ENTITY_GRAPH_FETCH_TOPIC_WITH_ADJACENT_NODES)
    @Query("from DBTopicNode t where t.id = :id")
    Optional<DBTopicNode> fetchByIdWithTopic(@Param("id") long id);

    @NonNull
    default DBTopicNode checkExistenceAndGet(long id) throws PlatformException {
        return DomainObjectRepository.super.checkExistenceAndGet(id, DBTopicNode.class);
    }
}
