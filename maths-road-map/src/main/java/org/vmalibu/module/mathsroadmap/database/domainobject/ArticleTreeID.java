package org.vmalibu.module.mathsroadmap.database.domainobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ArticleTreeID implements Serializable {

    @Column(name = "fk_prev")
    private long fkPrev;

    @Column(name = "fk_next")
    private long fkNext;

}
