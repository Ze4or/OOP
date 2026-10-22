package ru.nsu.batyaev;

import java.util.*;

/**
 * Представление графа через матрицу смежности.
 */
public class AdjacencyMatrixGraph extends AbstractGraph {
    private final Map<Integer, Integer> vertexToIndex = new LinkedHashMap<>();
    private final List<Integer> indexToVertex = new ArrayList<>();
    private boolean[][] matrix = new boolean[0][0];

    @Override
    public boolean addVertex(int v) {
        if (vertexToIndex.containsKey(v)) return false;
        int idx = indexToVertex.size();
        vertexToIndex.put(v, idx);
        indexToVertex.add(v);

        int n = indexToVertex.size();
        boolean[][] newMatrix = new boolean[n][n];

        for (int i = 0; i < n - 1; i++) {
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, n - 1);
        }
        matrix = newMatrix;
        return true;
    }

    @Override
    public boolean removeVertex(int v) {
        if (!vertexToIndex.containsKey(v)) return false;
        int idx = vertexToIndex.get(v);

        vertexToIndex.remove(v);
        indexToVertex.remove(idx);

        int n = indexToVertex.size();
        boolean[][] newMatrix = new boolean[n][n];

        for (int i = 0, newI = 0; i <= n; i++) {
            if (i == idx) continue;
            for (int j = 0, newJ = 0; j <= n; j++) {
                if (j == idx) continue;
                newMatrix[newI][newJ] = matrix[i][j];
                newJ++;
            }
            newI++;
        }
        matrix = newMatrix;

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
        if (matrix[uIdx][vIdx]) return false;
        matrix[uIdx][vIdx] = true;
        return true;
    }

    @Override
    public boolean removeEdge(int u, int v) {
        if (!hasVertex(u) || !hasVertex(v)) return false;
        int uIdx = vertexToIndex.get(u);
        int vIdx = vertexToIndex.get(v);
        if (!matrix[uIdx][vIdx]) return false;
        matrix[uIdx][vIdx] = false;
        return true;
    }

    @Override
    public boolean hasVertex(int v) {
        return vertexToIndex.containsKey(v);
    }

    @Override
    public boolean hasEdge(int u, int v) {
        if (!hasVertex(u) || !hasVertex(v)) return false;
        return matrix[vertexToIndex.get(u)][vertexToIndex.get(v)];
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
        int n = indexToVertex.size();
        for (int j = 0; j < n; j++) {
            if (matrix[uIdx][j]) {
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
        int n = indexToVertex.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j]) count++;
            }
        }
        return count;
    }
}
