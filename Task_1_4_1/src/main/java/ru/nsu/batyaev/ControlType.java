package ru.nsu.batyaev;

/**
 * Виды контроля согласно учебному плану ФИТ НГУ.
 */
public enum ControlType {
    TASK("Задание", false, true),
    TEST("Контрольная", false, true),
    COLLOQUIUM("Коллоквиум", false, true),
    EXAM("Экзамен", true, true),
    DIFF_CREDIT("Дифференцированный зачет", true, true),
    CREDIT("Зачет", false, false),
    PRACTICE_DEFENSE("Защита отчета по практике", true, true),
    THESIS_DEFENSE("Защита ВКР", true, true);

    private final String title;
    private final boolean goesToDiploma;
    private final boolean graded;

    ControlType(String title, boolean goesToDiploma, boolean graded) {
        this.title = title;
        this.goesToDiploma = goesToDiploma;
        this.graded = graded;
    }

    public boolean goesToDiploma() {
        return goesToDiploma;
    }

    public boolean isGraded() {
        return graded;
    }

    @Override
    public String toString() {
        return title;
    }
}
