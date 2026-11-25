package ru.nsu.batyaev;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        CurriculumPlan fitPlan = CurriculumPlan.createFitStandardPlan();
        RecordBook book = new RecordBook(
                "250627",
                "Батяев Кирилл Максимович",
                StudyForm.BUDGET,
                3,
                fitPlan
        );

        System.out.println("Студент: " + book.getStudentName() + " (зачетка №" + book.getStudentId() + ")");
        System.out.println("Направление: 09.03.01 Информатика и вычислительная техника (КНиС, набор 2025)");
        System.out.println("Форма обучения: " + book.getStudyForm() + "\n");

        // === 3-Й СЕМЕСТР (2 курс, осень) ===
        // Текущий контроль: 3 задания, 2 контрольные
        book.addRecord(new GradeRecord("Объектно-ориентированное программирование", 3, ControlType.TASK, Grade.EXCELLENT, LocalDate.of(2026, 11, 10)));
        book.addRecord(new GradeRecord("Операционные системы", 3, ControlType.TASK, Grade.SATISFACTORY, LocalDate.of(2026, 11, 24)));
        book.addRecord(new GradeRecord("Разработка программно-аппаратного комплекса", 3, ControlType.TASK, Grade.GOOD, LocalDate.of(2026, 12, 5)));
        book.addRecord(new GradeRecord("Дифференциальные уравнения и теория функций комплексного переменного", 3, ControlType.TEST, Grade.GOOD, LocalDate.of(2026, 11, 18)));
        book.addRecord(new GradeRecord("Теория вероятностей и математическая статистика", 3, ControlType.TEST, Grade.GOOD, LocalDate.of(2026, 12, 12)));

        // Дифференцированные зачеты (6 шт.)
        book.addRecord(new GradeRecord("Объектно-ориентированное программирование", 3, ControlType.DIFF_CREDIT, Grade.EXCELLENT, LocalDate.of(2026, 12, 24)));
        book.addRecord(new GradeRecord("Операционные системы", 3, ControlType.DIFF_CREDIT, Grade.SATISFACTORY, LocalDate.of(2026, 12, 26)));
        book.addRecord(new GradeRecord("Разработка программно-аппаратного комплекса", 3, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2026, 12, 27)));
        book.addRecord(new GradeRecord("Иностранный язык", 3, ControlType.DIFF_CREDIT, Grade.EXCELLENT, LocalDate.of(2026, 12, 28)));
        book.addRecord(new GradeRecord("Модели вычислений", 3, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2026, 12, 29)));
        book.addRecord(new GradeRecord("Теория вероятностей и математическая статистика", 3, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2026, 12, 30)));

        // Экзамены (2 шт.)
        book.addRecord(new GradeRecord("Дифференциальные уравнения и теория функций комплексного переменного", 3, ControlType.EXAM, Grade.GOOD, LocalDate.of(2027, 1, 14)));
        book.addRecord(new GradeRecord("Введение в искусственный интеллект", 3, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2027, 1, 21)));

        System.out.println("--- Итоги 3-го семестра (2 курс, осень) ---");
        System.out.printf("Текущий средний балл: %.2f%n", book.calculateCurrentAverageGrade());
        System.out.println("Возможность перевода на бюджет: " + book.canTransferToBudget());
        System.out.println("Прогноз красного диплома: " + book.canGetRedDiploma());

        // === 4-Й СЕМЕСТР (2 курс, весна) ===
        book.advanceSemester();

        // Текущий контроль: 2 задания, 1 контрольная
        book.addRecord(new GradeRecord("Встроенные цифровые системы управления", 4, ControlType.TASK, Grade.GOOD, LocalDate.of(2027, 4, 15)));
        book.addRecord(new GradeRecord("Введение в аналоговую электронику и технику измерений", 4, ControlType.TASK, Grade.GOOD, LocalDate.of(2027, 5, 12)));
        book.addRecord(new GradeRecord("Теория параллелизма", 4, ControlType.TEST, Grade.GOOD, LocalDate.of(2027, 5, 18)));

        // Дифференцированные зачеты (5 шт.)
        book.addRecord(new GradeRecord("Встроенные цифровые системы управления", 4, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2027, 5, 25)));
        book.addRecord(new GradeRecord("Деловой английский язык", 4, ControlType.DIFF_CREDIT, Grade.EXCELLENT, LocalDate.of(2027, 5, 27)));
        book.addRecord(new GradeRecord("Введение в компьютерные сети", 4, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2027, 5, 28)));
        book.addRecord(new GradeRecord("Разработка программно-аппаратного комплекса", 4, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2027, 5, 29)));
        book.addRecord(new GradeRecord("Введение в аналоговую электронику и технику измерений", 4, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2027, 5, 30)));

        // Экзамены (5 шт.)
        book.addRecord(new GradeRecord("Теория вероятностей и математическая статистика", 4, ControlType.EXAM, Grade.GOOD, LocalDate.of(2027, 6, 6)));
        book.addRecord(new GradeRecord("Операционные системы", 4, ControlType.EXAM, Grade.SATISFACTORY, LocalDate.of(2027, 6, 11)));
        book.addRecord(new GradeRecord("Модели вычислений", 4, ControlType.EXAM, Grade.GOOD, LocalDate.of(2027, 6, 16)));
        book.addRecord(new GradeRecord("Теория параллелизма", 4, ControlType.EXAM, Grade.GOOD, LocalDate.of(2027, 6, 20)));
        book.addRecord(new GradeRecord("Объектно-ориентированное программирование", 4, ControlType.EXAM, Grade.GOOD, LocalDate.of(2027, 6, 25)));

        System.out.println("\n--- Итоги 4-го семестра (2 курс, весна) ---");
        System.out.printf("Текущий средний балл: %.2f%n", book.calculateCurrentAverageGrade());
        System.out.println("Возможность перевода на бюджет: " + book.canTransferToBudget());
        System.out.println("Прогноз красного диплома: " + book.canGetRedDiploma());

        if (book.transferToBudget()) {
            System.out.println("Перевод выполнен! Новая форма обучения: " + book.getStudyForm());
        }

        book.advanceSemester();
        System.out.println("\n--- Начало 5-го семестра (3 курс) ---");
        System.out.println("Повышенная стипендия в текущем семестре: " + book.canGetIncreasedScholarship());
    }
}
