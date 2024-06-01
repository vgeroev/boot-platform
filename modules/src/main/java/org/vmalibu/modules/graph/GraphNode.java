package org.vmalibu.modules.graph;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface GraphNode<I> {

    @NonNull I getNodeKey();
}
