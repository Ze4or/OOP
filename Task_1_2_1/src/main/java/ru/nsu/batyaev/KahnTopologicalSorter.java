package ru.nsu.batyaev;

import java.util.*;

/**
 * Топологическая сортировка с использованием алгоритма Кана.
 */
public class KahnTopologicalSorter implements TopologicalSorter {
    @Override
    public List<Integer> sort(Graph graph) {
        Map<Integer, Integer> inDegree = new HashMap<>();
        for (int v : graph.getVertices()) {
            inDegree.put(v, 0);
        }
        for (int u : graph.getVertices()) {
            for (int v : graph.getNeighbors(u)) {
                inDegree.put(v, inDegree.getOrDefault(v, 0) + 1);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int v : graph.getVertices()) {
            if (inDegree.get(v) == 0) {
                queue.add(v);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.add(u);
            for (int v : graph.getNeighbors(u)) {
                inDegree.put(v, inDegree.get(v) - 1);
                if (inDegree.get(v) == 0) {
                    queue.add(v);
                }
            }
        }

        if (result.size() != graph.getVerticesCount()) {
            throw new IllegalStateException("Граф содержит цикл, Топологическая сортировка невозможна.");
        }
        return result;
    }
}
