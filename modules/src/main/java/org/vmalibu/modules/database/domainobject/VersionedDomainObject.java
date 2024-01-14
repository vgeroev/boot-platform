package org.vmalibu.modules.database.domainobject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@FieldNameConstants
public class VersionedDomainObject extends DomainObject {

    @Version
    @Column(name = "version")
    private short version;
}
