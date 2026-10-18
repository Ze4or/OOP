package ru.nsu.batyaev;

import java.util.*;

/**
 * Представление графа через матрицу инцидентности.
 * Строки соответствуют вершинам, столбцы - ребрам.
 * Значения: -1 (исходящее ребро), +1 (входящее ребро), 2 (петля).
 */
public class IncidenceMatrixGraph extends AbstractGraph {
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

        List<Integer> newRow = new ArrayList<>(Collections.nCopies(edges.size(), 0));
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
            int val = 0;
            if (u == v && i == uIdx) val = 2;
            else if (i == uIdx) val = -1;
            else if (i == vIdx) val = 1;
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
            if (val == -1 || val == 2) {
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
