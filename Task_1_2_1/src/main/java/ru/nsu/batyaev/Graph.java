package ru.nsu.batyaev;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Интерфейс ориентированного графа с вершинами типа int.
 */
public interface Graph {
    // Операции с вершинами
    boolean addVertex(int v);
    boolean removeVertex(int v);
    boolean hasVertex(int v);
    Set<Integer> getVertices();
    int getVerticesCount();

    // Операции с ребрами
    boolean addEdge(int u, int v);
    boolean removeEdge(int u, int v);
    boolean hasEdge(int u, int v);
    int getEdgesCount();

    // Получение списка смежных (исходящих) вершин
    List<Integer> getNeighbors(int u);

    // Топологическая сортировка по умолчанию (Алгоритм Кана)
    default List<Integer> topologicalSort() {
        return new KahnTopologicalSorter().sort(this);
    }

    /**
     * Чтение графа из файла фиксированного формата.
     */
    static <G extends Graph> G readFromFile(Path path, Supplier<G> graphFactory) throws IOException {
        G graph = graphFactory.get();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length == 1) {
                    graph.addVertex(Integer.parseInt(parts[0]));
                } else if (parts.length >= 2) {
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);
                    graph.addEdge(u, v);
                }
            }
        }
        return graph;
    }
}
