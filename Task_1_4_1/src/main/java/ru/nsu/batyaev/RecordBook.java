package ru.nsu.batyaev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Электронная зачетная книжка студента ФИТ НГУ
 * (направление 09.03.01 «Информатика и вычислительная техника. Компьютерные науки и системотехника»).
 */
public class RecordBook {
    private final String studentId;
    private final String studentName;
    private StudyForm studyForm;
    private int currentSemester;
    private final CurriculumPlan curriculumPlan;
    private final List<GradeRecord> records = new ArrayList<>();

    public RecordBook(String studentId, String studentName, StudyForm studyForm, int currentSemester, CurriculumPlan curriculumPlan) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Номер зачетной книжки обязателен");
        }
        if (studentName == null || studentName.isBlank()) {
            throw new IllegalArgumentException("ФИО студента обязательно");
        }
        if (currentSemester < 1 || currentSemester > CurriculumPlan.TOTAL_SEMESTERS) {
            throw new IllegalArgumentException("Текущий семестр должен быть в диапазоне 1..8");
        }
        this.studentId = studentId;
        this.studentName = studentName;
        this.studyForm = Objects.requireNonNull(studyForm);
        this.currentSemester = currentSemester;
        this.curriculumPlan = Objects.requireNonNull(curriculumPlan);
    }

    public void addRecord(GradeRecord record) {
        Objects.requireNonNull(record);
        if (record.semester() > currentSemester) {
            throw new IllegalArgumentException(
                    "Нельзя добавить оценку за будущий семестр (" + record.semester() + " > " + currentSemester + ")");
        }
        records.add(record);
    }

    public void advanceSemester() {
        if (currentSemester >= CurriculumPlan.TOTAL_SEMESTERS) {
            throw new IllegalStateException("Студент уже на последнем семестре обучения");
        }
        currentSemester++;
    }

    public boolean transferToBudget() {
        if (!canTransferToBudget()) {
            return false;
        }
        this.studyForm = StudyForm.BUDGET;
        return true;
    }

    /**
     * 1. Вычисление текущего среднего балла за все время обучения.
     */
    public double calculateCurrentAverageGrade() {
        return records.stream()
                .filter(r -> r.grade().isNumeric())
                .mapToInt(r -> r.grade().getNumericValue())
                .average()
                .orElse(0.0);
    }

    /**
     * 2. Проверка возможности перевода с платной на бюджетную форму обучения:
     * отсутствие оценок "удовлетворительно" за последние две экзаменационные сессии
     * только по экзаменам.
     */
    public boolean canTransferToBudget() {
        if (studyForm != StudyForm.PAID) {
            return false;
        }
        if (hasAcademicDebts()) {
            return false;
        }

        boolean hasBadCurrentExam = records.stream()
                .filter(r -> r.semester() == currentSemester && r.controlType() == ControlType.EXAM)
                .anyMatch(r -> !r.grade().isGoodOrExcellent());
        if (hasBadCurrentExam) {
            return false;
        }

        List<Integer> completedExamSessions = new ArrayList<>();
        for (int sem = 1; sem <= currentSemester; sem++) {
            int plannedExams = curriculumPlan.getControlCount(sem, ControlType.EXAM);
            if (plannedExams == 0) {
                continue;
            }

            final int targetSem = sem;
            long passedExamsInSem = records.stream()
                    .filter(r -> r.semester() == targetSem && r.controlType() == ControlType.EXAM && r.grade().isPassing())
                    .map(GradeRecord::subjectName)
                    .distinct()
                    .count();

            if (passedExamsInSem >= plannedExams) {
                completedExamSessions.add(sem);
            }
        }

        if (completedExamSessions.size() < 2) {
            return false;
        }

        final Set<Integer> lastTwoSessions = Set.of(
                completedExamSessions.get(completedExamSessions.size() - 1),
                completedExamSessions.get(completedExamSessions.size() - 2)
        );

        return records.stream()
                .filter(r -> lastTwoSessions.contains(r.semester()) && r.controlType() == ControlType.EXAM)
                .allMatch(r -> r.grade().isGoodOrExcellent());
    }

    /**
     * 3. Прогноз возможности получения красного диплома с отличием:
     * - >= 75% последних оценок в приложении к диплому — "отлично";
     * - отсутствие итоговых оценок "удовлетворительно" (по экзаменам, диф. зачетам, практике, ВКР);
     * - квалификационная работа (ВКР) на "отлично".
     */
    public boolean canGetRedDiploma() {
        final Map<String, GradeRecord> latestDiplomaGrades = new LinkedHashMap<>();
        records.stream()
                .filter(r -> r.controlType().goesToDiploma())
                .sorted(Comparator.comparingInt(GradeRecord::semester).thenComparing(GradeRecord::date))
                .forEach(r -> latestDiplomaGrades.put(r.subjectName(), r));

        for (GradeRecord finalRecord : latestDiplomaGrades.values()) {
            if (!finalRecord.grade().isGoodOrExcellent()) {
                return false;
            }
        }

        boolean thesisDefended = false;
        for (GradeRecord finalRecord : latestDiplomaGrades.values()) {
            if (finalRecord.controlType() == ControlType.THESIS_DEFENSE) {
                thesisDefended = true;
                if (finalRecord.grade() != Grade.EXCELLENT) {
                    return false;
                }
            }
        }

        long currentExcellent = latestDiplomaGrades.values().stream()
                .filter(r -> r.grade() == Grade.EXCELLENT)
                .count();
        long currentTotal = latestDiplomaGrades.size();

        int plannedInCurrentSem = curriculumPlan.getDiplomaControlsCount(currentSemester);
        long actualInCurrentSem = records.stream()
                .filter(r -> r.semester() == currentSemester && r.controlType().goesToDiploma())
                .map(GradeRecord::subjectName)
                .distinct()
                .count();
        long remainingControls = Math.max(0, plannedInCurrentSem - actualInCurrentSem);

        for (int sem = currentSemester + 1; sem <= CurriculumPlan.TOTAL_SEMESTERS; sem++) {
            remainingControls += curriculumPlan.getDiplomaControlsCount(sem);
        }

        if (!thesisDefended && remainingControls == 0) {
            remainingControls = 1;
        }

        long totalExpected = currentTotal + remainingControls;
        long maxPossibleExcellent = currentExcellent + remainingControls;

        return maxPossibleExcellent * 4 >= totalExpected * 3;
    }

    /**
     * 4. Проверка возможности получения повышенной стипендии в текущем семестре.
     */
    public boolean canGetIncreasedScholarship() {
        if (studyForm != StudyForm.BUDGET || currentSemester <= 1) {
            return false;
        }
        if (hasAcademicDebts()) {
            return false;
        }

        final int previousSemester = currentSemester - 1;
        List<GradeRecord> prevSessionRecords = records.stream()
                .filter(r -> r.semester() == previousSemester)
                .filter(r -> r.controlType().goesToDiploma() || r.controlType() == ControlType.CREDIT)
                .toList();

        if (prevSessionRecords.isEmpty()) {
            return false;
        }

        return prevSessionRecords.stream().allMatch(r ->
                r.controlType().goesToDiploma()
                        ? r.grade() == Grade.EXCELLENT
                        : r.grade() == Grade.PASSED
        );
    }

    private boolean hasAcademicDebts() {
        final Map<String, GradeRecord> latestBySubject = new HashMap<>();
        records.stream()
                .sorted(Comparator.comparingInt(GradeRecord::semester).thenComparing(GradeRecord::date))
                .forEach(r -> latestBySubject.put(r.subjectName() + "_" + r.controlType(), r));

        return latestBySubject.values().stream()
                .anyMatch(r -> !r.grade().isPassing());
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public StudyForm getStudyForm() { return studyForm; }
    public int getCurrentSemester() { return currentSemester; }
    public List<GradeRecord> getRecords() { return Collections.unmodifiableList(records); }
}
