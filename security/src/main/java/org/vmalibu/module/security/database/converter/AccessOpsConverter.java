
package org.vmalibu.module.security.database.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.access.struct.AccessOpCollection;

import java.util.Set;

// Without autoApply does not work. ;(
@Converter(autoApply = true)
public class AccessOpsConverter implements AttributeConverter<Set<AccessOp>, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Set<AccessOp> attribute) {
        return new AccessOpCollection(attribute.toArray(new AccessOp[0])).getValue();
    }

    @Override
    public Set<AccessOp> convertToEntityAttribute(Integer value) {
        return new AccessOpCollection(value).toOps();
    }
}
