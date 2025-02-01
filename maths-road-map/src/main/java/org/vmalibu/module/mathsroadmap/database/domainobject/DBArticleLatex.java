package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.domainobject.VersionedDomainObject;

@Table(
        name = MathsRoadMapConsts.DB_PREFIX + "article_latex"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBArticleLatex extends VersionedDomainObject<Long> {

    public static final String DB_ARTICLE_ID = "id";
    public static final String DB_LATEX = "latex";
    public static final String DB_CONFIGURATION = "configuration";

    @Override
    @Id
    @Access(value = AccessType.PROPERTY)
    @Column(name = DB_ARTICLE_ID)
    public Long getId() {
        return super.getId();
    }

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = DB_ARTICLE_ID)
    private DBArticle article;

    @Column(name = DB_LATEX, nullable = false)
    private String latex;

    @Column(name = DB_CONFIGURATION)
    private String configuration;

}
