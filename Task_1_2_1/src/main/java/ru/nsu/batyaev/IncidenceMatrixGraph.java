package ru.nsu.batyaev;

import java.util.*;

/**
 * Представление графа через матрицу инцидентности.
 */
public class IncidenceMatrixGraph extends AbstractGraph {
    private static final int NO_EDGE = 0;
    private static final int INCOMING_EDGE = 1;
    private static final int OUTGOING_EDGE = -1;
    private static final int LOOP_EDGE = 2;

    private static class Edge {
        final int u, v;
        Edge(int u, int v) { this.u = u; this.v = v; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge edge = (Edge) o;
            return u == edge.u && v == edge.v;
        }
        @Override
        public int hashCode() { return Objects.hash(u, v); }
    }

    private final Map<Integer, Integer> vertexToIndex = new LinkedHashMap<>();
    private final List<Integer> indexToVertex = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<List<Integer>> matrix = new ArrayList<>();

    @Override
    public boolean addVertex(int v) {
        if (vertexToIndex.containsKey(v)) return false;
        int idx = indexToVertex.size();
        vertexToIndex.put(v, idx);
        indexToVertex.add(v);

        List<Integer> newRow = new ArrayList<>(Collections.nCopies(edges.size(), NO_EDGE));
        matrix.add(newRow);
        return true;
    }

    @Override
    public boolean removeVertex(int v) {
        if (!vertexToIndex.containsKey(v)) return false;
        int vIdx = vertexToIndex.get(v);

        for (int i = edges.size() - 1; i >= 0; i--) {
            Edge e = edges.get(i);
            if (e.u == v || e.v == v) {
                removeEdgeByIndex(i);
            }
        }

        matrix.remove(vIdx);
        vertexToIndex.remove(v);
        indexToVertex.remove(vIdx);

        for (int i = vIdx; i < indexToVertex.size(); i++) {
            vertexToIndex.put(indexToVertex.get(i), i);
        }
        return true;
    }

    @Override
    public boolean addEdge(int u, int v) {
        addVertex(u);
        addVertex(v);
        Edge newEdge = new Edge(u, v);
        if (edges.contains(newEdge)) return false;

        edges.add(newEdge);
        int uIdx = vertexToIndex.get(u);
        int vIdx = vertexToIndex.get(v);

        for (int i = 0; i < indexToVertex.size(); i++) {
            int val = NO_EDGE;
            if (u == v && i == uIdx) val = LOOP_EDGE;
            else if (i == uIdx) val = OUTGOING_EDGE;
            else if (i == vIdx) val = INCOMING_EDGE;
            matrix.get(i).add(val);
        }
        return true;
    }

    @Override
    public boolean removeEdge(int u, int v) {
        Edge e = new Edge(u, v);
        int eIdx = edges.indexOf(e);
        if (eIdx == -1) return false;
        removeEdgeByIndex(eIdx);
        return true;
    }

    private void removeEdgeByIndex(int eIdx) {
        edges.remove(eIdx);
        for (List<Integer> row : matrix) {
            row.remove(eIdx);
        }
    }

    @Override
    public boolean hasVertex(int v) {
        return vertexToIndex.containsKey(v);
    }

    @Override
    public boolean hasEdge(int u, int v) {
        return edges.contains(new Edge(u, v));
    }

    @Override
    public Set<Integer> getVertices() {
        return Collections.unmodifiableSet(vertexToIndex.keySet());
    }

    @Override
    public List<Integer> getNeighbors(int u) {
        if (!hasVertex(u)) return Collections.emptyList();
        int uIdx = vertexToIndex.get(u);
        List<Integer> neighbors = new ArrayList<>();
        List<Integer> row = matrix.get(uIdx);

        for (int eIdx = 0; eIdx < row.size(); eIdx++) {
            int val = row.get(eIdx);
            if (val == OUTGOING_EDGE || val == LOOP_EDGE) {
                neighbors.add(edges.get(eIdx).v);
            }
        }
        return neighbors;
    }

    @Override
    public int getVerticesCount() {
        return vertexToIndex.size();
    }

    @Override
    public int getEdgesCount() {
        return edges.size();
    }
}
