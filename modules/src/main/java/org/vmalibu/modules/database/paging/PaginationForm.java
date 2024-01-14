package org.vmalibu.modules.database.paging;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class PaginationForm {

    public static final String JSON_SORT_DIRECTION = "sortDirection";
    public static final String JSON_PAGE = "page";
    public static final String JSON_PAGE_SIZE = "pageSize";
    public static final String JSON_SORT_FIELD = "sortField";

    public final SortDirection sortDirection;

    public final int page;

    public final int pageSize;

    protected PaginationForm(@NonNull Map<String, String> params) throws PlatformException {
        if (params.containsKey(JSON_SORT_DIRECTION)) {
            sortDirection = parseEnum(SortDirection.class, params, JSON_SORT_DIRECTION);
        } else {
            sortDirection = SortDirection.ASC;
        }

        Integer nullablePage;
        if (params.containsKey(JSON_PAGE)) {
            nullablePage = parseInt(params, JSON_PAGE);
        } else {
            nullablePage = getDefaultPage();
        }

        Integer nullablePageSize;
        if (params.containsKey(JSON_PAGE_SIZE)) {
            nullablePageSize = parseInt(params, JSON_PAGE_SIZE);
        } else {
            nullablePageSize = getDefaultPageSize();
        }

        validate(nullablePage, nullablePageSize);
        this.page = Objects.requireNonNull(nullablePage);
        this.pageSize = Math.min(getMaxPageSize(), Objects.requireNonNull(nullablePageSize));
    }

    protected int getDefaultPage() {
        return 0;
    }

    protected int getDefaultPageSize() {
        return Integer.MAX_VALUE;
    }

    protected int getMaxPageSize() {
        return Integer.MAX_VALUE;
    }

    private static void validate(Integer page, Integer pageSize) throws PlatformException {
        if (page == null || page < 0) {
            throw GeneralExceptionFactory.buildInvalidArgumentException("page must be not null and non negative");
        }

        if (pageSize == null || pageSize < 1) {
            throw GeneralExceptionFactory.buildInvalidArgumentException("pageSize must be not null and greater than 0");
        }
    }

    protected static @Nullable <T> T parseEnum(@NonNull Class<T> enumClass,
                                               @NonNull Map<String, String> params,
                                               @NonNull String fieldName) throws PlatformException {
        String strEnum = params.get(fieldName);
        return Stream.of(enumClass.getEnumConstants())
                .filter(value -> value.toString().equals(strEnum))
                .findFirst()
                .orElseThrow(() -> GeneralExceptionFactory.buildInvalidValueException(fieldName));
    }

    protected static @Nullable Integer parseInt(@NonNull Map<String, String> params,
                                                @NonNull String fieldName) throws PlatformException {
        try {
            return Optional.ofNullable(params.get(fieldName))
                    .map(Integer::valueOf)
                    .orElse(null);
        } catch (NumberFormatException e) {
            throw GeneralExceptionFactory.buildInvalidValueException(fieldName);
        }
    }

}
