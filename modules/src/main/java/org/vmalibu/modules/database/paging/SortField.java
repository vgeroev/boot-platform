package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Sort;

public interface SortField {

    @NonNull Sort getSort();
}
