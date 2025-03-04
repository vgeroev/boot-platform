package org.vmalibu.module.core.database.domainobject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.vmalibu.module.core.CoreConsts;
import org.vmalibu.modules.database.domainobject.VersionedDomainObject;

@Table(
        name = CoreConsts.DB_PREFIX + "tag"
)
@Entity
@Getter
@Setter
@Access(value = AccessType.FIELD)
@NoArgsConstructor
@FieldNameConstants
public class DBTag extends VersionedDomainObject<Long> {

    public static final String DB_NAME = "name";
    public static final String DB_COLOR = "color";

    @Override
    @Access(AccessType.PROPERTY)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(
            name = "pooled",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(name = OptimizableGenerator.INITIAL_PARAM, value = "1"),
                    @org.hibernate.annotations.Parameter(name = OptimizableGenerator.INCREMENT_PARAM, value = "5"),
                    @org.hibernate.annotations.Parameter(name = OptimizableGenerator.OPT_PARAM, value = "pooled"),
                    @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, value = "_seq")
            }
    )
    public Long getId() {
        return super.getId();
    }

    @Column(name = DB_NAME, nullable = false)
    private String name;

    @Column(name = DB_COLOR, nullable = false)
    private int color;

}

