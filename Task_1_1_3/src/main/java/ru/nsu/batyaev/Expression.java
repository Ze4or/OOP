package ru.nsu.batyaev;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Абстрактный базовый класс, представляющий математическое выражение.
 * Содержит методы для парсинга, дифференцирования,
 * вычисления и ввода/вывода выражений.
 */
public abstract class Expression {
    public abstract String toString();
    public abstract Expression derivative(String var);
    public abstract double eval(Map<String, Double> env);

    public static Expression fromConsole(Scanner scanner) {
//        Scanner scanner = new Scanner(System.in);
        return parse(scanner.nextLine());
    }

    public static Expression fromFile(String filePath) throws IOException {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            return parse(sb.toString());
        }
    }

    /**
     * Разбирает строку и рекурсивно строит дерево математического выражения.
     * Поддерживает скобки, бинарные операции (+, -, *, /), числа и переменные.
     *
     * @param s строка для разбора
     * @return построенный объект Expression
     * @throws IllegalArgumentException если строка пуста
     */
    public static Expression parse(String s) {
        if (s == null) return null;
        s = s.replaceAll("\\s+", "");

        if (s.isEmpty()) {
            throw new IllegalArgumentException("Пустое выражение");
        }

        if (s.startsWith("(") && s.endsWith(")")) {
            int depth = 0;
            boolean matchesEntireString = true;

            for (int i = 0; i < s.length() - 1; i++) {
                char c = s.charAt(i);
                if (c == '(') depth++;
                else if (c == ')') depth--;

                if (depth == 0) {
                    matchesEntireString = false;
                    break;
                }
            }

            if (matchesEntireString) {
                s = s.substring(1, s.length() - 1);
            }
        }

        // Поиск бинарного оператора верхнего уровня (вне внутренних скобок)
        int depth = 0;
        int opIndex = -1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && (c == '+' || c == '-' || c == '*' || c == '/')) {
                opIndex = i;
                break;
            }
        }

        // Разбиение по найденному оператору
        if (opIndex != -1) {
            Expression left = parse(s.substring(0, opIndex));
            Expression right = parse(s.substring(opIndex + 1));
            char op = s.charAt(opIndex);

            switch (op) {
                case '+': return new Add(left, right);
                case '-': return new Sub(left, right);
                case '*': return new Mul(left, right);
                case '/': return new Div(left, right);
            }
        }

        // Попытка распознать число, иначе — переменная
        try {
            return new Number(Double.parseDouble(s));
        } catch (NumberFormatException ex) {
            return new Variable(s);
        }
    }

    public void print() {
        System.out.println(this.toString());
    }

    public void print(PrintStream stream) {
        stream.println(this.toString());
    }

    public void print(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(this.toString());
        }
    }

    /**
     * Вычисляет значение выражения по строке присвоения переменных.
     *
     * @param bindings строка с присвоениями формата "x = 10; y = 13"
     * @return округленный целочисленный результат вычисления
     */
    public int eval(String bindings) {
        Map<String, Double> env = new HashMap<>();
        if (bindings != null && !bindings.trim().isEmpty()) {
            String[] parts = bindings.split(";");
            for (String part : parts) {
                String[] kv = part.split("=");
                if (kv.length == 2) {
                    env.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
                }
            }
        }
        return (int) Math.round(eval(env));
    }
}