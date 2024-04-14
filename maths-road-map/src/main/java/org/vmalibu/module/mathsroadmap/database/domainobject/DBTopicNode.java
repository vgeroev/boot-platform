package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.HashSet;
import java.util.Set;

@Table(
        name = MathsRoadMapConsts.DB_PREFIX + "topic_node"
)
@Entity
@NamedEntityGraph(
        name = DBTopicNode.ENTITY_GRAPH_FETCH_TOPIC_WITH_ADJACENT_NODES,
        attributeNodes = {
                @NamedAttributeNode(DBTopicNode.Fields.topic),
                @NamedAttributeNode(DBTopicNode.Fields.nextNodes),
                @NamedAttributeNode(DBTopicNode.Fields.prevNodes),
        })
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBTopicNode extends IdentityGeneratedDomainObject {

    public static final String ENTITY_GRAPH_FETCH_TOPIC_WITH_ADJACENT_NODES = "EG.DBTopicNode.fetchTopicWithAdjacentNodes";

    public static final String DB_EASINESS_LEVEL = "easiness_level";

    @Column(name = DB_EASINESS_LEVEL, nullable = false)
    private int easinessLevel;

    @OneToOne(mappedBy = DBTopic.Fields.node, cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private DBTopic topic;

    public void setTopic(@NonNull DBTopic topic) {
        this.topic = topic;
        topic.setNode(this);
    }

    @ManyToMany
    @JoinTable(
            name = MathsRoadMapConsts.DB_PREFIX + "topic_tree",
            joinColumns = {
                    @JoinColumn(name = "fk_prev")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "fk_next")
            }
    )
    private Set<DBTopicNode> nextNodes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = MathsRoadMapConsts.DB_PREFIX + "topic_tree",
            joinColumns = {
                    @JoinColumn(name = "fk_next")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "fk_prev")
            }
    )
    private Set<DBTopicNode> prevNodes = new HashSet<>();

}
