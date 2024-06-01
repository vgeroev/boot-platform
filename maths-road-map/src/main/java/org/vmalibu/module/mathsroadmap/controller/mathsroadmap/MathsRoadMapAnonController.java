package org.vmalibu.module.mathsroadmap.controller.mathsroadmap;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.service.roadmap.RoadMapDTO;
import org.vmalibu.module.mathsroadmap.service.roadmap.RoadMapService;
import org.vmalibu.module.mathsroadmap.service.roadmap.RoadMapTreeDTO;
import org.vmalibu.module.mathsroadmap.service.roadmap.list.RoadMapPagingRequest;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.database.paging.PaginationForm;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Map;

@RestController
@RequestMapping(MathsRoadMapConsts.REST_ANON_PREFIX + "/road-map")
@AllArgsConstructor
public class MathsRoadMapAnonController {

    private final RoadMapService roadMapService;

    @GetMapping("/tree/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoadMapTreeDTO getTree(
            @PathVariable("id") long id
    ) throws PlatformException {
        return roadMapService.getTree(id);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<RoadMapDTO> list(
            @RequestParam(required = false) final Map<String, String> params
    ) throws PlatformException {
        RoadMapPaginationForm form = new RoadMapPaginationForm(params);
        return roadMapService.findAll(
                new RoadMapPagingRequest.Builder(form.page, form.pageSize)
                        .withTitlePrefix(form.titlePrefix)
                        .withCreatorUsernamePrefix(form.creatorUsernamePrefix)
                        .build()
        );
    }

    public static class RoadMapPaginationForm extends PaginationForm {

        static final String JSON_TITLE_PREFIX = "titlePrefix";
        static final String JSON_CREATOR_USERNAME_PREFIX = "creatorUsernamePrefix";

        final String titlePrefix;
        final String creatorUsernamePrefix;

        public RoadMapPaginationForm(@NonNull Map<String, String> params) throws PlatformException {
            super(params);

            this.titlePrefix = params.getOrDefault(JSON_TITLE_PREFIX, null);
            this.creatorUsernamePrefix = params.getOrDefault(JSON_CREATOR_USERNAME_PREFIX, null);
        }

        @Override
        protected int getMaxPageSize() {
            return 1024;
        }
    }
}
