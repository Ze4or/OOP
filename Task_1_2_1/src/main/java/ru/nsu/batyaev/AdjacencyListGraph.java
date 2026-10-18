package ru.nsu.batyaev;

import java.util.*;

/**
 * Представление графа через список смежности.
 */
public class AdjacencyListGraph extends AbstractGraph {
    private final Map<Integer, Set<Integer>> adj = new LinkedHashMap<>();

    @Override
    public boolean addVertex(int v) {
        if (adj.containsKey(v)) return false;
        adj.put(v, new LinkedHashSet<>());
        return true;
    }

    @Override
    public boolean removeVertex(int v) {
        if (!adj.containsKey(v)) return false;
        adj.remove(v);
        for (Set<Integer> neighbors : adj.values()) {
            neighbors.remove(v);
        }
        return true;
    }

    @Override
    public boolean addEdge(int u, int v) {
        addVertex(u);
        addVertex(v);
        return adj.get(u).add(v);
    }

    @Override
    public boolean removeEdge(int u, int v) {
        if (!adj.containsKey(u)) return false;
        return adj.get(u).remove(v);
    }

    @Override
    public boolean hasVertex(int v) {
        return adj.containsKey(v);
    }

    @Override
    public boolean hasEdge(int u, int v) {
        return adj.containsKey(u) && adj.get(u).contains(v);
    }

    @Override
    public Set<Integer> getVertices() {
        return Collections.unmodifiableSet(adj.keySet());
    }

    @Override
    public List<Integer> getNeighbors(int u) {
        if (!adj.containsKey(u)) return Collections.emptyList();
        return new ArrayList<>(adj.get(u));
    }

    @Override
    public int getVerticesCount() {
        return adj.size();
    }

    @Override
    public int getEdgesCount() {
        int count = 0;
        for (Set<Integer> neighbors : adj.values()) {
            count += neighbors.size();
        }
        return count;
    }
}
