package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DivTest {

    @Test
    @DisplayName("Вычисление деления")
    void testEval() {
        Expression div = new Div(new Number(15), new Number(3));
        assertEquals(5.0, div.eval(Map.of()), 1e-9);
    }

    @Test
    @DisplayName("Дифференцирование частного: (f / g)' = (f'*g - f*g') / (g*g)")
    void testDerivative() {
        Expression div = new Div(new Variable("x"), new Variable("y"));
        Expression der = div.derivative("x");

        assertEquals("(((1*y)-(x*0))/(y*y))", der.toString());
    }

    @Test
    @DisplayName("Преобразование деления в строку")
    void testToString() {
        Expression div = new Div(new Variable("a"), new Variable("b"));
        assertEquals("(a/b)", div.toString());
    }
}