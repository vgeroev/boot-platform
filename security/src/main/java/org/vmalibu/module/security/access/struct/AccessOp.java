package org.vmalibu.module.security.access.struct;

import lombok.Getter;

@Getter
public enum AccessOp {

    READ(1),
    WRITE(2),
    DELETE(4),
    EXECUTE(8);

    private final int value;
    AccessOp(int value) {
        this.value = value;
    }

}
