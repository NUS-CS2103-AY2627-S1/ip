package probe.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {

    private static final LocalDateTime FROM =
            LocalDateTime.of(2026, 8, 28, 10, 0);
    private static final LocalDateTime TO =
            LocalDateTime.of(2026, 8, 28, 11, 0);

    @Test
    void toStringIncludesDescriptionAndEventTimes() {
        Event event = new Event("Team meeting", FROM, TO);

        assertEquals("[E][ ] Team meeting (from: Aug 28 2026, 10:00am "
                        + "to: Aug 28 2026, 11:00am)", event.toString());
    }

    @Test
    void toFileFormatIncludesIncompleteStatus() {
        Event event = new Event("Team meeting", FROM, TO);

        assertEquals("E | 0 | Team meeting | 2026-08-28T10:00 | 2026-08-28T11:00",
                event.toFileFormat());
    }

    @Test
    void toFileFormatIncludesCompletedStatus() {
        Event event = new Event("Team meeting", FROM, TO);
        event.markAsDone();

        assertEquals("E | 1 | Team meeting | 2026-08-28T10:00 | 2026-08-28T11:00",
                event.toFileFormat());
    }
}
