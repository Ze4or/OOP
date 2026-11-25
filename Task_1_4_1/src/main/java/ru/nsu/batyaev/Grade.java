package ru.nsu.batyaev;

/**
 * Оценки, выставляемые в зачетную книжку.
 */
public enum Grade {
    EXCELLENT(5, "Отлично", true),
    GOOD(4, "Хорошо", true),
    SATISFACTORY(3, "Удовлетворительно", true),
    UNSATISFACTORY(2, "Неудовлетворительно", true),
    PASSED(0, "Зачтено", false),
    FAILED(0, "Не зачтено", false);

    private final int numericValue;
    private final String title;
    private final boolean numeric;

    Grade(int numericValue, String title, boolean numeric) {
        this.numericValue = numericValue;
        this.title = title;
        this.numeric = numeric;
    }

    public int getNumericValue() {
        if (!numeric) {
            throw new IllegalStateException("Оценка '" + title + "' не имеет числового значения");
        }
        return numericValue;
    }

    public boolean isNumeric() {
        return numeric;
    }

    public boolean isPassing() {
        return this == EXCELLENT || this == GOOD || this == SATISFACTORY || this == PASSED;
    }

    public boolean isGoodOrExcellent() {
        return this == EXCELLENT || this == GOOD;
    }

    @Override
    public String toString() {
        return title;
    }
}
