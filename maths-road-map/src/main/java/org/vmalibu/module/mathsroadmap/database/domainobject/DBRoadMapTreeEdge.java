package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.domainobject.SequenceGeneratedDomainObject;

@Table(
    name = MathsRoadMapConsts.DB_PREFIX + "road_map_tree_edge"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBRoadMapTreeEdge extends SequenceGeneratedDomainObject {

    public static final String DB_FK_ROAD_MAP = "fk_road_map";
    public static final String DB_FK_NEXT_ARTICLE = "fk_next_article";
    public static final String DB_FK_PREV_ARTICLE = "fk_prev_article";

    @ManyToOne
    @JoinColumn(name = DB_FK_ROAD_MAP, nullable = false)
    private DBRoadMap roadMap;

    @ManyToOne
    @JoinColumn(name = DB_FK_NEXT_ARTICLE, nullable = false)
    private DBArticle nextArticle;

    @ManyToOne
    @JoinColumn(name = DB_FK_PREV_ARTICLE, nullable = false)
    private DBArticle prevArticle;

}
