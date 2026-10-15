package spoon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spoon.exception.InvalidFormatException;

public class DeadlineTest {

    private Deadline deadline;

    @BeforeEach
    void setUp() throws Exception {
        deadline = new Deadline("submit essay", "01/01/0001 0101");
    }

    @Nested
    class ConstructorTests {

        @Test
        void constructor_invalidDateFormat_throwsInvalidFormatException() {
            assertThrows(InvalidFormatException.class, () ->
                    new Deadline("submit essay", "invalid-date"));
        }
    }

    @Nested
    class DateEvaluationTests {

        @Test
        void isDueOn_matchingDate_returnsTrue() {
            assertTrue(deadline.isDueOn(LocalDate.of(1, 1, 1)));
        }

        @Test
        void isDueOn_differentDate_returnsFalse() {
            assertFalse(deadline.isDueOn(LocalDate.of(1, 1, 2)));
        }

        @Test
        void isDueBy_targetDateAfterDeadline_returnsTrue() {
            assertTrue(deadline.isDueBy(LocalDate.of(1, 1, 1)));
            assertTrue(deadline.isDueBy(LocalDate.of(1, 1, 2)));
        }

        @Test
        void isDueBy_targetDateBeforeDeadline_returnsFalse() {
            assertFalse(deadline.isDueBy(LocalDate.of(1, 1, 1).minusDays(1)));
        }
    }

    @Nested
    class FormattingTests {

        @Test
        void format_uncompleted_returnsCorrectStorageFormat() {
            assertEquals("D | 0 | submit essay | 01/01/0001 0101", deadline.format());
        }

        @Test
        void format_completed_returnsCorrectStorageFormat() {
            deadline.complete();
            assertEquals("D | 1 | submit essay | 01/01/0001 0101", deadline.format());
        }

        @Test
        void toString_uncompleted_returnsCorrectDisplayFormat() {
            assertEquals("[D][ ] submit essay (by: Jan 01 0001, 1:01am)", deadline.toString());
        }

        @Test
        void toString_completed_returnsCorrectDisplayFormat() {
            deadline.complete();
            assertEquals("[D][X] submit essay (by: Jan 01 0001, 1:01am)", deadline.toString());
        }

        @Test
        void toString_dateOnly_returnsDisplayFormatWithoutTime() throws Exception {
            Deadline dateOnlyDeadline = new Deadline("read chapter", "01/01/0001");
            assertEquals("[D][ ] read chapter (by: Jan 01 0001)", dateOnlyDeadline.toString());
        }
    }

    @Nested
    class EqualityAndHashCodeTests {

        @Test
        void equals_sameDescriptionAndDate_returnsTrueAndSameHashCode() throws Exception {
            Deadline sameDeadline = new Deadline("submit essay", "01/01/0001 0101");
            assertEquals(deadline, sameDeadline);
            assertEquals(deadline.hashCode(), sameDeadline.hashCode());
        }

        @Test
        void equals_caseInsensitiveDescriptionSameDate_returnsTrue() throws Exception {
            Deadline caseDeadline = new Deadline("SUBMIT ESSAY", "01/01/0001 0101");
            assertEquals(deadline, caseDeadline);
            assertEquals(deadline.hashCode(), caseDeadline.hashCode());
        }

        @Test
        void equals_differentDate_returnsFalse() throws Exception {
            Deadline differentDate = new Deadline("submit essay", "02/01/0001 0101");
            assertNotEquals(deadline, differentDate);
        }

        @Test
        void equals_differentDescription_returnsFalse() throws Exception {
            Deadline differentDesc = new Deadline("write code", "01/01/0001 0101");
            assertNotEquals(deadline, differentDesc);
        }

        @Test
        void equals_differentTaskSubclass_returnsFalse() {
            ToDo todo = new ToDo("submit essay");
            assertNotEquals(deadline, todo);
        }
    }
}
