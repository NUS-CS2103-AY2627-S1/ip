package judey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import judey.exception.JudeyException;

class EventTest {
    @Test
    void constructor_rejectsEqualStartAndEnd() {
        JudeyException exception = assertThrows(JudeyException.class,
                () -> new Event("meeting", "26/9/2026 1400", "26/9/2026 1400"));

        assertEquals("The event start date/time must be before its end date/time.", exception.getMessage());
    }

    @Test
    void constructor_rejectsEndBeforeStart() {
        JudeyException exception = assertThrows(JudeyException.class,
                () -> new Event("meeting", "26/9/2026 1500", "26/9/2026 1400"));

        assertEquals("The event start date/time must be before its end date/time.", exception.getMessage());
    }

    @Test
    void constructor_rejectsNonexistentCalendarDate() {
        JudeyException exception = assertThrows(JudeyException.class,
                () -> new Event("meeting", "30/2/2028 1400", "1/3/2028 1400"));

        assertEquals("The date/time you entered is invalid. Please use a real date in the format d/M/yyyy HHmm.",
                exception.getMessage());
    }

    @Test
    void validateStartIsNotPast_rejectsPastStart() throws Exception {
        Event event = new Event("meeting", "26/9/2026 1400", "26/9/2026 1500");

        JudeyException exception = assertThrows(JudeyException.class,
                () -> event.validateStartIsNotPast(LocalDateTime.of(2026, 9, 26, 14, 1)));

        assertEquals("The event start date/time cannot be in the past.", exception.getMessage());
    }

    @Test
    void validateStartIsNotPast_acceptsStartAtCurrentTime() throws Exception {
        Event event = new Event("meeting", "26/9/2026 1400", "26/9/2026 1500");

        event.validateStartIsNotPast(LocalDateTime.of(2026, 9, 26, 14, 0));
    }
}
