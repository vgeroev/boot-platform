package org.vmalibu.module.mathsroadmap.controller.mathsroadmap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.mathsroadmap.MathsRoadMapConsts;
import org.vmalibu.module.mathsroadmap.service.roadmap.RoadMapDTO;
import org.vmalibu.module.mathsroadmap.service.roadmap.RoadMapService;
import org.vmalibu.module.mathsroadmap.service.roadmap.RoadMapTreeDTO;
import org.vmalibu.module.mathsroadmap.service.roadmap.graph.ArticleEdge;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;

@RestController
@RequestMapping(MathsRoadMapConsts.REST_AUTHORIZED_PREFIX + "/road-map")
@AllArgsConstructor
public class MathsRoadMapAuthorizedController {

    private final RoadMapService roadMapService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoadMapDTO create(
            @RequestBody CreateRequest request,
            final UserSource userSource
    ) throws PlatformException {
        String title = request.title;
        if (!StringUtils.hasText(title)) {
            throw GeneralExceptionFactory.buildEmptyValueException(CreateRequest.JSON_TITLE);
        }

        return roadMapService.create(title, request.description, userSource);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoadMapDTO update(
            @PathVariable("id") long id,
            @RequestBody UpdateRequest request,
            UserSource userSource
    ) throws PlatformException {
        String title = request.title;
        if (!StringUtils.hasText(title)) {
            throw GeneralExceptionFactory.buildEmptyValueException(UpdateRequest.JSON_TITLE);
        }

        return roadMapService.update(id, title, request.description, userSource);
    }

    @PatchMapping("/replace-tree/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoadMapTreeDTO replaceTree(
            @PathVariable("id") long id,
            @RequestBody ReplaceTreeRequest treeRequest,
            UserSource userSource
    ) throws PlatformException {
        return roadMapService.replaceTree(id, treeRequest.tree, userSource);
    }

    @Data
    public static class CreateRequest {

        static final String JSON_TITLE = "title";
        static final String JSON_DESCRIPTION = "description";

        @JsonProperty(JSON_TITLE)
        private String title;

        @JsonProperty(JSON_DESCRIPTION)
        private String description;
    }

    @Data
    public static class UpdateRequest {

        static final String JSON_TITLE = "title";
        static final String JSON_DESCRIPTION = "description";

        @JsonProperty(JSON_TITLE)
        private String title;

        @JsonProperty(JSON_DESCRIPTION)
        private String description;
    }

    @Data
    public static class ReplaceTreeRequest {

        static final String JSON_TREE = "tree";

        @JsonProperty(JSON_TREE)
        private Set<ArticleEdge> tree;
    }
}
