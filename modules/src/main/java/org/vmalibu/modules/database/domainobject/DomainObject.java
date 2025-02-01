package org.vmalibu.modules.database.domainobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@FieldNameConstants
public abstract class DomainObject<T> {

    private T id;

}
