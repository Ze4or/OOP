package ru.nsu.batyaev;

import java.util.*;

/**
 * Представление графа через матрицу смежности.
 */
public class AdjacencyMatrixGraph extends AbstractGraph {
    private final Map<Integer, Integer> vertexToIndex = new LinkedHashMap<>();
    private final List<Integer> indexToVertex = new ArrayList<>();
    private final List<List<Boolean>> matrix = new ArrayList<>();

    @Override
    public boolean addVertex(int v) {
        if (vertexToIndex.containsKey(v)) return false;
        int idx = indexToVertex.size();
        vertexToIndex.put(v, idx);
        indexToVertex.add(v);

        for (List<Boolean> row : matrix) {
            row.add(false);
        }
        List<Boolean> newRow = new ArrayList<>(Collections.nCopies(idx + 1, false));
        matrix.add(newRow);
        return true;
    }

    @Override
    public boolean removeVertex(int v) {
        if (!vertexToIndex.containsKey(v)) return false;
        int idx = vertexToIndex.get(v);

        vertexToIndex.remove(v);
        indexToVertex.remove(idx);
        matrix.remove(idx);

        for (List<Boolean> row : matrix) {
            row.remove(idx);
        }

        for (int i = idx; i < indexToVertex.size(); i++) {
            vertexToIndex.put(indexToVertex.get(i), i);
        }
        return true;
    }

    @Override
    public boolean addEdge(int u, int v) {
        addVertex(u);
        addVertex(v);
        int uIdx = vertexToIndex.get(u);
        int vIdx = vertexToIndex.get(v);
        if (matrix.get(uIdx).get(vIdx)) return false;
        matrix.get(uIdx).set(vIdx, true);
        return true;
    }

    @Override
    public boolean removeEdge(int u, int v) {
        if (!hasVertex(u) || !hasVertex(v)) return false;
        int uIdx = vertexToIndex.get(u);
        int vIdx = vertexToIndex.get(v);
        if (!matrix.get(uIdx).get(vIdx)) return false;
        matrix.get(uIdx).set(vIdx, false);
        return true;
    }

    @Override
    public boolean hasVertex(int v) {
        return vertexToIndex.containsKey(v);
    }

    @Override
    public boolean hasEdge(int u, int v) {
        if (!hasVertex(u) || !hasVertex(v)) return false;
        return matrix.get(vertexToIndex.get(u)).get(vertexToIndex.get(v));
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
        List<Boolean> row = matrix.get(uIdx);
        for (int j = 0; j < row.size(); j++) {
            if (row.get(j)) {
                neighbors.add(indexToVertex.get(j));
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
        int count = 0;
        for (List<Boolean> row : matrix) {
            for (boolean cell : row) {
                if (cell) count++;
            }
        }
        return count;
    }
}
