package ru.nsu.batyaev;

import java.util.List;

/**
 * Интерфейс стратегии для алгоритмов топологической сортировки.
 */
public interface TopologicalSorter {
    List<Integer> sort(Graph graph);
}
