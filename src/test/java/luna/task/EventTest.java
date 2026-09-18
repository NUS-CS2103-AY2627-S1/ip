package luna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests validation, comparison, and formatting in {@link Event}.
 */
public class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 15, 9, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 15, 10, 30);

    @Test
    public void constructor_validRange_storesAndFormatsEvent() {
        Event event = new Event("team meeting", START, END);

        assertEquals(START, event.getFromDateTime());
        assertEquals(END, event.getToDateTime());
        assertEquals("E | 0 | team meeting | 2026-09-15T09:00 | 2026-09-15T10:30",
                event.toStorageString());
        assertEquals("[E][ ] team meeting (from: 15 Sep 2026, 9:00 AM to: 15 Sep 2026, 10:30 AM)",
                event.toString());
    }

    @Test
    public void constructor_endIsNotAfterStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", START, START));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", START, START.minusMinutes(1)));
    }

    @Test
    public void constructor_nullTime_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Event("meeting", null, END));
        assertThrows(NullPointerException.class, () -> new Event("meeting", START, null));
    }

    @Test
    public void hasSameDetails_allDetailsMatch_returnsTrue() {
        Event firstEvent = new Event("Team Meeting", START, END);
        Event secondEvent = new Event("team meeting", START, END);

        assertTrue(firstEvent.hasSameDetails(secondEvent));
    }

    @Test
    public void hasSameDetails_timeRangeDiffers_returnsFalse() {
        Event event = new Event("meeting", START, END);
        Event differentStart = new Event("meeting", START.plusMinutes(1), END);
        Event differentEnd = new Event("meeting", START, END.plusMinutes(1));

        assertFalse(event.hasSameDetails(differentStart));
        assertFalse(event.hasSameDetails(differentEnd));
    }
}
