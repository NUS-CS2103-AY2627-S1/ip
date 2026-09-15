package jaku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Verifies that recurring task occurrences move by their configured intervals. */
class RecurringTaskTest {
    /** Verifies daily todos advance and reverse by one calendar day. */
    @Test
    void recurringTodo_movesByDailyInterval() {
        RecurringTodo task = new RecurringTodo("review notes", LocalDate.of(2026, 9, 10), Recurrence.DAILY);

        task.advanceOccurrence();
        assertEquals(LocalDate.of(2026, 9, 11), task.getNextOccurrence());

        task.reverseOccurrence();
        assertEquals(LocalDate.of(2026, 9, 10), task.getNextOccurrence());
    }

    /** Verifies weekly events move both ends of the event by seven days. */
    @Test
    void recurringEvent_movesByWeeklyInterval() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 10, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 10, 15, 30);
        RecurringEvent task = new RecurringEvent("project meeting", start, end, Recurrence.WEEKLY);

        task.advanceOccurrence();
        assertEquals(LocalDateTime.of(2026, 9, 17, 14, 0), task.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 17, 15, 30), task.getTo());

        task.reverseOccurrence();
        assertEquals(start, task.getFrom());
        assertEquals(end, task.getTo());
    }

    /** Verifies recurrence labels ignore case and unsupported labels remain invalid. */
    @Test
    void recurrenceFromLabel_handlesCaseAndUnsupportedValues() {
        assertEquals(Recurrence.DAILY, Recurrence.fromLabel("DAILY"));
        assertEquals(Recurrence.WEEKLY, Recurrence.fromLabel("weekly"));
        assertThrows(IllegalArgumentException.class, () -> Recurrence.fromLabel("monthly"));
    }
}
