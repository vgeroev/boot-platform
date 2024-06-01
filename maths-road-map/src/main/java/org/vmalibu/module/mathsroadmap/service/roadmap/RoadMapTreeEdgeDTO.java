package org.vmalibu.module.mathsroadmap.service.roadmap;

public record RoadMapTreeEdgeDTO(long id,
                                 long roadMapId,
                                 long prevArticleId,
                                 long nextArticleId) {
}
