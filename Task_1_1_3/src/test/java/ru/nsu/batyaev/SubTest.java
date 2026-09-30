package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SubTest {

    @Test
    @DisplayName("Вычисление разности двух чисел")
    void testEval() {
        Expression sub = new Sub(new Number(10), new Number(4));
        assertEquals(6.0, sub.eval(Map.of()), 1e-9);
    }

    @Test
    @DisplayName("Дифференцирование разности")
    void testDerivative() {
        Expression sub = new Sub(new Variable("x"), new Variable("y"));
        Expression derX = sub.derivative("x");

        assertEquals("(1-0)", derX.toString());
    }

    @Test
    @DisplayName("Преобразование разности в строку")
    void testToString() {
        Expression sub = new Sub(new Variable("a"), new Variable("b"));
        assertEquals("(a-b)", sub.toString());
    }
}