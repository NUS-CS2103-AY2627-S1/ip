package xue.model;

import org.junit.jupiter.api.Test;
import xue.XueException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

/** Tests the observable behavior of {@link Task}. */
class TaskTest {

    @Test
    void createTask_defaultValues_areStoredAndIncomplete() {
        Task task = new Task("read book");

        assertEquals("T", task.getType());
        assertEquals("read book", task.getDescription());
        assertNull(task.getFrom());
        assertNull(task.getTo());
        assertNull(task.getFromDateTime());
        assertNull(task.getToDateTime());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void createTask_savedStatus_restoresCompletionState() {
        Task doneTask = new Task("T", "read book", null, null, true);

        assertTrue(doneTask.isDone());
        assertEquals("X", doneTask.getStatusIcon());
    }

    @Test
    void markAsDone_thenMarkAsNotDone_updatesStatus() {
        Task task = new Task("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void markAsDone_whenAlreadyDone_keepsTaskDone() {
        Task task = new Task("T", "read book", null, null, true);

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_whenAlreadyIncomplete_keepsTaskIncomplete() {
        Task task = new Task("read book");

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getDateTimeDescription_todo_returnsEmptyDescription() {
        Task task = new Task("read book");

        assertEquals("", task.getDateTimeDescription());
    }

    @Test
    void getDateTimeDescription_deadlineNaturalLanguage_keepsOriginalText() {
        Task task = new Task("D", "return book", null, "Sunday");

        assertEquals(" (by: Sunday)", task.getDateTimeDescription());
    }

    @Test
    void getDateTimeDescription_eventNaturalLanguage_keepsOriginalText() {
        Task task = new Task("E", "project meeting", "Mon 2pm", "4pm");

        assertEquals(" (from: Mon 2pm to: 4pm)", task.getDateTimeDescription());
    }

    @Test
    void getDateTimeDescription_deadlineDateOnly_formatsDate() {
        Task task = new Task("D", "submit form", null, "2019-10-15");

        assertEquals(" (by: Oct 15 2019)", task.getDateTimeDescription());
    }

    @Test
    void getDateTimeDescription_eventDateTime_formatsBothDateTimes() {
        Task task = new Task("E", "conference", "2019-10-15 0900", "2019-10-15 1800");

        assertEquals(" (from: Oct 15 2019 09:00 AM to: Oct 15 2019 06:00 PM)",
                task.getDateTimeDescription());
    }

    @Test
    void createTask_unknownType_throwsXueException() {
        assertThrows(XueException.class, () -> new Task("X", "custom task", null, null));
    }

    @Test
    void createTask_numericDates_parsesDateOnlyAndDateTimeValues() {
        Task deadline = new Task("D", "submit form", null, "2019-10-15");
        Task event = new Task("E", "conference", "2019-10-15 0900", "2019-10-15 1800");

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), deadline.getToDateTime());
        assertNull(deadline.getFromDateTime());
        assertEquals(LocalDateTime.of(2019, 10, 15, 9, 0), event.getFromDateTime());
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), event.getToDateTime());
    }

    @Test
    void createTask_numericDateAtMidnight_formatsWithoutTime() {
        Task task = new Task("D", "submit form", null, "2024-02-29");

        assertEquals(" (by: Feb 29 2024)", task.getDateTimeDescription());
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), task.getToDateTime());
    }

    @Test
    void createTask_invalidNumericDates_rejectsDeadlinesAndBothEventEndpoints() {
        String[] invalidDates = {"2026-09-31 1200", "2026-02-29 1200", "2026-09-31",
            "2026/09/24 1200", "15-10-2019", "2026.09.24", "2026-9-24 1200",
            "2026-09-24 12:00", "2026-09-24 2400", "2026-09-24 1260",
            "2026-09-24 1200 extra", "20260924", "", "   ", null};
        for (String value : invalidDates) {
            assertThrows(XueException.class, () -> new Task("D", "deadline", null, value), value);
            assertThrows(XueException.class, () -> new Task("E", "event", value, "2026-10-01"), value);
            assertThrows(XueException.class, () -> new Task("E", "event", "2026-09-01", value), value);
        }
    }

    @Test
    void createTask_eventEndsBeforeStart_rejectsDatesAndTimes() {
        assertThrows(XueException.class,
                () -> new Task("E", "event", "2026-09-26 1600", "2026-09-25 1400"));
        assertThrows(XueException.class,
                () -> new Task("E", "event", "2026-09-26 1600", "2026-09-26 1400"));
        assertThrows(XueException.class,
                () -> new Task("E", "event", "2026-09-26", "2026-09-25"));
        assertThrows(XueException.class,
                () -> new Task("E", "event", "2026-09-26 1600", "2026-09-26"));
    }

    @Test
    void createTask_validBoundaries_acceptsLeapDayMidnightAndEqualEndpoints() {
        Task deadline = new Task("D", "leap day", null, "2028-02-29 0000");
        assertEquals(LocalDateTime.of(2028, 2, 29, 0, 0), deadline.getToDateTime());
        Task event = new Task("E", "instant", "2026-09-30 2359", "2026-09-30 2359");
        assertEquals(event.getFromDateTime(), event.getToDateTime());
        Task overnight = new Task("E", "overnight", "2026-09-30 2359", "2026-10-01 0000");
        assertTrue(overnight.getToDateTime().isAfter(overnight.getFromDateTime()));
    }

    @Test
    void createTask_invalidDate_throwsXueException() {
        assertThrows(XueException.class,
                () -> new Task("D", "submit form", null, "2019-02-30"));
    }

    @Test
    void createTask_nullDescription_throwsXueException() {
        assertThrows(XueException.class, () -> new Task((String) null));
    }

    @Test
    void createTask_blankDescription_throwsXueException() {
        assertThrows(XueException.class, () -> new Task("   "));
    }
}
