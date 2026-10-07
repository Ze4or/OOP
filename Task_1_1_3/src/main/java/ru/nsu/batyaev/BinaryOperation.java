package ru.nsu.batyaev;

/**
 * Абстрактный класс для всех бинарных математических операций.
 */
public abstract class BinaryOperation extends Expression {
    protected final Expression left;
    protected final Expression right;
    protected final String operator;

    public BinaryOperation(Expression left, Expression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + operator + right.toString() + ")";
    }
}