package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VariableTest {

    @Test
    @DisplayName("Получение значения переменной из карты окружения")
    void testEvalSuccess() {
        Variable var = new Variable("x");
        assertEquals(10.0, var.eval(Map.of("x", 10.0)), 1e-9);
    }

    @Test
    @DisplayName("Исключение, если переменной нет в карте окружения")
    void testEvalMissingVariable() {
        Variable var = new Variable("z");
        assertThrows(IllegalArgumentException.class, () -> var.eval(Map.of("x", 1.0)));
    }

    @Test
    @DisplayName("Производная переменной по себе равна 1, по другой — 0")
    void testDerivative() {
        Variable var = new Variable("x");

        assertEquals("1", var.derivative("x").toString());
        assertEquals("0", var.derivative("y").toString());
    }

    @Test
    @DisplayName("Строковое представление переменной совпадает с ее именем")
    void testToString() {
        Variable var = new Variable("varName");
        assertEquals("varName", var.toString());
    }
}