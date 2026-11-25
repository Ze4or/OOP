package ru.nsu.batyaev;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Неизменяемая запись о результате прохождения контроля по дисциплине.
 */
public record GradeRecord(
        String subjectName,
        int semester,
        ControlType controlType,
        Grade grade,
        LocalDate date
) {
    public GradeRecord {
        if (subjectName == null || subjectName.isBlank()) {
            throw new IllegalArgumentException("Название дисциплины не может быть пустым");
        }
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("Номер семестра должен быть от 1 до 8");
        }
        Objects.requireNonNull(controlType, "Вид контроля обязателен");
        Objects.requireNonNull(grade, "Оценка обязательна");
        Objects.requireNonNull(date, "Дата обязательна");

        if (controlType.isGraded() && !grade.isNumeric()) {
            throw new IllegalArgumentException("Для вида контроля '" + controlType + "' требуется числовая оценка");
        }
        if (!controlType.isGraded() && grade.isNumeric()) {
            throw new IllegalArgumentException("Для вида контроля '" + controlType + "' допустимы только Зачтено/Не зачтено");
        }
    }
}
