package ru.nsu.batyaev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

    @Test
    @DisplayName("Парсинг строки с операциями разного приоритета и скобками")
    void testParse() {
        Expression expr = Expression.parse(" (x + (y * 2)) ");
        assertEquals("(x+(y*2))", expr.toString());
    }

    @Test
    @DisplayName("Парсинг из Scanner (fromConsole)")
    void testFromConsole() {
        Scanner scanner = new Scanner("(a-b)\n");
        Expression expr = Expression.fromConsole(scanner);
        assertEquals("(a-b)", expr.toString());
    }

    @Test
    @DisplayName("Чтение выражения из файла (fromFile)")
    void testFromFile(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("input.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("(x+10)");
        }

        Expression expr = Expression.fromFile(file.getAbsolutePath());
        assertEquals("(x+10)", expr.toString());
    }

    @Test
    @DisplayName("Вычисление по текстовой строке с присваиваниями (eval с bindings)")
    void testEvalWithBindings() {
        Expression expr = Expression.parse("((x*y)+5)");
        int result = expr.eval("x = 2; y = 3");
        assertEquals(11, result);
    }

    @Test
    @DisplayName("Запись выражения в файл через метод print(String)")
    void testPrintToFile(@TempDir Path tempDir) throws IOException {
        Expression expr = Expression.parse("(x*y)");
        File outputFile = tempDir.resolve("output.txt").toFile();

        expr.print(outputFile.getAbsolutePath());

        Expression loaded = Expression.fromFile(outputFile.getAbsolutePath());
        assertEquals("(x*y)", loaded.toString());
    }

    @Test
    @DisplayName("Печать в поток вывода print(PrintStream)")
    void testPrintToStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        Expression expr = Expression.parse("(a+b)");
        expr.print(ps);

        assertEquals("(a+b)" + System.lineSeparator(), baos.toString());
    }
}