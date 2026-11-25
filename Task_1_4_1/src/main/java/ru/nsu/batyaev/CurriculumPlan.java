package ru.nsu.batyaev;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Нормативный учебный план ФИТ НГУ.
 */
public class CurriculumPlan {
    public static final int TOTAL_SEMESTERS = 8;

    private final Map<Integer, Map<ControlType, Integer>> plan = new HashMap<>();

    public CurriculumPlan() {
        for (int sem = 1; sem <= TOTAL_SEMESTERS; sem++) {
            plan.put(sem, new EnumMap<>(ControlType.class));
        }
    }

    public void setControlCount(int semester, ControlType type, int count) {
        if (semester < 1 || semester > TOTAL_SEMESTERS) {
            throw new IllegalArgumentException("Некорректный семестр: " + semester);
        }
        if (count < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        plan.get(semester).put(type, count);
    }

    public int getControlCount(int semester, ControlType type) {
        return plan.getOrDefault(semester, Collections.emptyMap()).getOrDefault(type, 0);
    }

    public int getDiplomaControlsCount(int semester) {
        return plan.getOrDefault(semester, Collections.emptyMap()).entrySet().stream()
                .filter(e -> e.getKey().goesToDiploma())
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    /**
     * Стандартный учебный план ФИТ НГУ по таблице.
     */
    public static CurriculumPlan createFitStandardPlan() {
        CurriculumPlan cp = new CurriculumPlan();

        int[] tasks       = {2, 2, 3, 2, 2, 2, 2, 0}; // Всего 15
        int[] tests       = {3, 3, 2, 1, 2, 2, 0, 0}; // Всего 13
        int[] colloquiums = {1, 1, 0, 0, 0, 0, 0, 0}; // Всего 2
        int[] exams       = {3, 3, 2, 5, 3, 2, 1, 0}; // Всего 19
        int[] diffCredits = {3, 3, 6, 5, 4, 6, 4, 4}; // Всего 35
        int[] credits     = {3, 2, 0, 0, 0, 0, 1, 1}; // Всего 7
        int[] practices   = {0, 0, 0, 0, 0, 0, 1, 2}; // Всего 3
        int[] thesis      = {0, 0, 0, 0, 0, 0, 0, 1}; // Всего 1

        for (int i = 0; i < TOTAL_SEMESTERS; i++) {
            int sem = i + 1;
            cp.setControlCount(sem, ControlType.TASK, tasks[i]);
            cp.setControlCount(sem, ControlType.TEST, tests[i]);
            cp.setControlCount(sem, ControlType.COLLOQUIUM, colloquiums[i]);
            cp.setControlCount(sem, ControlType.EXAM, exams[i]);
            cp.setControlCount(sem, ControlType.DIFF_CREDIT, diffCredits[i]);
            cp.setControlCount(sem, ControlType.CREDIT, credits[i]);
            cp.setControlCount(sem, ControlType.PRACTICE_DEFENSE, practices[i]);
            cp.setControlCount(sem, ControlType.THESIS_DEFENSE, thesis[i]);
        }
        return cp;
    }
}
