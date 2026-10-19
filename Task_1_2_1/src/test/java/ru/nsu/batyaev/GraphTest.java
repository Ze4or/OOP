package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование реализаций графов и алгоритмов")
class GraphTest {

    static class SupplierGraph {
        private final String name;
        private final Supplier<Graph> supplier;

        public SupplierGraph(String name, Supplier<Graph> supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        public Supplier<Graph> supplier() {
            return supplier;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static Stream<SupplierGraph> graphProvider() {
        return Stream.of(
                new SupplierGraph("AdjacencyListGraph", AdjacencyListGraph::new),
                new SupplierGraph("AdjacencyMatrixGraph", AdjacencyMatrixGraph::new),
                new SupplierGraph("IncidenceMatrixGraph", IncidenceMatrixGraph::new)
        );
    }

    @ParameterizedTest(name = "Тест операций с вершинами: {0}")
    @MethodSource("graphProvider")
    @DisplayName("Операции с вершинами")
    void testVertexOperations(SupplierGraph sg) {
        Graph graph = sg.supplier().get();

        assertTrue(graph.addVertex(1));
        assertTrue(graph.addVertex(2));
        assertFalse(graph.addVertex(1), "Повторное добавление вершины должно возвращать false");

        assertTrue(graph.hasVertex(1));
        assertTrue(graph.hasVertex(2));
        assertFalse(graph.hasVertex(3));

        assertEquals(2, graph.getVerticesCount());
        assertEquals(Set.of(1, 2), graph.getVertices());

        assertTrue(graph.removeVertex(1));
        assertFalse(graph.hasVertex(1));
        assertEquals(1, graph.getVerticesCount());
        assertFalse(graph.removeVertex(1), "Повторное удаление должно возвращать false");
    }

    @ParameterizedTest(name = "Тест операций с ребрами: {0}")
    @MethodSource("graphProvider")
    @DisplayName("Операции с ребрами")
    void testEdgeOperations(SupplierGraph sg) {
        Graph graph = sg.supplier().get();

        assertTrue(graph.addEdge(1, 2));
        assertFalse(graph.addEdge(1, 2), "Повторное добавление ребра должно возвращать false");

        assertTrue(graph.hasVertex(1));
        assertTrue(graph.hasVertex(2));

        assertTrue(graph.hasEdge(1, 2));
        assertFalse(graph.hasEdge(2, 1), "Ребро должно быть ориентированным");

        assertEquals(1, graph.getEdgesCount());
        assertEquals(List.of(2), graph.getNeighbors(1));
        assertTrue(graph.getNeighbors(2).isEmpty());

        assertTrue(graph.removeEdge(1, 2));
        assertFalse(graph.hasEdge(1, 2));
        assertEquals(0, graph.getEdgesCount());
        assertFalse(graph.removeEdge(1, 2));
    }

    @ParameterizedTest(name = "Удаление вершины со смежными ребрами: {0}")
    @MethodSource("graphProvider")
    @DisplayName("Удаление вершины с инцидентными ребрами")
    void testRemoveVertexWithEdges(SupplierGraph sg) {
        Graph graph = sg.supplier().get();

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(1, 3);

        assertEquals(3, graph.getEdgesCount());

        assertTrue(graph.removeVertex(2));
        assertEquals(2, graph.getVerticesCount());
        assertEquals(1, graph.getEdgesCount(), "Ребра, связанные с удаленной вершиной, должны удаляться");
        assertFalse(graph.hasEdge(1, 2));
        assertFalse(graph.hasEdge(2, 3));
        assertTrue(graph.hasEdge(1, 3));
    }

    @ParameterizedTest(name = "Топологическая сортировка: {0}")
    @MethodSource("graphProvider")
    @DisplayName("Топологическая сортировка ациклического графа (DAG)")
    void testTopologicalSort(SupplierGraph sg) {
        Graph graph = sg.supplier().get();

        graph.addEdge(5, 2);
        graph.addEdge(5, 0);
        graph.addEdge(4, 0);
        graph.addEdge(4, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        List<Integer> kahnOrder = new KahnTopologicalSorter().sort(graph);
        List<Integer> dfsOrder = new DfsTopologicalSorter().sort(graph);

        assertValidTopologicalOrder(graph, kahnOrder);
        assertValidTopologicalOrder(graph, dfsOrder);
    }

    @ParameterizedTest(name = "Обнаружение циклов: {0}")
    @MethodSource("graphProvider")
    @DisplayName("Обнаружение циклов при топологической сортировке")
    void testTopologicalSortCycle(SupplierGraph sg) {
        Graph graph = sg.supplier().get();

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        assertThrows(IllegalStateException.class, () -> new KahnTopologicalSorter().sort(graph));
        assertThrows(IllegalStateException.class, () -> new DfsTopologicalSorter().sort(graph));
    }

    @Test
    @DisplayName("Сравнение разных реализаций графа на равенство (equals и hashCode)")
    void testCrossImplementationEqualsAndHashCode() {
        Graph g1 = new AdjacencyListGraph();
        Graph g2 = new AdjacencyMatrixGraph();
        Graph g3 = new IncidenceMatrixGraph();

        for (Graph g : List.of(g1, g2, g3)) {
            g.addEdge(10, 20);
            g.addEdge(10, 30);
            g.addEdge(20, 40);
            g.addVertex(50);
        }

        assertEquals(g1, g2);
        assertEquals(g2, g3);
        assertEquals(g1, g3);

        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals(g2.hashCode(), g3.hashCode());

        g1.addEdge(30, 50);
        assertNotEquals(g1, g2);
    }

    @Test
    @DisplayName("Чтение из файла с пропусками и комментариями")
    void testReadFromFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("graph.txt");
        String content = """
                # Комментарий в начале
                10 20
                10 30
                
                20 40
                # Еще один комментарий
                50
                """;
        Files.writeString(file, content);

        Graph graph = new AdjacencyListGraph();
        graph.readFromFile(file);

        assertEquals(5, graph.getVerticesCount());
        assertTrue(graph.getVertices().containsAll(Set.of(10, 20, 30, 40, 50)));
        assertEquals(3, graph.getEdgesCount());
        assertTrue(graph.hasEdge(10, 20));
        assertTrue(graph.hasEdge(10, 30));
        assertTrue(graph.hasEdge(20, 40));
        assertTrue(graph.getNeighbors(50).isEmpty());
    }

    private void assertValidTopologicalOrder(Graph graph, List<Integer> order) {
        assertEquals(graph.getVerticesCount(), order.size(), "Порядок должен содержать все вершины");

        for (int u : graph.getVertices()) {
            for (int v : graph.getNeighbors(u)) {
                int indexU = order.indexOf(u);
                int indexV = order.indexOf(v);
                assertTrue(indexU < indexV,
                        String.format("Вершина %d должна быть перед вершиной %d. Порядок: %s", u, v, order));
            }
        }
    }
}
