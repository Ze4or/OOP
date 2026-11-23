package ru.nsu.batyaev;

import java.time.LocalDate;
import java.util.*;

/**
 * Оценки, выставляемые в зачетную книжку.
 */
enum Grade {
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

/**
 * Виды контроля согласно учебному плану ФИТ НГУ.
 */
enum ControlType {
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

/**
 * Форма обучения студента.
 */
enum StudyForm {
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

/**
 * Неизменяемая запись о результате прохождения контроля по дисциплине.
 */
record GradeRecord(
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

/**
 * Нормативный учебный план ФИТ НГУ.
 */
class CurriculumPlan {
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
     * только по экзаменам (в диф. зачетах оценки "3" допустимы).
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