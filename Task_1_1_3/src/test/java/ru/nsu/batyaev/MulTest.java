package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MulTest {

    @Test
    @DisplayName("Вычисление произведения")
    void testEval() {
        Expression mul = new Mul(new Number(6), new Number(7));
        assertEquals(42.0, mul.eval(Map.of()), 1e-9);
    }

    @Test
    @DisplayName("Дифференцирование произведения: (f * g)' = f' * g + f * g'")
    void testDerivative() {
        Expression mul = new Mul(new Variable("x"), new Number(3));
        Expression der = mul.derivative("x");

        assertEquals("((1*3)+(x*0))", der.toString());
    }

    @Test
    @DisplayName("Преобразование произведения в строку")
    void testToString() {
        Expression mul = new Mul(new Variable("x"), new Number(2));
        assertEquals("(x*2)", mul.toString());
    }
}