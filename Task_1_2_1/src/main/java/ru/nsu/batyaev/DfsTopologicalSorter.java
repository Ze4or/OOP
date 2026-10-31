package ru.nsu.batyaev;

import java.util.*;

/**
 * Топологическая сортировка с использованием Поиска в глубину.
 */
public class DfsTopologicalSorter implements TopologicalSorter {
    private enum State { UNVISITED, VISITING, VISITED }

    @Override
    public List<Integer> sort(Graph graph) {
        Map<Integer, State> state = new HashMap<>();
        for (int v : graph.getVertices()) {
            state.put(v, State.UNVISITED);
        }

        LinkedList<Integer> result = new LinkedList<>();
        for (int v : graph.getVertices()) {
            if (state.get(v) == State.UNVISITED) {
                dfs(v, graph, state, result);
            }
        }
        return result;
    }

    private void dfs(int u, Graph graph, Map<Integer, State> state, LinkedList<Integer> result) {
        state.put(u, State.VISITING);
        for (int v : graph.getNeighbors(u)) {
            if (state.get(v) == State.VISITING) {
                throw new IllegalStateException("Граф содержит цикл, Топологическая сортировка невозможна.");
            }
            if (state.get(v) == State.UNVISITED) {
                dfs(v, graph, state, result);
            }
        }
        state.put(u, State.VISITED);
        result.addFirst(u);
    }
}
