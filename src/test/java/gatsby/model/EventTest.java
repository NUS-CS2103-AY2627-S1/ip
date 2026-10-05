package gatsby.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the event-specific display and save-file representations. */
class EventTest {

    private static final LocalDateTime START = LocalDateTime.of(2019, 12, 2, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2019, 12, 2, 16, 0);

    /** Verifies the human-readable and persisted forms of an unfinished event. */
    @Test
    void unfinishedEvent_formatsTimesAndPendingStatus() {
        Event event = new Event("project meeting", START, END);

        assertEquals("[E][ ] project meeting (from: Dec 02 2019 14:00:00 "
                + "to: Dec 02 2019 16:00:00)", event.toString());
        assertEquals("E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00",
                event.toFileFormat());
    }

    /** Verifies that a completed event retains both times and records its done status. */
    @Test
    void finishedEvent_formatsTimesAndDoneStatus() {
        Event event = new Event("project meeting", START, END);
        event.markDone();

        assertEquals("[E][X] project meeting (from: Dec 02 2019 14:00:00 "
                + "to: Dec 02 2019 16:00:00)", event.toString());
        assertEquals("E | 1 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00",
                event.toFileFormat());
    }
}
