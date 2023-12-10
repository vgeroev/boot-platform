package org.vmalibu.modules.database.paging;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaginatedDto<T> {

    private final List<T> result;
    private final long totalCount;
    private final int page;
    private final int pageSize;
}
