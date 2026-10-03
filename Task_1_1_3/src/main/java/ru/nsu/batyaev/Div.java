package ru.nsu.batyaev;

import java.util.Map;

/**
 * Класс, представляющий операцию деления (/).
 */
public class Div extends BinaryOperation {
    public Div(Expression left, Expression right) {
        super(left, right, "/");
    }

    /**
     * Вычисляет производную частного по правилу: (f / g)' = (f' * g - f * g') / (g * g).
     *
     * @param var имя переменной, по которой берется производная
     * @return новое выражение, представляющее производную частного
     */
    @Override
    public Expression derivative(String var) {
        return new Div(
                new Sub(
                        new Mul(left.derivative(var), right),
                        new Mul(left, right.derivative(var))
                ),
                new Mul(right, right)
        );
    }

    @Override
    public double eval(Map<String, Double> env) {
        return left.eval(env) / right.eval(env);
    }
}