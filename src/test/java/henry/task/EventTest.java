package henry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests event chronology, storage, and user-facing date formatting.
 */
public class EventTest {
    @Test
    public void constructor_endAfterStart_createsEvent() {
        Event event = new Event("meeting", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 15, 0));

        assertEquals("E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T15:00",
                event.toFileString());
        assertEquals("[E][ ] meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 3:00 PM)",
                event.toString());
    }

    @Test
    public void constructor_endAtStart_exceptionThrown() {
        LocalDateTime time = LocalDateTime.of(2019, 12, 2, 14, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", time, time));
    }
}
