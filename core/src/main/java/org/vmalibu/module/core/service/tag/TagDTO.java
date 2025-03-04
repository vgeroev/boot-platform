package org.vmalibu.module.core.service.tag;

import lombok.Builder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.core.database.domainobject.DBTag;
import org.vmalibu.module.core.utils.HexColorUtils;

@Builder
public record TagDTO(long id, @NonNull String name, @NonNull String hexColor) {

    public static TagDTO from(DBTag tag) {
        if (tag == null) {
            return null;
        }

        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .hexColor(HexColorUtils.getPaddedHex(tag.getColor()))
                .build();
    }
}
