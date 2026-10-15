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

import spoon.exception.InvalidArgumentException;
import spoon.exception.InvalidFormatException;

public class EventTest {

    private Event event;

    @BeforeEach
    void setUp() throws Exception {
        event = new Event("project meeting", "01/01/0001 0001", "02/01/0001 0101");
    }

    @Nested
    class ConstructorTests {

        @Test
        void constructor_endBeforeStart_throwsInvalidArgumentException() {
            assertThrows(InvalidArgumentException.class, () ->
                    new Event("meeting", "02/01/0001 1200", "01/01/0001 1200"));
        }

        @Test
        void constructor_invalidDateFormat_throwsInvalidFormatException() {
            assertThrows(InvalidFormatException.class, () ->
                    new Event("meeting", "invalid-start", "02/01/0001 1200"));
            assertThrows(InvalidFormatException.class, () ->
                    new Event("meeting", "01/01/0001 1200", "invalid-end"));
        }

        @Test
        void constructor_startEqualsEnd_createsSuccessfully() throws Exception {
            Event sameTimeEvent = new Event("flash mob", "01/01/0001 1200", "01/01/0001 1200");
            assertEquals("E | 0 | flash mob | 01/01/0001 1200 | 01/01/0001 1200", sameTimeEvent.format());
        }
    }

    @Nested
    class DateEvaluationTests {

        @Test
        void isDueOn_dateWithinRange_returnsTrue() {
            assertTrue(event.isDueOn(LocalDate.of(1, 1, 1)));
            assertTrue(event.isDueOn(LocalDate.of(1, 1, 2)));
        }

        @Test
        void isDueOn_dateOutsideRange_returnsFalse() {
            assertFalse(event.isDueOn(LocalDate.of(1, 1, 3)));
            assertFalse(event.isDueOn(LocalDate.of(1, 1, 1).minusDays(1)));
        }

        @Test
        void isDueBy_targetDateOnOrAfterStart_returnsTrue() {
            assertTrue(event.isDueBy(LocalDate.of(1, 1, 1)));
            assertTrue(event.isDueBy(LocalDate.of(1, 1, 5)));
        }

        @Test
        void isDueBy_targetDateBeforeStart_returnsFalse() {
            assertFalse(event.isDueBy(LocalDate.of(1, 1, 1).minusDays(1)));
        }
    }

    @Nested
    class FormattingTests {

        @Test
        void format_uncompleted_returnsCorrectStorageFormat() {
            assertEquals("E | 0 | project meeting | 01/01/0001 0001 | 02/01/0001 0101", event.format());
        }

        @Test
        void format_completed_returnsCorrectStorageFormat() {
            event.complete();
            assertEquals("E | 1 | project meeting | 01/01/0001 0001 | 02/01/0001 0101", event.format());
        }

        @Test
        void toString_uncompleted_returnsCorrectDisplayFormat() {
            String expected = "[E][ ] project meeting (from: Jan 01 0001, 12:01am to: Jan 02 0001, 1:01am)";
            assertEquals(expected, event.toString());
        }

        @Test
        void toString_completed_returnsCorrectDisplayFormat() {
            event.complete();
            String expected = "[E][X] project meeting (from: Jan 01 0001, 12:01am to: Jan 02 0001, 1:01am)";
            assertEquals(expected, event.toString());
        }

        @Test
        void toString_dateOnlyFormat_displaysWithoutTime() throws Exception {
            Event dateOnlyEvent = new Event("camp", "01/01/0001", "03/01/0001");
            assertEquals("[E][ ] camp (from: Jan 01 0001 to: Jan 03 0001)", dateOnlyEvent.toString());
        }
    }

    @Nested
    class EqualityAndHashCodeTests {

        @Test
        void equals_sameDetails_returnsTrueAndSameHashCode() throws Exception {
            Event sameEvent = new Event("project meeting", "01/01/0001 0001", "02/01/0001 0101");
            assertEquals(event, sameEvent);
            assertEquals(event.hashCode(), sameEvent.hashCode());
        }

        @Test
        void equals_caseInsensitiveNameSameDates_returnsTrue() throws Exception {
            Event caseEvent = new Event("PROJECT MEETING", "01/01/0001 0001", "02/01/0001 0101");
            assertEquals(event, caseEvent);
            assertEquals(event.hashCode(), caseEvent.hashCode());
        }

        @Test
        void equals_differentStart_returnsFalse() throws Exception {
            Event diffStart = new Event("project meeting", "02/01/0001 0001", "02/01/0001 0101");
            assertNotEquals(event, diffStart);
        }

        @Test
        void equals_differentEnd_returnsFalse() throws Exception {
            Event diffEnd = new Event("project meeting", "01/01/0001 0001", "03/01/0001 0101");
            assertNotEquals(event, diffEnd);
        }

        @Test
        void equals_differentName_returnsFalse() throws Exception {
            Event diffName = new Event("workshop", "01/01/0001 0001", "02/01/0001 0101");
            assertNotEquals(event, diffName);
        }

        @Test
        void equals_differentTaskSubclass_returnsFalse() {
            ToDo todo = new ToDo("project meeting");
            assertNotEquals(event, todo);
        }
    }
}
