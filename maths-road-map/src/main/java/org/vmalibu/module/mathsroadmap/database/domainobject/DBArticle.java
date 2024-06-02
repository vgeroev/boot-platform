package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.modules.database.domainobject.IdentityGeneratedDomainObject;

import java.util.Date;

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
    public static final String DB_TITLE = "title";
    public static final String DB_DESCRIPTION = "description";

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

    @Column(name = DB_TITLE, nullable = false)
    private String title;

    @Column(name = DB_DESCRIPTION)
    private String description;

    public void setArticleLatex(@NonNull DBArticleLatex articleLatex) {
        this.articleLatex = articleLatex;
        articleLatex.setArticle(this);
    }

}
