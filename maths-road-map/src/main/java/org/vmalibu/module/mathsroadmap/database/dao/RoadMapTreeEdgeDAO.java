package org.vmalibu.module.mathsroadmap.database.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMapTreeEdge;
import org.vmalibu.modules.database.repository.DomainObjectRepository;

import java.util.List;

public interface RoadMapTreeEdgeDAO extends DomainObjectRepository<Long, DBRoadMapTreeEdge> {

    @Query("""
        select n from DBRoadMapTreeEdge n
            left join fetch n.roadMap
            left join fetch n.nextArticle
            left join fetch n.prevArticle
            where n.roadMap.id = :roadMapId
    """)
    List<DBRoadMapTreeEdge> findTree(@Param("roadMapId") long roadMapId);

    @Modifying
    @Query("delete from DBRoadMapTreeEdge edge where edge.roadMap.id = :roadMapId")
    void deleteTree(@Param("roadMapId") long roadMapId);

}
