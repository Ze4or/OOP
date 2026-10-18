package ru.nsu.batyaev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("==================================================");
        System.out.println("  ДЕМОНСТРАЦИЯ ВСЕХ ФУНКЦИЙ И ИНТЕРФЕЙСА GRAPH");
        System.out.println("==================================================\n");

        Graph graph = new AdjacencyListGraph();

        System.out.println("--- 1. Операции с вершинами ---");
        System.out.println("Добавляем вершины 1, 2, 3, 4, 5, 10 (addVertex):");
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(10);

        System.out.println("Проверка наличия вершины 3 (hasVertex): " + graph.hasVertex(3));
        System.out.println("Проверка наличия вершины 99 (hasVertex): " + graph.hasVertex(99));
        System.out.println("Множество всех вершин (getVertices): " + graph.getVertices());
        System.out.println("Количество вершин (getVerticesCount): " + graph.getVerticesCount());

        System.out.println("\nУдаляем вершину 10 (removeVertex)...");
        boolean removedV = graph.removeVertex(10);
        System.out.println("Вершина 10 успешно удалена: " + removedV);
        System.out.println("Вершины после удаления 10: " + graph.getVertices());
        System.out.println("Количество вершин после удаления: " + graph.getVerticesCount());

        System.out.println("\n--- 2. Операции с ребрами ---");
        System.out.println("Добавляем ребра: 5->2, 5->1, 4->1, 2->3, 3->1, 1->99 (addEdge)");
        graph.addEdge(5, 2);
        graph.addEdge(5, 1);
        graph.addEdge(4, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        graph.addEdge(1, 99);

        System.out.println("Проверка наличия ребра 5->2 (hasEdge): " + graph.hasEdge(5, 2));
        System.out.println("Проверка наличия ребра 2->5 (hasEdge): " + graph.hasEdge(2, 5));
        System.out.println("Количество ребер в графе (getEdgesCount): " + graph.getEdgesCount());

        System.out.println("\nУдаляем ребро 1->99 (removeEdge) и вершину 99...");
        boolean removedE = graph.removeEdge(1, 99);
        graph.removeVertex(99);
        System.out.println("Ребро 1->99 успешно удалено: " + removedE);
        System.out.println("Проверка ребра 1->99 после удаления: " + graph.hasEdge(1, 99));
        System.out.println("Количество ребер после удаления: " + graph.getEdgesCount());

        System.out.println("\n--- 3. Получение соседей вершины ---");
        System.out.println("Соседи вершины 5 (getNeighbors): " + graph.getNeighbors(5));
        System.out.println("Соседи вершины 2 (getNeighbors): " + graph.getNeighbors(2));
        System.out.println("Соседи вершины 1 (getNeighbors, нет исходящих ребер): " + graph.getNeighbors(1));

        System.out.println("\n--- 4. Вывод структуры графа (toString) ---");
        System.out.println(graph);

        System.out.println("--- 5. Алгоритмы топологической сортировки ---");
        System.out.println("Сортировка по умолчанию (Алгоритм Кана / BFS): " + graph.topologicalSort());
        System.out.println("Сортировка через стратегию DFS (Тарьян): " + new DfsTopologicalSorter().sort(graph));

        System.out.println("\n--- 6. Сравнение разных способов представления графа ---");
        Graph matrixGraph = new AdjacencyMatrixGraph();
        matrixGraph.addEdge(5, 2);
        matrixGraph.addEdge(5, 1);
        matrixGraph.addEdge(4, 1);
        matrixGraph.addEdge(2, 3);
        matrixGraph.addEdge(3, 1);

        System.out.println("Сравнение AdjacencyListGraph и AdjacencyMatrixGraph (equals): " + graph.equals(matrixGraph));
        System.out.println("Совпадение hashCode: " + (graph.hashCode() == matrixGraph.hashCode()));

        System.out.println("\n--- 7. Чтение из файла фиксированного формата ---");
        Path tempFile = Files.createTempFile("graph_sample", ".txt");
        Files.writeString(tempFile, "# Пример файла\n100 200\n100 300\n200 400\n300 400\n500\n");

        Graph incidenceGraph = Graph.readFromFile(tempFile, IncidenceMatrixGraph::new);
        System.out.println("Граф, прочитанный из файла в IncidenceMatrixGraph:");
        System.out.println(incidenceGraph);
        System.out.println("Соседи вершины 100: " + incidenceGraph.getNeighbors(100));
        System.out.println("Топологическая сортировка прочитанного графа: " + incidenceGraph.topologicalSort());

        Files.deleteIfExists(tempFile);

        System.out.println("\n==================================================");
        System.out.println("  ВСЕ ОПЕРАЦИИ ГРАФА ПРОТЕСТИРОВАНЫ И УСПЕШНО ВЫПОЛНЕНЫ!");
        System.out.println("==================================================");
    }
}
