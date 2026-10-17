package ru.nsu.batyaev;

import java.util.Map;

/**
 * Класс, представляющий операцию вычитания (-).
 */
public class Sub extends BinaryOperation {
    public Sub(Expression left, Expression right) {
        super(left, right, "-");
    }

    /**
     * Вычисляет производную разности по правилу: (f - g)' = f' - g'.
     *
     * @param var имя переменной, по которой берется производная
     * @return новое выражение, представляющее производную разности
     */
    @Override
    public Expression derivative(String var) {
        return new Sub(left.derivative(var), right.derivative(var));
    }

    @Override
    public double eval(Map<String, Double> env) {
        return left.eval(env) - right.eval(env);
    }
}