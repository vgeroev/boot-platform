package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.*;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.database.domainobject.DomainObject;

@Table(
        name = MathsRoadMapConsts.DB_PREFIX + "article_user_likes"
)
@Entity
@Setter
@Access(value = AccessType.FIELD)
public class DBArticleUserLikes extends DomainObject<DBArticleUserLikes.Id> {

    public static final String DB_FK_USER = "fk_user";
    public static final String DB_FK_ARTICLE = "fk_article";
    public static final String DB_VALUE = "value";

    @Override
    @EmbeddedId
    @Access(value = AccessType.PROPERTY)
    public Id getId() {
        return super.getId();
    }

    @Column(name = DB_VALUE, nullable = false)
    private boolean value;

    public boolean getValue() {
        return value;
    }

    public boolean isLike() {
        return getValue();
    }

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    public static class Id {

        private DBUser user;
        private DBArticle article;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = DB_FK_USER, nullable = false)
        public DBUser getUser() {
            return user;
        }

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = DB_FK_ARTICLE, nullable = false)
        public DBArticle getArticle() {
            return article;
        }
    }
}
