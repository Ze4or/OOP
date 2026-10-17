package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NumberTest {

    @Test
    @DisplayName("Возврат собственного значения при eval")
    void testEval() {
        Number num = new Number(12.34);
        assertEquals(12.34, num.eval(Map.of()), 1e-9);
    }

    @Test
    @DisplayName("Производная от числа всегда равна 0")
    void testDerivative() {
        Number num = new Number(100);
        Expression der = num.derivative("x");

        assertEquals("0", der.toString());
    }

    @Test
    @DisplayName("Форматирование целых и дробных чисел")
    void testToString() {
        assertEquals("5", new Number(5.0).toString());
        assertEquals("5.5", new Number(5.5).toString());
    }
}