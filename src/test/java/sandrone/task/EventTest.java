package sandrone.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests date matching for {@link Event}. */
public class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 30, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 2, 16, 0);

    @Test
    public void occursOn_dateBeforeEvent_returnsFalse() {
        Event event = new Event("project meeting", START, END);

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 29)));
    }

    @Test
    public void occursOn_startDate_returnsTrue() {
        Event event = new Event("project meeting", START, END);

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    public void occursOn_dateDuringEvent_returnsTrue() {
        Event event = new Event("project meeting", START, END);

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 31)));
    }

    @Test
    public void occursOn_endDate_returnsTrue() {
        Event event = new Event("project meeting", START, END);

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    public void occursOn_dateAfterEvent_returnsFalse() {
        Event event = new Event("project meeting", START, END);

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 3)));
    }

    @Test
    public void toString_newEvent_returnsFormattedEvent() {
        Event event = new Event("project meeting", START, END);

        assertEquals("[E][ ] project meeting (from: 30/8/2026 2:00PM to: 2/9/2026 4:00PM)", event.toString());
    }

    @Test
    public void toString_completedEvent_showsCompletedStatus() {
        Event event = new Event("project meeting", START, END);
        event.markAsDone();

        assertEquals("[E][X] project meeting (from: 30/8/2026 2:00PM to: 2/9/2026 4:00PM)", event.toString());
    }

    @Test
    public void toFileFormat_newEvent_returnsSavedRepresentation() {
        Event event = new Event("project meeting", START, END);

        assertEquals("E | 0 | project meeting | 30/8/2026 2:00PM | 2/9/2026 4:00PM", event.toFileFormat());
    }

    @Test
    public void toFileFormat_completedEvent_savesCompletedStatus() {
        Event event = new Event("project meeting", START, END);
        event.markAsDone();

        assertEquals("E | 1 | project meeting | 30/8/2026 2:00PM | 2/9/2026 4:00PM", event.toFileFormat());
    }
}
