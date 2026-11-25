package ru.nsu.batyaev;

/**
 * Форма обучения студента.
 */
public enum StudyForm {
    BUDGET("Бюджетная"),
    PAID("Платная");

    private final String title;

    StudyForm(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
