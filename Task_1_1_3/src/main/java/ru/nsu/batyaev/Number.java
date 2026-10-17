package ru.nsu.batyaev;

import java.util.Map;

/**
 * Терминальный узел дерева выражений, представляющий числовую константу.
 */
public class Number extends Expression {
    private final double value;

    public Number(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    @Override
    public Expression derivative(String var) {
        return new Number(0);
    }

    @Override
    public double eval(Map<String, Double> env) {
        return value;
    }
}