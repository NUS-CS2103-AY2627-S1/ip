package lloyd.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests event construction and date-time validation.
 */
public class EventTest {

    /** Verifies that a newly created event starts incomplete. */
    @Test
    public void event_newEvent_isNotDone() {
        Event event = new Event(
                "Test event", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertFalse(event.isDone());
    }

    /** Verifies that an event cannot end before it starts. */
    @Test
    public void event_invalidEvent_throwsIllegalArgumentException() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.minusHours(1);
        assertThrows(IllegalArgumentException.class, () ->
                new Event("Invalid event", from, to));
    }
}
