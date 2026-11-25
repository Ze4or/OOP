package ru.nsu.batyaev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RecordBookTest {

    private CurriculumPlan fitPlan;

    @BeforeEach
    void setUp() {
        fitPlan = CurriculumPlan.createFitStandardPlan();
    }

    @Nested
    @DisplayName("Валидация инвариантов")
    class ValidationTests {

        @Test
        @DisplayName("Grade: числовые оценки возвращают балл, зачеты бросают исключение")
        void gradeNumericBehavior() {
            assertEquals(5, Grade.EXCELLENT.getNumericValue());
            assertTrue(Grade.SATISFACTORY.isPassing());
            assertFalse(Grade.UNSATISFACTORY.isPassing());
            assertFalse(Grade.SATISFACTORY.isGoodOrExcellent());
            assertThrows(IllegalStateException.class, Grade.PASSED::getNumericValue);
            assertThrows(IllegalStateException.class, Grade.FAILED::getNumericValue);
        }

        @Test
        @DisplayName("GradeRecord: запрещает несовместимую пару контроль/оценка и некорректные аргументы")
        void gradeRecordValidation() {
            LocalDate date = LocalDate.of(2026, 1, 15);

            assertThrows(IllegalArgumentException.class, () ->
                    new GradeRecord("Матан", 1, ControlType.EXAM, Grade.PASSED, date));

            assertThrows(IllegalArgumentException.class, () ->
                    new GradeRecord("Физкультура", 1, ControlType.CREDIT, Grade.EXCELLENT, date));

            assertThrows(IllegalArgumentException.class, () ->
                    new GradeRecord("   ", 1, ControlType.EXAM, Grade.GOOD, date));
            assertThrows(IllegalArgumentException.class, () ->
                    new GradeRecord("Матан", 9, ControlType.EXAM, Grade.GOOD, date));
            assertThrows(NullPointerException.class, () ->
                    new GradeRecord("Матан", 1, null, Grade.GOOD, date));
        }

        @Test
        @DisplayName("RecordBook: запрещает добавлять оценки за будущий семестр и менять список напрямую")
        void recordBookInvariants() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 2, fitPlan);

            assertThrows(IllegalArgumentException.class, () ->
                    book.addRecord(new GradeRecord("ООП", 3, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 20))));

            assertThrows(UnsupportedOperationException.class, () ->
                    book.getRecords().add(new GradeRecord("ООП", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2025, 1, 20))));
        }

        @Test
        @DisplayName("RecordBook: нельзя перейти дальше 8-го семестра")
        void cannotAdvancePastSemesterEight() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 8, fitPlan);
            assertThrows(IllegalStateException.class, book::advanceSemester);
        }

        @Test
        @DisplayName("CurriculumPlan: проверка стандартного плана ФИТ")
        void curriculumPlanValidation() {
            assertEquals(19, sumAcrossSemesters(fitPlan, ControlType.EXAM));
            assertEquals(35, sumAcrossSemesters(fitPlan, ControlType.DIFF_CREDIT));
            assertEquals(6, fitPlan.getDiplomaControlsCount(1));
            assertEquals(0, fitPlan.getControlCount(8, ControlType.EXAM));

            assertThrows(IllegalArgumentException.class, () ->
                    fitPlan.setControlCount(0, ControlType.EXAM, 1));
            assertThrows(IllegalArgumentException.class, () ->
                    fitPlan.setControlCount(1, ControlType.EXAM, -1));
        }

        private int sumAcrossSemesters(CurriculumPlan plan, ControlType type) {
            int sum = 0;
            for (int sem = 1; sem <= CurriculumPlan.TOTAL_SEMESTERS; sem++) {
                sum += plan.getControlCount(sem, type);
            }
            return sum;
        }
    }

    @Nested
    @DisplayName("1. Текущий средний балл")
    class AverageGradeTests {

        @Test
        @DisplayName("Пустая зачетка возвращает средний балл 0.0")
        void emptyBookReturnsZero() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 1, fitPlan);
            assertEquals(0.0, book.calculateCurrentAverageGrade(), 1e-9);
        }

        @Test
        @DisplayName("Средний балл учитывает числовые оценки и игнорирует обычные зачеты")
        void calculatesNumericAverageAndIgnoresCredits() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 1, fitPlan);
            book.addRecord(new GradeRecord("ООП", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 10)));
            book.addRecord(new GradeRecord("ОС", 1, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2026, 1, 12)));
            book.addRecord(new GradeRecord("Алгебра", 1, ControlType.TEST, Grade.SATISFACTORY, LocalDate.of(2025, 12, 10)));
            book.addRecord(new GradeRecord("Физкультура", 1, ControlType.CREDIT, Grade.PASSED, LocalDate.of(2025, 12, 20)));

            assertEquals(4.0, book.calculateCurrentAverageGrade(), 1e-9);
        }
    }

    @Nested
    @DisplayName("2. Перевод с платной формы на бюджет")
    class TransferToBudgetTests {

        @Test
        @DisplayName("Бюджетник не может перевестись на бюджет повторно")
        void budgetStudentCannotTransfer() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 3, fitPlan);
            assertFalse(book.canTransferToBudget());
            assertFalse(book.transferToBudget());
        }

        @Test
        @DisplayName("Нельзя перевестись после одной закрытой экзаменационной сессии")
        void cannotTransferAfterSingleSession() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.PAID, 3, fitPlan);

            book.addRecord(new GradeRecord("ДУ и ТФКП", 3, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2027, 1, 14)));
            book.addRecord(new GradeRecord("Введение в ИИ", 3, ControlType.EXAM, Grade.GOOD, LocalDate.of(2027, 1, 21)));

            assertFalse(book.canTransferToBudget());
        }

        @Test
        @DisplayName("Можно перевестись после 2 сессий без '3' за экзамены, даже если есть '3' за дифзачет")
        void canTransferWithSatisfactoryDiffCredit() {
            CurriculumPlan simplePlan = createSmallPlan(2, 2, 0);
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.PAID, 1, simplePlan);

            book.addRecord(new GradeRecord("Матан", 1, ControlType.EXAM, Grade.GOOD, LocalDate.of(2026, 1, 10)));
            book.addRecord(new GradeRecord("Алгебра", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 15)));
            book.addRecord(new GradeRecord("История", 1, ControlType.DIFF_CREDIT, Grade.SATISFACTORY, LocalDate.of(2025, 12, 25)));

            book.advanceSemester();
            book.addRecord(new GradeRecord("Дискретка", 2, ControlType.EXAM, Grade.GOOD, LocalDate.of(2026, 6, 10)));
            book.addRecord(new GradeRecord("Программирование", 2, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 6, 15)));

            assertTrue(book.canTransferToBudget());
            assertTrue(book.transferToBudget());
            assertEquals(StudyForm.BUDGET, book.getStudyForm());
            assertFalse(book.canTransferToBudget());
        }

        @Test
        @DisplayName("Тройка за экзамен в одной из последних двух сессий блокирует перевод")
        void satisfactoryExamInLastTwoSessionsBlocksTransfer() {
            CurriculumPlan simplePlan = createSmallPlan(1, 1, 0);
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.PAID, 1, simplePlan);

            book.addRecord(new GradeRecord("Матан", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 10)));
            book.advanceSemester();
            book.addRecord(new GradeRecord("ОС", 2, ControlType.EXAM, Grade.SATISFACTORY, LocalDate.of(2026, 6, 10)));

            assertFalse(book.canTransferToBudget());
        }

        @Test
        @DisplayName("Старая тройка за экзамен в 1-м семестре не мешает, если 2-й и 3-й закрыты на 4 и 5")
        void oldSatisfactoryExamDoesNotBlockAfterTwoCleanSessions() {
            CurriculumPlan simplePlan = createSmallPlan(1, 1, 1);
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.PAID, 1, simplePlan);

            book.addRecord(new GradeRecord("Матан", 1, ControlType.EXAM, Grade.SATISFACTORY, LocalDate.of(2026, 1, 10)));
            book.advanceSemester();
            book.addRecord(new GradeRecord("Алгебра", 2, ControlType.EXAM, Grade.GOOD, LocalDate.of(2026, 6, 10)));
            book.advanceSemester();
            book.addRecord(new GradeRecord("ООП", 3, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2027, 1, 10)));

            assertTrue(book.canTransferToBudget());
        }

        @Test
        @DisplayName("Академическая задолженность блокирует перевод до ее пересдачи")
        void academicDebtBlocksTransferUntilRetaken() {
            CurriculumPlan simplePlan = createSmallPlan(1, 1, 0);
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.PAID, 1, simplePlan);

            book.addRecord(new GradeRecord("Матан", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 10)));
            book.addRecord(new GradeRecord("Физкультура", 1, ControlType.CREDIT, Grade.FAILED, LocalDate.of(2025, 12, 25)));

            book.advanceSemester();
            book.addRecord(new GradeRecord("Алгебра", 2, ControlType.EXAM, Grade.GOOD, LocalDate.of(2026, 6, 10)));

            assertFalse(book.canTransferToBudget());

            book.addRecord(new GradeRecord("Физкультура", 2, ControlType.CREDIT, Grade.PASSED, LocalDate.of(2026, 6, 20)));
            assertTrue(book.canTransferToBudget());
        }
    }

    @Nested
    @DisplayName("3. Прогноз красного диплома")
    class RedDiplomaTests {

        @Test
        @DisplayName("Итоговая '3' по экзамену или дифзачету лишает права на красный диплом")
        void satisfactoryFinalGradePreventsRedDiploma() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 3, fitPlan);
            book.addRecord(new GradeRecord("ОС", 3, ControlType.DIFF_CREDIT, Grade.SATISFACTORY, LocalDate.of(2026, 12, 26)));

            assertFalse(book.canGetRedDiploma());
        }

        @Test
        @DisplayName("Перекрытие '3' более поздней оценкой '4' по тому же предмету возвращает шанс на красный диплом")
        void laterGradeOverridesEarlierSatisfactory() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 3, fitPlan);
            book.addRecord(new GradeRecord("ОС", 3, ControlType.DIFF_CREDIT, Grade.SATISFACTORY, LocalDate.of(2026, 12, 26)));
            assertFalse(book.canGetRedDiploma());

            book.advanceSemester();
            book.addRecord(new GradeRecord("ОС", 4, ControlType.EXAM, Grade.GOOD, LocalDate.of(2027, 6, 11)));
            assertTrue(book.canGetRedDiploma());
        }

        @Test
        @DisplayName("ВКР ниже 'отлично' запрещает красный диплом даже при 100% пятерок по остальным предметам")
        void thesisBelowExcellentPreventsRedDiploma() {
            CurriculumPlan smallPlan = new CurriculumPlan();
            smallPlan.setControlCount(1, ControlType.EXAM, 3);
            smallPlan.setControlCount(8, ControlType.THESIS_DEFENSE, 1);

            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 8, smallPlan);
            book.addRecord(new GradeRecord("Матан", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2025, 1, 10)));
            book.addRecord(new GradeRecord("Алгебра", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2025, 1, 15)));
            book.addRecord(new GradeRecord("ООП", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2025, 1, 20)));
            book.addRecord(new GradeRecord("ВКР", 8, ControlType.THESIS_DEFENSE, Grade.GOOD, LocalDate.of(2028, 6, 20)));

            assertFalse(book.canGetRedDiploma());
        }

        @Test
        @DisplayName("Прогноз порога 75%: точно работает на границе 3/4 (75%) и 2/4 (50%)")
        void forecastsSeventyFivePercentThreshold() {
            CurriculumPlan smallPlan = new CurriculumPlan();
            smallPlan.setControlCount(1, ControlType.EXAM, 2);
            smallPlan.setControlCount(2, ControlType.EXAM, 1);
            smallPlan.setControlCount(2, ControlType.THESIS_DEFENSE, 1);

            RecordBook borderlineBook = new RecordBook("1", "Студент 1", StudyForm.BUDGET, 1, smallPlan);
            borderlineBook.addRecord(new GradeRecord("Предмет 1", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 10)));
            borderlineBook.addRecord(new GradeRecord("Предмет 2", 1, ControlType.EXAM, Grade.GOOD, LocalDate.of(2026, 1, 15)));
            assertTrue(borderlineBook.canGetRedDiploma());

            RecordBook failedBook = new RecordBook("2", "Студент 2", StudyForm.BUDGET, 1, smallPlan);
            failedBook.addRecord(new GradeRecord("Предмет 1", 1, ControlType.EXAM, Grade.GOOD, LocalDate.of(2026, 1, 10)));
            failedBook.addRecord(new GradeRecord("Предмет 2", 1, ControlType.EXAM, Grade.GOOD, LocalDate.of(2026, 1, 15)));
            assertFalse(failedBook.canGetRedDiploma());
        }
    }

    @Nested
    @DisplayName("4. Повышенная стипендия в текущем семестре")
    class IncreasedScholarshipTests {

        @Test
        @DisplayName("Не положена платникам и первокурсникам в 1-м семестре")
        void deniedForPaidAndFirstSemester() {
            RecordBook paidBook = new RecordBook("1", "Платник", StudyForm.PAID, 2, fitPlan);
            paidBook.addRecord(new GradeRecord("Матан", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 10)));
            assertFalse(paidBook.canGetIncreasedScholarship());

            RecordBook firstSemBook = new RecordBook("2", "Первокурсник", StudyForm.BUDGET, 1, fitPlan);
            assertFalse(firstSemBook.canGetIncreasedScholarship());
        }

        @Test
        @DisplayName("Положена бюджетнику, закрывшему прошлую сессию на одни '5' и 'Зачтено'")
        void grantedWhenPreviousSessionAllExcellent() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 1, fitPlan);
            book.addRecord(new GradeRecord("ООП", 1, ControlType.TASK, Grade.GOOD, LocalDate.of(2025, 11, 15)));
            book.addRecord(new GradeRecord("ООП", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 15)));
            book.addRecord(new GradeRecord("История", 1, ControlType.DIFF_CREDIT, Grade.EXCELLENT, LocalDate.of(2025, 12, 25)));
            book.addRecord(new GradeRecord("Физкультура", 1, ControlType.CREDIT, Grade.PASSED, LocalDate.of(2025, 12, 27)));

            book.advanceSemester();
            assertTrue(book.canGetIncreasedScholarship());
        }

        @Test
        @DisplayName("Не положена, если в прошлой сессии есть хотя бы одна '4' по экзамену или дифзачету")
        void deniedWhenPreviousSessionHasGood() {
            RecordBook book = new RecordBook("250627", "Иванов Иван Иванович", StudyForm.BUDGET, 1, fitPlan);
            book.addRecord(new GradeRecord("ООП", 1, ControlType.EXAM, Grade.EXCELLENT, LocalDate.of(2026, 1, 15)));
            book.addRecord(new GradeRecord("История", 1, ControlType.DIFF_CREDIT, Grade.GOOD, LocalDate.of(2025, 12, 25)));

            book.advanceSemester();
            assertFalse(book.canGetIncreasedScholarship());
        }
    }

    private CurriculumPlan createSmallPlan(int sem1Exams, int sem2Exams, int sem3Exams) {
        CurriculumPlan plan = new CurriculumPlan();
        plan.setControlCount(1, ControlType.EXAM, sem1Exams);
        plan.setControlCount(2, ControlType.EXAM, sem2Exams);
        plan.setControlCount(3, ControlType.EXAM, sem3Exams);
        return plan;
    }
}
