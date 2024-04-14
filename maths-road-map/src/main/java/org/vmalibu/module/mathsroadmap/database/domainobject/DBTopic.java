package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.domainobject.VersionedDomainObject;

@Table(
        name = MathsRoadMapConsts.DB_PREFIX + "topic"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBTopic extends VersionedDomainObject {

    public static final String DB_NODE_ID = "id";
    public static final String DB_TITLE = "title";
    public static final String DB_BODY = "body";

    @Override
    @Id
    @Access(value = AccessType.PROPERTY)
    @Column(name = DB_NODE_ID)
    public Long getId() {
        return super.getId();
    }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = DB_NODE_ID)
    private DBTopicNode node;

    @Column(name = DB_TITLE, nullable = false)
    private String title;

    @Column(name = DB_BODY, nullable = false)
    private String body;

}
