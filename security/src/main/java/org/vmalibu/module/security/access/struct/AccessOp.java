package org.vmalibu.module.security.access.struct;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Defines access operations")
public enum AccessOp {

    @Schema(description = "Read access")
    READ(1),
    @Schema(description = "Write access")
    WRITE(2),
    @Schema(description = "Delete access")
    DELETE(4),
    @Schema(description = "Execute access")
    EXECUTE(8);

    private final int value;
    AccessOp(int value) {
        this.value = value;
    }

}
