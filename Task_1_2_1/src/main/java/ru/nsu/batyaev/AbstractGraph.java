package ru.nsu.batyaev;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Базовый абстрактный класс графа.
 * Реализует общие методы сравнения на равенство, хэширования и вывода в строку.
 */
public abstract class AbstractGraph implements Graph {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Graph)) return false;
        Graph other = (Graph) o;

        Set<Integer> thisVertices = this.getVertices();
        Set<Integer> otherVertices = other.getVertices();

        if (!thisVertices.equals(otherVertices)) return false;

        for (int v : thisVertices) {
            Set<Integer> thisNeighbors = new HashSet<>(this.getNeighbors(v));
            Set<Integer> otherNeighbors = new HashSet<>(other.getNeighbors(v));
            if (!thisNeighbors.equals(otherNeighbors)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int v : getVertices()) {
            result += v * 31;
            for (int n : getNeighbors(v)) {
                result += Objects.hash(v, n);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" {\n");
        sb.append("  Вершины (").append(getVerticesCount()).append("): ").append(getVertices()).append("\n");
        sb.append("  Ребра (").append(getEdgesCount()).append("):\n");
        for (int u : getVertices()) {
            List<Integer> neighbors = getNeighbors(u);
            if (!neighbors.isEmpty()) {
                sb.append("    ").append(u).append(" -> ").append(neighbors).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
