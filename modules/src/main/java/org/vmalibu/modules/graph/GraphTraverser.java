package org.vmalibu.modules.graph;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class GraphTraverser {

    public static <I, N extends GraphNode<I>> @NonNull Iterable<N> depth(
            @NonNull Function<N, Iterable<N>> nodesGetter,
            @NonNull N root
    ) {
        DepthIterator<N> iterator = new DepthIterator<>(nodesGetter, root);
        return new NodeIterable<>(iterator);
    }

    public static <N, X extends Throwable> @NonNull List<N> simpleDependencyTreeConstructor(
            @NonNull Collection<N> graph,
            @NonNull BiPredicate<N, N> dependencyResolver,
            @NonNull Supplier<X> onCircularDependencies) throws X {
        List<N> result = new ArrayList<>();
        while (result.size() != graph.size()) {
            N next = null;
            for (N node : graph) {
                if (!result.contains(node)) {
                    boolean isNext = graph.stream()
                            .allMatch(n -> !dependencyResolver.test(node, n) || result.contains(n));
                    if (isNext) {
                        next = node;
                        break;
                    }
                }
            }

            if (next == null) {
                throw onCircularDependencies.get();
            }

            result.add(next);
        }

        return result;
    }

    public static <N> @NonNull List<N> simpleDependencyTreeConstructor(
            @NonNull Collection<N> graph,
            @NonNull BiPredicate<N, N> dependencyResolver) {
        return simpleDependencyTreeConstructor(graph, dependencyResolver, () -> new IllegalStateException("There is a circular dependency"));
    }

    public static <I, N extends GraphNode<I>> @NonNull List<N> getFirstCycle(
            @NonNull Function<N, Iterable<N>> nodesGetter,
            @NonNull Iterable<N> roots
    ) {
        for (N root : roots) {
            Iterable<N> nodes = GraphTraverser.depth(nodesGetter, root);
            List<N> path = new ArrayList<>();
            Set<I> pathNodeKeys = new HashSet<>();
            for (N node : nodes) {
                I nodeKey = node.getNodeKey();
                if (pathNodeKeys.contains(nodeKey)) {
                    int index = findFirstIndexOf(path, node);
                    List<N> cycle = path.subList(index, path.size());
                    cycle.add(node);
                    return cycle;
                }
                pathNodeKeys.add(nodeKey);
                path.add(node);
            }
        }

        return List.of();
    }

    private static int findFirstIndexOf(List<? extends GraphNode<?>> graphNodes, GraphNode<?> graphNode) {
        for (int i = 0; i < graphNodes.size(); i++) {
            if (graphNode.getNodeKey().equals(graphNodes.get(i).getNodeKey())) {
                return i;
            }
        }
        return -1;
    }

    private record NodeIterable<N>(Iterator<N> iterator) implements Iterable<N> {
    }

    private static class DepthIterator<N> extends GraphIterator<N, Deque<N>> {

        DepthIterator(Function<N, Iterable<N>> nodesGetter, N root) {
            super(new LinkedList<>(), Deque::pollLast, nodesGetter, root);
        }

    }

    private static class BreadthIterator<N> extends GraphIterator<N, Deque<N>> {

        BreadthIterator(Function<N, Iterable<N>> nodesGetter, N root) {
            super(new LinkedList<>(), Deque::pollFirst, nodesGetter, root);
        }

    }

    private abstract static class GraphIterator<N, C extends Collection<N>> implements Iterator<N> {

        private final C buffer;
        private final Function<C, N> pollNext;
        private final Function<N, Iterable<N>> nodesGetter;

        GraphIterator(C buffer,
                      Function<C, N> pollNext,
                      Function<N, Iterable<N>> nodesGetter,
                      N root) {
            this.buffer = buffer;
            this.buffer.add(root);
            this.pollNext = pollNext;
            this.nodesGetter = nodesGetter;
        }

        @Override
        public boolean hasNext() {
            return !buffer.isEmpty();
        }

        @Override
        public N next() {
            N next = pollNext.apply(buffer);
            if (next == null) {
                throw new NoSuchElementException();
            }

            Iterable<N> nextNodes = nodesGetter.apply(next);
            nextNodes.forEach(buffer::add);
            return next;
        }

    }

}
