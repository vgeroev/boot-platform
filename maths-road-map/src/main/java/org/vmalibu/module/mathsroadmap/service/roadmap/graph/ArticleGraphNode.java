package org.vmalibu.module.mathsroadmap.service.roadmap.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.graph.GraphNode;

public class ArticleGraphNode implements GraphNode<Long> {

    private final long id;

    public ArticleGraphNode(long id) {
        this.id = id;
    }

    @Override
    public @NonNull Long getNodeKey() {
        return id;
    }
}
