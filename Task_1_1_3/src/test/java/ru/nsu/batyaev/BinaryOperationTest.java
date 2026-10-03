package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BinaryOperationTest {
    private static class DummyOp extends BinaryOperation {
        public DummyOp(Expression left, Expression right, String operator) {
            super(left, right, operator);
        }

        @Override
        public Expression derivative(String var) {
            return new Number(0);
        }

        @Override
        public double eval(Map<String, Double> env) {
            return 0;
        }
    }

    @Test
    @DisplayName("Формирование общего скобочного формата бинарных операций")
    void testToStringFormatting() {
        BinaryOperation dummy = new DummyOp(new Variable("a"), new Variable("b"), "^");
        assertEquals("(a^b)", dummy.toString());
    }
}