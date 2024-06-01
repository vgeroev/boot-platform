package org.vmalibu.module.mathsroadmap.service.roadmap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.vmalibu.module.mathsroadmap.service.roadmap.graph.ArticleEdge;
import org.vmalibu.module.mathsroadmap.service.roadmap.list.RoadMapPagingRequest;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Set;

public interface RoadMapService {

    @Nullable RoadMapDTO findById(long id);

    @NonNull RoadMapDTO create(@NonNull String title,
                               @Nullable String description,
                               @NonNull UserSource userSource) throws PlatformException;

    @NonNull PaginatedDto<RoadMapDTO> findAll(@NonNull RoadMapPagingRequest pagingRequest);

    @NonNull RoadMapDTO update(long id,
                               @NonNull String title,
                               @Nullable String description,
                               @NonNull UserSource userSource) throws PlatformException;

    @NonNull RoadMapTreeDTO getTree(long id) throws PlatformException;

    @NonNull RoadMapTreeDTO replaceTree(long id,
                                        @NonNull Set<ArticleEdge> articleEdges,
                                        @NonNull UserSource userSource) throws PlatformException;
}
