package loki.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the shared interval and completion contracts of event constructors. */
class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime END = START.plusHours(2);

    @Test
    void constructor_validOverloads_preserveScheduleAndCompletion() {
        Event unfinished = new Event("Meeting", START, END);
        Event completed = new Event("Meeting", true, START, END);

        assertFalse(unfinished.isDone());
        assertTrue(completed.isDone());
        assertEquals(unfinished.saveString(), new Event("Meeting", false, START, END).saveString());
        assertEquals(unfinished.saveString(), new Event("Meeting", 0, START, END).saveString());
        assertEquals(completed.saveString(), new Event("Meeting", 1, START, END).saveString());
        assertEquals("E | 0 | Meeting | 2019-08-06T14:00:00 -> 2019-08-06T16:00:00", unfinished.saveString());
    }

    @Test
    void constructor_textOverloads_preserveScheduleAndCompletion() {
        Event unfinished = new Event("Meeting", "2019-08-06 1400", "2019-08-06 1600");
        Event completed = new Event("Meeting", true, "2019-08-06 1400", "2019-08-06 1600");

        assertEquals(new Event("Meeting", START, END).saveString(), unfinished.saveString());
        assertEquals(new Event("Meeting", true, START, END).saveString(), completed.saveString());
        assertEquals(completed.saveString(),
                new Event("Meeting", 1, "2019-08-06T14:00:00", "2019-08-06T16:00:00").saveString());
    }

    @Test
    void constructor_equalEndpoints_acceptsZeroDuration() {
        assertEquals(new Event("Meeting", START, START).saveString(),
                new Event("Meeting", false, START, START).saveString());
        assertEquals(new Event("Meeting", START, START).saveString(),
                new Event("Meeting", 0, START, START).saveString());
    }

    @Test
    void constructor_reversedInterval_rejectsEveryOverload() {
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", END, START));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", true, END, START));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", 1, END, START));
    }

    @Test
    void constructor_missingEndpoint_rejectsEveryOverload() {
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", null, END));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", START, null));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", false, null, END));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", false, START, null));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", 0, null, END));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", 0, START, null));
    }

    @Test
    void constructor_invalidNumericStatusOrTitle_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", -1, START, END));
        assertThrows(IllegalArgumentException.class, () -> new Event("Meeting", 2, START, END));
        assertThrows(IllegalArgumentException.class, () -> new Event("", START, END));
        assertThrows(IllegalArgumentException.class, () -> new Event("", true, START, END));
        assertThrows(IllegalArgumentException.class, () -> new Event("", 1, START, END));
    }
}
