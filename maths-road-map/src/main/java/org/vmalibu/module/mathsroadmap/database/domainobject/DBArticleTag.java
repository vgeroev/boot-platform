package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.*;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.domainobject.DomainObject;

@Table(
        name = DBArticleTag.DB_TABLE_NAME
)
@Entity
@Setter
@Access(value = AccessType.FIELD)
public class DBArticleTag extends DomainObject<DBArticleTag.Id> {

    public static final String DB_TABLE_NAME = MathsRoadMapConsts.DB_PREFIX + "article_tag";

    public static final String DB_FK_TAG = "fk_tag";
    public static final String DB_FK_ARTICLE = "fk_article";

    @Override
    @EmbeddedId
    @Access(value = AccessType.PROPERTY)
    public Id getId() {
        return super.getId();
    }

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    public static class Id {

        private DBTag tag;
        private DBArticle article;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = DB_FK_TAG, nullable = false)
        public DBTag getTag() {
            return tag;
        }

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = DB_FK_ARTICLE, nullable = false)
        public DBArticle getArticle() {
            return article;
        }
    }
}
