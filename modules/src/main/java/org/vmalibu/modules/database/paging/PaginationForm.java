package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;

import java.util.Map;

public class PaginationForm {

    public static final String JSON_SORT_DIRECTION = "sortDirection";
    public static final String JSON_PAGE = "page";
    public static final String JSON_PAGE_SIZE = "page_size";
    public static final String JSON_SORT_FIELD = "sortField";

    public final SortDirection sortDirection;

    public final Integer page;

    public final Integer pageSize;

    protected PaginationForm(@NonNull Map<String, String> params) throws PlatformException {
        if (params.containsKey(JSON_SORT_DIRECTION)) {
            sortDirection = SortDirection.valueOf(params.get(JSON_SORT_DIRECTION));
        } else {
            sortDirection = SortDirection.ASC;
        }

        if (params.containsKey(JSON_PAGE)) {
            page = Integer.valueOf(params.get(JSON_PAGE));
        } else {
            page = 0;
        }

        if (params.containsKey(JSON_PAGE_SIZE)) {
            pageSize = Integer.valueOf(params.get(JSON_PAGE_SIZE));
        } else {
            pageSize = null;
        }

        validate();
    }

    private void validate() throws PlatformException {
        if (page == null || page < 0) {
            throw GeneralExceptionBuilder.buildInvalidArgumentException("page must be not null and non negative");
        }

        if (pageSize != null && pageSize < 1) {
            throw GeneralExceptionBuilder.buildInvalidArgumentException("pageSize must be > 0");
        }

        if (page > 0 && pageSize == null) {
            throw GeneralExceptionBuilder.buildInvalidArgumentException("If page > 0 then pageSize must be not null");
        }
    }

}
