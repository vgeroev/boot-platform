package org.vmalibu.module.mathsroadmap.service.roadmap.graph;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.graph.GraphTraverser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ArticleTreeValidator {

    public static @NonNull List<ArticleGraphNode> getFirstCycle(@NonNull Set<@NonNull ArticleEdge> edges) {
        Set<ArticleGraphNode> rootNodes = edges.stream()
                .map(e -> new ArticleGraphNode(e.prevId()))
                .collect(Collectors.toSet());

        return GraphTraverser.getFirstCycle(
                node -> edges.stream()
                        .filter(e -> node.getNodeKey().equals(e.prevId()))
                        .map(e -> new ArticleGraphNode(e.nextId()))
                        .collect(Collectors.toSet()),
                rootNodes
        );
    }

}
