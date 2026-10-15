package doge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests event-specific display and persistence behavior. */
class EventTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 6, 5, 12, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 6, 5, 13, 30);

    /** Verifies the display format of an event. */
    @Test
    void toString_event_includesStartAndEndTimes() {
        Event event = new Event("Team lunch", START, END);

        assertEquals("[E][ ] Team lunch (from: Jun 05 2026, 12:00 PM to: Jun 05 2026, 1:30 PM)",
                event.toString());
    }

    /** Verifies that an event persists both endpoints and completion status. */
    @Test
    void toStorageString_completedEvent_includesStatusAndTimes() {
        Event event = new Event("Team lunch", START, END);
        event.markDone();

        assertEquals("E | 1 | Team lunch | 2026-06-05T12:00 | 2026-06-05T13:30", event.toStorageString());
    }

    /** Verifies that event priority is included in persistence output. */
    @Test
    void toStorageString_prioritizedEvent_includesPriority() {
        Event event = new Event("Team lunch", START, END);
        event.setPriority(3);

        assertEquals("E | 0 | Team lunch | 2026-06-05T12:00 | 2026-06-05T13:30 | 3", event.toStorageString());
    }

    /** Verifies that an event cannot end at or before its start time. */
    @Test
    void constructor_endNotLaterThanStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("Team lunch", END, START));
        assertThrows(IllegalArgumentException.class, () -> new Event("Team lunch", START, START));
    }
}
