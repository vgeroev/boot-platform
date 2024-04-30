package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.database.converter.AbstractionLevelConverter;
import org.vmalibu.module.mathsroadmap.service.article.AbstractionLevel;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.HashSet;
import java.util.Set;

@Table(
        name = MathsRoadMapConsts.DB_PREFIX + "article"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBArticle extends IdentityGeneratedDomainObject {

    public static final String DB_ABSTRACTION_LEVEL = "abstraction_level";
    public static final String DB_TITLE = "title";

    @OneToOne(mappedBy = DBArticleLatex.Fields.article, cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private DBArticleLatex articleLatex;

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
