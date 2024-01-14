package org.vmalibu.modules.database.domainobject;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

@MappedSuperclass
public abstract class SequenceGeneratedDomainObject extends VersionedDomainObject {

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pooled")
    @GenericGenerator(
            name = "pooled",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @Parameter(name = OptimizableGenerator.INITIAL_PARAM, value = "1"),
                    @Parameter(name = OptimizableGenerator.INCREMENT_PARAM, value = "3"),
                    @Parameter(name = OptimizableGenerator.OPT_PARAM, value = "pooled"),
                    @Parameter(name = SequenceStyleGenerator.CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, value = "_seq")
            }
    )
    public Long getId() {
        return super.getId();
    }
}
