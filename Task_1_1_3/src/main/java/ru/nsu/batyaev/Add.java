package ru.nsu.batyaev;

import java.util.Map;

/**
 * Класс, представляющий операцию сложения (+).
 */
public class Add extends BinaryOperation {
    public Add(Expression left, Expression right) {
        super(left, right, "+");
    }

    /**
     * Вычисляет производную суммы по правилу: (f + g)' = f' + g'.
     *
     * @param var имя переменной, по которой берется производная
     * @return новое выражение, представляющее производную суммы
     */
    @Override
    public Expression derivative(String var) {
        return new Add(left.derivative(var), right.derivative(var));
    }

    @Override
    public double eval(Map<String, Double> env) {
        return left.eval(env) + right.eval(env);
    }
}