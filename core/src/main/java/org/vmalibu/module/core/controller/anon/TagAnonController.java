package org.vmalibu.module.core.controller.anon;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.core.CoreConsts;
import org.vmalibu.module.core.service.tag.TagDTO;
import org.vmalibu.module.core.service.tag.TagService;
import org.vmalibu.module.core.service.tag.list.TagPagingRequest;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.PaginationForm;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Map;

@RestController
@RequestMapping(CoreConsts.REST_ANON_PREFIX + "/tag")
@AllArgsConstructor
public class TagAnonController {

    private final TagService tagService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<TagDTO> list(
            @RequestParam(required = false) final Map<String, String> params
    ) throws PlatformException {
        TagPaginationForm form = new TagPaginationForm(params);
        return tagService.findAll(
                new TagPagingRequest.Builder(form.page, form.pageSize)
                        .withSearchText(form.searchText)
                        .build()
        );
    }

    public static class TagPaginationForm extends PaginationForm {

        static final String JSON_SEARCH_TEXT = "searchText";

        final String searchText;

        public TagPaginationForm(@NonNull Map<String, String> params) throws PlatformException {
            super(params);
            this.searchText = params.getOrDefault(JSON_SEARCH_TEXT, null);
        }

    }
}
