package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests event construction, typed times, and display text. */
public class EventTest {
    @Test
    public void constructor_validStartAndEnd_storesTimes() {
        Event event = new Event("project meeting", "2019-12-09 1400", "2019-12-09 1600");

        assertEquals(LocalDateTime.of(2019, 12, 9, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 9, 16, 0), event.getTo());
        assertEquals("[E][ ] project meeting (from: Dec 09 2019 14:00 to: Dec 09 2019 16:00)",
                event.toString());
    }

    @Test
    public void constructor_endBeforeStart_throwsException() {
        JaylenException exception = assertThrows(JaylenException.class, () ->
                new Event("project meeting", "2019-12-09 1600", "2019-12-09 1400"));

        assertEquals("The event end time must be after the start time.", exception.getMessage());
    }

    @Test
    public void constructor_equalStartAndEnd_throwsException() {
        JaylenException exception = assertThrows(JaylenException.class, () ->
                new Event("project meeting", "2019-12-09 1400", "2019-12-09 1400"));

        assertEquals("The event end time must be after the start time.", exception.getMessage());
    }
}
