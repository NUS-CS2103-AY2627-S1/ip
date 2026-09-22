package yappa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import yappa.exception.YappaException;

/**
 * Tests event time validation and serialization.
 */
public class EventTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 12, 2, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 12, 2, 16, 0);

    /**
     * Verifies that a valid event is created and displayed correctly.
     *
     * @throws YappaException If event validation unexpectedly fails.
     */
    @Test
    public void constructor_endAfterStart_createsEvent() throws YappaException {
        Event event = new Event("project meeting", START, END);

        assertEquals(
                "[E] [ ] project meeting (from: Dec 02 2026, 2:00 PM to: Dec 02 2026, 4:00 PM)",
                event.toString());
    }

    /**
     * Verifies that zero-length and backwards events are rejected.
     */
    @Test
    public void constructor_endEqualToOrBeforeStart_throwsYappaException() {
        assertThrows(YappaException.class,
                () -> new Event("zero-length meeting", START, START));
        assertThrows(YappaException.class,
                () -> new Event("backwards meeting", START, START.minusMinutes(1)));
    }

    /**
     * Verifies serialization of a completed event.
     *
     * @throws YappaException If event validation unexpectedly fails.
     */
    @Test
    public void toFileString_completedEvent_returnsStorageRecord() throws YappaException {
        Event event = new Event("project meeting", true, START, END);

        assertEquals(
                "E | 1 | project meeting | 2026-12-02T14:00 | 2026-12-02T16:00",
                event.toFileString());
    }
}
