package rem.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching for deadlines and events.
 */
public class ScheduledTaskTest {
    @Test
    public void deadlineOccursOn_dueDate_trueReturned() {
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 8, 29, 23, 59));

        assertTrue(deadline.occursOn(LocalDate.of(2026, 8, 29)));
    }

    @Test
    public void deadlineOccursOn_otherDate_falseReturned() {
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 8, 29, 23, 59));

        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    public void eventOccursOn_startMiddleAndEndDates_trueReturned() {
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 28, 23, 0),
                LocalDateTime.of(2026, 8, 30, 1, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 28)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 29)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    public void eventOccursOn_beforeOrAfterEvent_falseReturned() {
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 28, 23, 0),
                LocalDateTime.of(2026, 8, 30, 1, 0));

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 27)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 31)));
    }

    @Test
    public void constructors_invalidSchedulingDetails_assertionErrorThrown() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 30, 9, 0);
        LocalDateTime earlierEnd = LocalDateTime.of(2026, 8, 29, 17, 0);

        assertThrows(AssertionError.class, () -> new Deadline("submit", null));
        assertThrows(AssertionError.class, () -> new Event("conference", null, start));
        assertThrows(AssertionError.class, () -> new Event("conference", start, null));
        assertThrows(AssertionError.class, () -> new Event("conference", start, earlierEnd));
    }

    @Test
    public void toString_scheduledTasksWithNotes_noteShownAfterSchedule() {
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 8, 29, 18, 0));
        deadline.setNote("Include appendix");
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 30, 9, 0),
                LocalDateTime.of(2026, 8, 30, 17, 0));
        event.setNote("Bring pass");

        assertEquals("[D][ ] submit (by: Aug 29 2026, 6:00 PM)\n  Note: Include appendix",
                deadline.toString());
        assertEquals("[E][ ] conference (from: Aug 30 2026, 9:00 AM "
                + "to: Aug 30 2026, 5:00 PM)\n  Note: Bring pass", event.toString());
    }
}
