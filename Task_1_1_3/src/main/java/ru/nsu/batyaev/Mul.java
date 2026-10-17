package ru.nsu.batyaev;

import java.util.Map;

/**
 * Класс, представляющий операцию умножения (*).
 */
public class Mul extends BinaryOperation {
    public Mul(Expression left, Expression right) {
        super(left, right, "*");
    }

    /**
     * Вычисляет производную произведения по правилу: (f * g)' = f' * g + f * g'.
     *
     * @param var имя переменной, по которой берется производная
     * @return новое выражение, представляющее производную произведения
     */
    @Override
    public Expression derivative(String var) {
        return new Add(
                new Mul(left.derivative(var), right),
                new Mul(left, right.derivative(var))
        );
    }

    @Override
    public double eval(Map<String, Double> env) {
        return left.eval(env) * right.eval(env);
    }
}