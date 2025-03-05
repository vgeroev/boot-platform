package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.Date;
import java.util.Set;

@Table(
        name = DBArticle.DB_TABLE_NAME
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@FieldNameConstants
@DynamicUpdate
public class DBArticle extends IdentityGeneratedDomainObject {

    public static final String DB_TABLE_NAME = MathsRoadMapConsts.DB_PREFIX + "article";

    public static final String DB_CREATED_AT = "created_at";
    public static final String DB_UPDATED_AT = "updated_at";
    public static final String DB_CREATOR_ID = "creator_id";
    public static final String DB_TITLE = "title";
    public static final String DB_DESCRIPTION = "description";
    public static final String DB_LIKES = "likes";
    public static final String DB_DISLIKES = "dislikes";
    public static final String DB_TAG_IDS = "tag_ids";

    @OneToOne(mappedBy = DBArticleLatex.Fields.article, cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private DBArticleLatex articleLatex;

    @CreatedDate
    @Column(name = DB_CREATED_AT, nullable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = DB_UPDATED_AT)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DB_CREATOR_ID, nullable = false)
    private DBUser creator;

    @Column(name = DB_TITLE, nullable = false)
    private String title;

    @Column(name = DB_DESCRIPTION)
    private String description;

    @Column(name = DB_LIKES, nullable = false)
    private int likes;

    @Column(name = DB_DISLIKES, nullable = false)
    private int dislikes;

    @Column(name = DB_TAG_IDS, nullable = false)
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] tagIds = new Long[0];

    @ManyToMany
    @JoinTable(
            name = DBArticleTag.DB_TABLE_NAME,
            joinColumns = {
                    @JoinColumn(name = DBArticleTag.DB_FK_ARTICLE)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = DBArticleTag.DB_FK_TAG)
            }
    )
    private Set<DBTag> tags;

    public void setArticleLatex(@NonNull DBArticleLatex articleLatex) {
        this.articleLatex = articleLatex;
        articleLatex.setArticle(this);
    }

}
