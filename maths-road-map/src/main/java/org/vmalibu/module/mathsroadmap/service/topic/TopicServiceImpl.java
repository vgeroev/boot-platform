package org.vmalibu.module.mathsroadmap.service.topic;

import com.google.common.graph.Traverser;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vmalibu.module.mathsroadmap.database.dao.TopicDAO;
import org.vmalibu.module.mathsroadmap.database.dao.TopicNodeDAO;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBTopicNode;
import org.vmalibu.module.mathsroadmap.database.domainobject.DBTopic;
import org.vmalibu.module.mathsroadmap.exception.MathsRoadMapExceptionFactory;
import org.vmalibu.modules.database.domainobject.DomainObject;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicDAO topicDAO;
    private final TopicNodeDAO topicNodeDAO;

    @Override
    @Transactional(readOnly = true)
    public @Nullable TopicDTO findTopic(long id) {
        Optional<DBTopicNode> oTopicNode = topicNodeDAO.fetchByIdWithTopic(id);
        return oTopicNode.map(TopicDTO::from).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull TopicDTO create(@NonNull String title,
                                    @NonNull String body,
                                    int easinessLevel,
                                    @NonNull Set<Long> prevNodeIds,
                                    @NonNull Set<Long> nextNodeIds) throws PlatformException {
        checkTopic(title, body);

        Set<DBTopicNode> prevNodes = getTopics(prevNodeIds);
        Set<DBTopicNode> nextNodes = getTopics(nextNodeIds);

        checkNode(easinessLevel, prevNodes, nextNodes);

        DBTopic topic = new DBTopic();
        topic.setTitle(title);
        topic.setBody(body);

        DBTopicNode topicNode = new DBTopicNode();
        topicNode.setTopic(topic);
        topicNode.setEasinessLevel(easinessLevel);
        topicNode.setPrevNodes(prevNodes);
        topicNode.setNextNodes(nextNodes);

        topicNodeDAO.save(topicNode);
        return TopicDTO.from(topicNode.getTopic());
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void addNodes(long id,
                         @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                         @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException {
        DBTopicNode topicNode = topicNodeDAO.checkExistenceAndGet(id);
        if (prevNodeIds.isPresent() || nextNodeIds.isPresent()) {
            Set<DBTopicNode> prevNodes = topicNode.getPrevNodes();
            Set<DBTopicNode> nextNodes = topicNode.getNextNodes();

            if (prevNodeIds.isPresent()) {
                prevNodes.addAll(getTopics(prevNodeIds.get()));
            }

            if (nextNodeIds.isPresent()) {
                nextNodes.addAll(getTopics(nextNodeIds.get()));
            }

            checkNode(topicNode.getEasinessLevel(), prevNodes, nextNodes);
        }
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void updateNode(long id,
                           @NonNull OptionalField<@NonNull Integer> easinessLevel,
                           @NonNull OptionalField<@NonNull Set<Long>> prevNodeIds,
                           @NonNull OptionalField<@NonNull Set<Long>> nextNodeIds) throws PlatformException {
        DBTopicNode topicNode = topicNodeDAO.checkExistenceAndGet(id);

        int eLevel;
        if (easinessLevel.isPresent()) {
            eLevel = easinessLevel.get();
        } else {
            eLevel = topicNode.getEasinessLevel();
        }

        Set<DBTopicNode> prevNodes;
        if (prevNodeIds.isPresent()) {
            prevNodes = getTopics(prevNodeIds.get());
        } else {
            prevNodes = topicNode.getPrevNodes();
        }

        Set<DBTopicNode> nextNodes;
        if (nextNodeIds.isPresent()) {
            nextNodes = getTopics(nextNodeIds.get());
        } else {
            nextNodes = topicNode.getNextNodes();
        }

        checkNode(eLevel, prevNodes, nextNodes);

        topicNode.setEasinessLevel(eLevel);
        topicNode.setPrevNodes(prevNodes);
        topicNode.setNextNodes(nextNodes);
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void updateTopicTitle(long id,
                                 @NonNull String title) throws PlatformException {
        checkTitle(title);
        int cRows = topicDAO.updateTitle(id, title);
        if (cRows < 1) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBTopic.class, id);
        }
    }

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public void updateTopicBody(long id,
                                @NonNull String body) throws PlatformException {
        checkBody(body);
        int cRows = topicDAO.updateBody(id, body);
        if (cRows < 1) {
            throw GeneralExceptionFactory.buildNotFoundDomainObjectException(DBTopic.class, id);
        }
    }

    private Set<DBTopicNode> getTopics(Set<Long> ids) {
        List<Long> nonnullIds = removeNulls(ids);
        if (nonnullIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(topicNodeDAO.findAllById(nonnullIds));
    }

    private static void checkTopic(String title,
                                   String body) throws PlatformException {
        checkTitle(title);
        checkBody(body);
    }

    private static void checkTitle(String title) throws PlatformException {
        if (!StringUtils.hasText(title)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBTopic.class, DBTopic.Fields.title);
        }
    }

    private static void checkBody(String body) throws PlatformException {
        if (!StringUtils.hasText(body)) {
            throw GeneralExceptionFactory.buildEmptyValueException(DBTopic.class, DBTopic.Fields.body);
        }
    }

    private static void checkNode(int easinessLevel,
                                  Set<DBTopicNode> prevNodes,
                                  Set<DBTopicNode> nextNodes) throws PlatformException {
        checkEasinessLevel(easinessLevel, prevNodes, nextNodes);
        checkCycles(prevNodes, nextNodes);
    }

    private static void checkEasinessLevel(int easinessLevel,
                                           Set<DBTopicNode> prevNodes,
                                           Set<DBTopicNode> nextNodes) throws PlatformException {
        for (DBTopicNode prevTopic : prevNodes) {
            int topicEasinessLevel = prevTopic.getEasinessLevel();
            if (topicEasinessLevel < easinessLevel) {
                throw MathsRoadMapExceptionFactory.buildInvalidTopicEasinessLevelException(easinessLevel);
            }
        }

        for (DBTopicNode nextTopic : nextNodes) {
            int topicEasinessLevel = nextTopic.getEasinessLevel();
            if (topicEasinessLevel > easinessLevel) {
                throw MathsRoadMapExceptionFactory.buildInvalidTopicEasinessLevelException(easinessLevel);
            }
        }
    }

    private static void checkCycles(Set<DBTopicNode> prevNodes, Set<DBTopicNode> nextNodes) throws PlatformException {
        if (prevNodes.isEmpty() || nextNodes.isEmpty()) {
            return;
        }

        Set<Long> prevIds = prevNodes.stream()
                .map(DomainObject::getId)
                .collect(Collectors.toSet());

        //noinspection UnstableApiUsage
        Traverser<DBTopicNode> traverser = Traverser.forTree(DBTopicNode::getNextNodes);
        //noinspection UnstableApiUsage
        for (DBTopicNode nextNode : traverser.depthFirstPreOrder(nextNodes)) {
            if (prevIds.contains(nextNode.getId())) {
                throw MathsRoadMapExceptionFactory.buildTopicsHaveCycleException();
            }
        }
    }

    private static <T> List<T> removeNulls(Collection<T> collection) {
        return collection.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
