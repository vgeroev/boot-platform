package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.domainobject.listener.ReadOnlyEntityListener;

@Table(
    name = DBArticleTree.DB_ARTICLE_TREE
)
@Entity
@EntityListeners(ReadOnlyEntityListener.class)
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBArticleTree {

    public static final String DB_ARTICLE_TREE = MathsRoadMapConsts.DB_PREFIX + "article_tree";

    @EmbeddedId
    private ArticleTreeID id;

}
