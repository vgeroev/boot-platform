package org.vmalibu.module.mathsroadmap.service.roadmap;

import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;

import java.util.List;

public record RoadMapTreeDTO(RoadMapWithCreatorDTO roadMap,
                             List<ArticleDTO> articles,
                             List<RoadMapTreeEdgeDTO> tree) {
}
