package org.vmalibu.modules.graph;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface GraphEdge<I, N extends GraphNode<I>> {

    @NonNull N from();

    @NonNull N to();
}
