package org.vmalibu.module.security.database.domainobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vmalibu.module.security.access.struct.AccessOp;

import java.util.Objects;
import java.util.Set;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DBPrivilege {

    public static final String DB_KEY = "key";
    public static final String DB_VALUE = "value";

    @Column(name = DB_KEY, nullable = false)
    private String key;

    @Column(name = DB_VALUE, nullable = false)
//    @Convert(converter = AccessOpsConverter.class)
    private Set<AccessOp> value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBPrivilege that = (DBPrivilege) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
