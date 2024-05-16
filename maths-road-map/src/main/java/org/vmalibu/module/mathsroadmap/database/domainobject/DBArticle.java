package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.database.converter.AbstractionLevelConverter;
import org.vmalibu.module.mathsroadmap.service.article.AbstractionLevel;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Table(
        name = MathsRoadMapConsts.DB_PREFIX + "article"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@FieldNameConstants
public class DBArticle extends IdentityGeneratedDomainObject {

    public static final String DB_CREATED_AT = "created_at";
    public static final String DB_UPDATED_AT = "updated_at";
    public static final String DB_CREATOR_USERNAME = "creator_username";
    public static final String DB_ABSTRACTION_LEVEL = "abstraction_level";
    public static final String DB_TITLE = "title";

    @OneToOne(mappedBy = DBArticleLatex.Fields.article, cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private DBArticleLatex articleLatex;

    @CreatedDate
    @Column(name = DB_CREATED_AT, nullable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = DB_UPDATED_AT)
    private Date updatedAt;

    @Column(name = DB_CREATOR_USERNAME, nullable = false)
    private String creatorUsername;

    @Column(name = DB_ABSTRACTION_LEVEL, nullable = false)
    @Convert(converter = AbstractionLevelConverter.class)
    private AbstractionLevel abstractionLevel;

    @Column(name = DB_TITLE, nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = DBArticleTree.DB_ARTICLE_TREE,
            joinColumns = {
                    @JoinColumn(name = "fk_prev")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "fk_next")
            }
    )
    private Set<DBArticle> nextArticles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = DBArticleTree.DB_ARTICLE_TREE,
            joinColumns = {
                    @JoinColumn(name = "fk_next")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "fk_prev")
            }
    )
    private Set<DBArticle> prevArticles = new HashSet<>();

    public void setArticleLatex(@NonNull DBArticleLatex articleLatex) {
        this.articleLatex = articleLatex;
        articleLatex.setArticle(this);
    }

}
