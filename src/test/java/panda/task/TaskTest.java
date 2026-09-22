package panda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import panda.exception.InvalidDateException;
import panda.exception.InvalidEventPeriodException;

/**
 * Tests behavior shared by task types and the date-specific behavior of dated tasks.
 */
class TaskTest {

    @Test
    void todo_newTask_hasExpectedInitialStateAndFormats() {
        Todo todo = new Todo("read book");

        assertEquals("read book", todo.getName());
        assertEquals(TaskType.TODO, todo.getType());
        assertEquals("read book", todo.getDisplayText());
        assertEquals("T | 0 | read book", todo.toDataString());
        assertFalse(todo.isDone());
        assertFalse(todo.occursOn(LocalDate.of(2026, 9, 14)));
    }

    @Test
    void markAndUnmark_repeatedCalls_setExpectedStatus() {
        Todo todo = new Todo("read book");

        todo.mark();
        todo.mark();
        assertTrue(todo.isDone());
        assertEquals("T | 1 | read book", todo.toDataString());

        todo.unmark();
        todo.unmark();
        assertFalse(todo.isDone());
        assertEquals("T | 0 | read book", todo.toDataString());
    }

    @Test
    void rename_validName_updatesDisplayAndStoredText() {
        Todo todo = new Todo("read book");

        todo.rename("read Java book");

        assertEquals("read Java book", todo.getName());
        assertEquals("read Java book", todo.getDisplayText());
        assertEquals("T | 0 | read Java book", todo.toDataString());
    }

    @Test
    void toDataString_specialCharacters_escapesPipesAndBackslashes() {
        Todo todo = new Todo("read A | B \\ notes");

        assertEquals("T | 0 | read A \\| B \\\\ notes", todo.toDataString());
    }

    @Test
    void deadline_validDate_formatsAndMatchesOnlyItsDate() throws InvalidDateException {
        Deadline deadline = new Deadline("submit report", "2028-02-29 08:05");

        assertEquals(TaskType.DEADLINE, deadline.getType());
        assertEquals("submit report (by: Feb 29 2028 08:05)", deadline.getDisplayText());
        assertEquals("D | 0 | submit report | 2028-02-29 08:05", deadline.toDataString());
        assertTrue(deadline.occursOn(LocalDate.of(2028, 2, 29)));
        assertFalse(deadline.occursOn(LocalDate.of(2028, 2, 28)));
    }

    @Test
    void deadline_invalidDates_throwInvalidDateException() {
        assertThrows(InvalidDateException.class, () ->
                new Deadline("submit report", "2025-02-29 10:00"));
        assertThrows(InvalidDateException.class, () ->
                new Deadline("submit report", "2026-09-14 24:00"));
        assertThrows(InvalidDateException.class, () ->
                new Deadline("submit report", "14-09-2026 10:00"));
    }

    @Test
    void deadline_invalidReschedule_keepsOriginalDate() throws InvalidDateException {
        Deadline deadline = new Deadline("submit report", "2026-09-14 10:00");

        assertThrows(InvalidDateException.class, () ->
                deadline.reschedule("not a date"));

        assertEquals("submit report (by: Sep 14 2026 10:00)", deadline.getDisplayText());
    }

    @Test
    void event_validInterval_formatsAndMatchesInclusiveDateRange()
            throws InvalidDateException {
        Event event = new Event("camp", "2026-09-14 09:00", "2026-09-16 17:30");

        assertEquals(TaskType.EVENT, event.getType());
        assertEquals("camp (from: Sep 14 2026 09:00 to: Sep 16 2026 17:30)",
                event.getDisplayText());
        assertEquals("E | 0 | camp | 2026-09-14 09:00 | 2026-09-16 17:30",
                event.toDataString());
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 14)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 15)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 16)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 13)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 17)));
    }

    @Test
    void event_invalidStartOrEnd_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () ->
                new Event("meeting", "bad start", "2026-09-14 11:00"));
        assertThrows(InvalidDateException.class, () ->
                new Event("meeting", "2026-09-14 10:00", "bad end"));
    }

    @Test
    void event_startAfterEnd_throwsInvalidEventPeriodException() {
        assertThrows(InvalidEventPeriodException.class, () ->
                new Event("meeting", "2026-09-14 11:00", "2026-09-14 10:00"));
    }

    @Test
    void event_equalStartAndEnd_createsEvent() throws InvalidDateException {
        Event event = new Event("instant meeting", "2026-09-14 10:00", "2026-09-14 10:00");

        assertEquals("instant meeting (from: Sep 14 2026 10:00 to: Sep 14 2026 10:00)",
                event.getDisplayText());
    }

    @Test
    void parseListDate_validAndInvalidValues_returnsDateOrThrows() throws InvalidDateException {
        assertEquals(LocalDate.of(2028, 2, 29), Task.parseListDate("2028-02-29"));
        assertThrows(InvalidDateException.class, () -> Task.parseListDate("2025-02-29"));
        assertThrows(InvalidDateException.class, () -> Task.parseListDate("2026-9-14"));
        assertThrows(InvalidDateException.class, () -> Task.parseListDate("2026-09-14 extra"));
    }

    @Test
    void displayDate_nonEnglishDefaultLocale_stillUsesEnglishMonth()
            throws InvalidDateException {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINESE);
            Deadline deadline = new Deadline("report", "2026-09-14 10:00");

            assertEquals("report (by: Sep 14 2026 10:00)", deadline.getDisplayText());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
