package ru.nsu.batyaev;

import java.io.IOException;
import java.util.Scanner;

/**
 * Главный класс программы, содержащий точку входа main.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите способ ввода выражения:");
        System.out.println("1 — Ввести с консоли");
        System.out.println("2 — Прочитать из файла");
        System.out.print("Ваш выбор (1 или 2): ");

        String choice = scanner.nextLine().trim();
        Expression e;

        try {
            if ("1".equals(choice)) {
                System.out.println("Введите выражение (например: (3+(2*x))):");
                e = Expression.fromConsole(scanner);
            } else if ("2".equals(choice)) {
                System.out.print("Введите путь к файлу (например: input.txt): ");
                String filePath = scanner.nextLine().trim();
                e = Expression.fromFile(filePath);
            } else {
                System.out.println("Неверный ввод. Завершение работы.");
                return;
            }

            System.out.print("\nРаспознанное выражение: ");
            e.print();

            System.out.println("\nВведите переменную для дифференцирования:");
            String var = scanner.nextLine().trim();
            Expression de = e.derivative(var);
            System.out.print("Результат дифференцирования: ");
            de.print();

            System.out.println("\nВведите значения переменных (например: x = 10; y = 13):");
            String bindings = scanner.nextLine();
            int result = e.eval(bindings);
            System.out.println("Результат вычисления: " + result);

        } catch (IOException ex) {
            System.err.println("Ошибка при чтении файла: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Ошибка при обработке выражения: " + ex.getMessage());
        }
    }
}
