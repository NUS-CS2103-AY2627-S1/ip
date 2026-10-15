package alice.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests creation of future recurring task occurrences. */
class RecurringTaskTest {
    /** Verifies that Java calendar adjustment is used for monthly deadlines. */
    @Test
    void createNextOccurrence_monthlyDeadline_usesCalendarAdjustment() {
        Deadline deadline = new Deadline("pay rent", LocalDate.of(2027, 1, 31));
        deadline.setRecurrence(Recurrence.MONTHLY);

        Deadline nextDeadline = (Deadline) deadline.createNextOccurrence(
                LocalDateTime.of(2027, 2, 10, 12, 0));

        assertEquals(LocalDate.of(2027, 2, 28), nextDeadline.getBy());
        assertEquals(Recurrence.MONTHLY, nextDeadline.getRecurrence());
    }

    /** Verifies that recurring events shift both endpoints by the same interval. */
    @Test
    void createNextOccurrence_yearlyEvent_shiftsBothEndpoints() {
        Event event = new Event("conference", LocalDateTime.of(2024, 2, 29, 9, 0),
                LocalDateTime.of(2024, 3, 2, 17, 0));
        event.setRecurrence(Recurrence.YEARLY);

        Event nextEvent = (Event) event.createNextOccurrence(LocalDateTime.of(2024, 8, 1, 12, 0));

        assertEquals(LocalDateTime.of(2025, 2, 28, 9, 0), nextEvent.getFrom());
        assertEquals(LocalDateTime.of(2025, 3, 2, 17, 0), nextEvent.getTo());
        assertEquals("[E][ ] conference (from: Feb 28 2025 9:00 am to: Mar 02 2025 5:00 pm)"
                + " (repeats: yearly)", nextEvent.toString());
    }
}
