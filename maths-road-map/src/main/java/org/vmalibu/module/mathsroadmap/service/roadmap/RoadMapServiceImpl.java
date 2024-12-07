package org.vmalibu.module.mathsroadmap.service.roadmap;

import com.google.common.base.Functions;
import jakarta.persistence.criteria.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.mathsroadmap.database.dao.ArticleDAO;
import org.vmalibu.module.mathsroadmap.database.dao.RoadMapDAO;
import org.vmalibu.module.mathsroadmap.database.dao.RoadMapTreeEdgeDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBArticle;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMap;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBRoadMapTreeEdge;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.module.mathsroadmap.service.article.ArticleDTO;
import org.vmalibu.module.mathsroadmap.service.roadmap.graph.ArticleEdge;
import org.vmalibu.module.mathsroadmap.service.roadmap.graph.ArticleGraphNode;
import org.vmalibu.module.mathsroadmap.service.roadmap.graph.ArticleTreeValidator;
import org.vmalibu.module.mathsroadmap.service.roadmap.list.RoadMapListElement;
import org.vmalibu.module.mathsroadmap.service.roadmap.list.RoadMapPagingRequest;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.module.security.database.dao.UserDAO;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.database.paging.DomainObjectPagination;
import org.vmalibu.modules.database.paging.DomainObjectPaginationImpl;
import org.vmalibu.modules.database.paging.PaginatedDto;
import org.vmalibu.modules.graph.GraphNode;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoadMapServiceImpl implements RoadMapService {

    private final RoadMapDAO roadMapDAO;
    private final RoadMapTreeEdgeDAO roadMapTreeEdgeDAO;
    private final ArticleDAO articleDAO;
    private final UserDAO userDAO;
    private final DomainObjectPagination<DBRoadMap, RoadMapListElement> domainObjectPagination;

    public RoadMapServiceImpl(RoadMapDAO roadMapDAO,
                              RoadMapTreeEdgeDAO roadMapTreeEdgeDAO,
                              ArticleDAO articleDAO,
                              UserDAO userDAO) {
        this.roadMapDAO = roadMapDAO;
        this.roadMapTreeEdgeDAO = roadMapTreeEdgeDAO;
        this.articleDAO = articleDAO;
        this.domainObjectPagination = new DomainObjectPaginationImpl<>(roadMapDAO, RoadMapListElement::from);
        this.userDAO = userDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public @Nullable RoadMapDTO findById(long id) {
        return roadMapDAO.findById(id).map(RoadMapDTO::from).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull RoadMapDTO create(@NonNull String title,
                                      @Nullable String description,
                                      @NonNull UserSource userSource) throws PlatformException {
        if (!StringUtils.hasText(title)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBRoadMap.class, DBRoadMap.DB_TITLE);
        }

        DBUser user = userDAO.checkExistenceAndGet(userSource.getId());

        DBRoadMap roadMap = new DBRoadMap();
        roadMap.setTitle(title.trim());
        roadMap.setDescription(normalizeDescription(description));
        roadMap.setCreator(user);

        return RoadMapDTO.from(roadMapDAO.save(roadMap));
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull RoadMapDTO update(long id,
                                      @NonNull String title,
                                      @Nullable String description,
                                      @NonNull UserSource userSource) throws PlatformException {
        DBRoadMap roadMap = roadMapDAO.checkExistenceAndGet(id);
        validateRoadMapBelongsToUser(roadMap, userSource);

        roadMap.setTitle(title.trim());
        roadMap.setDescription(normalizeDescription(description));

        return RoadMapDTO.from(roadMap);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull PaginatedDto<RoadMapListElement> findAll(@NonNull RoadMapPagingRequest pagingRequest) {
        return domainObjectPagination.findAll(
                pagingRequest,
                buildSpecification(pagingRequest)
        );
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = PlatformException.class)
    public @NonNull RoadMapTreeDTO getTree(long id) throws PlatformException {
        List<DBRoadMapTreeEdge> tree = roadMapTreeEdgeDAO.findTree(id);
        DBRoadMap roadMap;
        if (tree.isEmpty()) {
            roadMap = roadMapDAO.checkExistenceAndGet(id);
            return new RoadMapTreeDTO(RoadMapDTO.from(roadMap), List.of(), List.of());
        } else {
            roadMap = tree.iterator().next().getRoadMap();
        }

        return buildTree(roadMap, tree);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull RoadMapTreeDTO replaceTree(long id,
                                               @NonNull Set<ArticleEdge> articleEdges,
                                               @NonNull UserSource userSource) throws PlatformException {
        DBRoadMap roadMap = roadMapDAO.checkExistenceAndGet(id);
        validateRoadMapBelongsToUser(roadMap, userSource);
        validateThatTree(articleEdges);

        roadMapTreeEdgeDAO.deleteTree(id);

        Set<Long> articleIds = articleEdges.stream()
                .flatMap(articleEdge -> Stream.of(articleEdge.prevId(), articleEdge.nextId()))
                .collect(Collectors.toSet());
        Map<Long, DBArticle> articles = getAndCheckArticles(articleIds).stream()
                .collect(Collectors.toMap(DomainObject::getId, Functions.identity()));

        List<DBRoadMapTreeEdge> roadMapTree = new ArrayList<>(articleEdges.size());
        for (ArticleEdge edge : articleEdges) {
            DBRoadMapTreeEdge roadMapTreeEdge = new DBRoadMapTreeEdge();
            roadMapTreeEdge.setRoadMap(roadMap);
            roadMapTreeEdge.setPrevArticle(articles.get(edge.prevId()));
            roadMapTreeEdge.setNextArticle(articles.get(edge.nextId()));
            roadMapTree.add(roadMapTreeEdge);
        }

        roadMapTreeEdgeDAO.saveAll(roadMapTree);
        return buildTree(roadMap, roadMapTree);
    }

    private void validateThatTree(Set<ArticleEdge> articleEdges) throws PlatformException {
        List<ArticleGraphNode> cycle = ArticleTreeValidator.getFirstCycle(articleEdges);
        if (!cycle.isEmpty()) {
            List<Long> cycleIds = cycle.stream()
                    .map(GraphNode::getNodeKey)
                    .toList();
            throw MathsRoadMapExceptionFactory.buildArticleGraphIsNotATreeException(cycleIds);
        }
    }

    private void validateRoadMapBelongsToUser(DBRoadMap roadMap, UserSource userSource) throws PlatformException {
        if (!Objects.equals(roadMap.getCreator().getId(), userSource.getId())) {
            throw MathsRoadMapExceptionFactory.buildUserDoesNotHaveAccessException(userSource.getUsername());
        }
    }

    private List<DBArticle> getAndCheckArticles(Set<Long> ids) throws PlatformException {
        if (ids.isEmpty()) {
            return List.of();
        }

        List<DBArticle> articles = articleDAO.findAllById(ids);
        if (articles.size() == ids.size()) {
            return articles;
        }

        Set<Long> articleIds = articles.stream()
                .map(DomainObject::getId)
                .collect(Collectors.toSet());
        for (Long id : ids) {
            if (!articleIds.contains(id)) {
                throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBArticle.class, id);
            }
        }

        throw new IllegalStateException("Unreachable statement");
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }

    private static RoadMapTreeDTO buildTree(DBRoadMap roadMap, List<DBRoadMapTreeEdge> roadMapEdges) {
        Map<Long, ArticleDTO> articlesDTO = new HashMap<>();
        List<RoadMapTreeEdgeDTO> roadMapEdgesDTO = new ArrayList<>();
        for (DBRoadMapTreeEdge node : roadMapEdges) {
            DBArticle prevArticle = node.getPrevArticle();
            if (!articlesDTO.containsKey(prevArticle.getId())) {
                articlesDTO.put(prevArticle.getId(), ArticleDTO.from(prevArticle));
            }
            DBArticle nextArticle = node.getNextArticle();
            if (!articlesDTO.containsKey(nextArticle.getId())) {
                articlesDTO.put(nextArticle.getId(), ArticleDTO.from(nextArticle));
            }

            roadMapEdgesDTO.add(
                    new RoadMapTreeEdgeDTO(
                            node.getId(),
                            node.getRoadMap().getId(),
                            prevArticle.getId(),
                            nextArticle.getId()
                    )
            );
        }

        return new RoadMapTreeDTO(
                RoadMapDTO.from(roadMap),
                List.copyOf(articlesDTO.values()),
                roadMapEdgesDTO
        );
    }

    private static Specification<DBRoadMap> buildSpecification(
           RoadMapPagingRequest pagingRequest
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            root.fetch(DBRoadMap.Fields.creator, JoinType.LEFT);

            String titlePrefix = pagingRequest.getTitlePrefix();
            if (titlePrefix != null) {
                predicates.add(getPrefixPredicate(root, cb, DBRoadMap.Fields.title, titlePrefix));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate getPrefixPredicate(
            Root<DBRoadMap> root,
            CriteriaBuilder cb,
            String fieldName,
            String field
    ) {
        Path<String> path = root.get(fieldName);
        return cb.like(path, field + "%");
    }

}
