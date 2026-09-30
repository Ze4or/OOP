package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddTest {

    @Test
    @DisplayName("Вычисление суммы двух чисел")
    void testEval() {
        Expression add = new Add(new Number(3), new Number(5));
        assertEquals(8.0, add.eval(Map.of()), 1e-9);
    }

    @Test
    @DisplayName("Дифференцирование суммы")
    void testDerivative() {
        Expression add = new Add(new Variable("x"), new Number(10));
        Expression der = add.derivative("x");

        assertEquals("(1+0)", der.toString());
    }

    @Test
    @DisplayName("Преобразование суммы в строку")
    void testToString() {
        Expression add = new Add(new Variable("x"), new Variable("y"));
        assertEquals("(x+y)", add.toString());
    }
}