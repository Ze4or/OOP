package ru.nsu.batyaev;

import java.util.Map;

/**
 * Терминальный узел дерева выражений, представляющий переменную.
 */
public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Вычисляет производную переменной:
     * равна 1, если переменная совпадает с var, и 0 в противном случае.
     *
     * @param var имя переменной, по которой выполняется дифференцирование
     * @return Number(1) или Number(0)
     */
    @Override
    public Expression derivative(String var) {
        return name.equals(var) ? new Number(1) : new Number(0);
    }

    @Override
    public double eval(Map<String, Double> env) {
        if (!env.containsKey(name)) {
            throw new IllegalArgumentException("Значение не задано: " + name);
        }
        return env.get(name);
    }
}