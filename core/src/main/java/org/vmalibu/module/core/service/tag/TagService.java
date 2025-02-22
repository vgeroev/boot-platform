package org.vmalibu.module.core.service.tag;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.core.service.tag.list.TagPagingRequest;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;

public interface TagService {

    @Nullable TagDTO findById(long id);

    @NonNull PaginatedDto<TagDTO> findAll(@NonNull TagPagingRequest request);

    @NonNull TagDTO create(@NonNull String name, int color) throws PlatformException;

    void remove(@NonNull Set<@NonNull Long> ids);
}
