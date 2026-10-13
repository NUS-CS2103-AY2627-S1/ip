package ruby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 26, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 26, 16, 0);

    @Test
    void newEvent_isNotDone() {
        Event event = new Event("project meeting", START, END);
        assertEquals(" ", event.getStatusIcon());
    }

    @Test
    void newEvent_toDataString_hasDoneFlagZero() {
        Event event = new Event("project meeting", START, END);
        assertEquals("E | 0 | project meeting | 2026-08-26T14:00 | 2026-08-26T16:00",
                event.toDataString());
    }

    @Test
    void toString_includesEventIconAndDescription() {
        Event event = new Event("project meeting", START, END);
        assertEquals("[E][ ] project meeting (from: Aug 26 2026, 14:00 to: Aug 26 2026, 16:00)",
                event.toString());
    }

    @Test
    void toString_showsDateOnlyWhenBothTimesAreMidnight() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 8, 26, 0, 0), LocalDateTime.of(2026, 8, 27, 0, 0));
        assertEquals("[E][ ] project meeting (from: Aug 26 2026 to: Aug 27 2026)",
                event.toString());
    }

    @Test
    void toString_formatsStartAndEndIndependently() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0), LocalDateTime.of(2026, 8, 27, 0, 0));
        assertEquals("[E][ ] project meeting (from: Aug 26 2026, 14:00 to: Aug 27 2026)",
                event.toString());
    }

    @Test
    void markAsDone_updatesStatusIconToX() {
        Event event = new Event("project meeting", START, END);
        event.markAsDone();
        assertEquals("X", event.getStatusIcon());
    }

    @Test
    void markAsDone_updatesToStringAndDataString() {
        Event event = new Event("project meeting", START, END);
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Aug 26 2026, 14:00 to: Aug 26 2026, 16:00)",
                event.toString());
        assertEquals("E | 1 | project meeting | 2026-08-26T14:00 | 2026-08-26T16:00",
                event.toDataString());
    }

    @Test
    void markAsNotDone_afterBeingDone_revertsStatus() {
        Event event = new Event("project meeting", START, END);
        event.markAsDone();
        event.markAsNotDone();
        assertEquals(" ", event.getStatusIcon());
        assertEquals("[E][ ] project meeting (from: Aug 26 2026, 14:00 to: Aug 26 2026, 16:00)",
                event.toString());
    }
}
