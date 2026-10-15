package jason.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests event date-time parsing and display behavior. */
class EventTest {
    @Test
    void constructor_isoDateTimes_storesDateTimesAndFormatsDisplay() {
        Event event = new Event("planning", "2020-01-02 14:00", "2020-01-02 16:30");

        assertEquals(LocalDateTime.of(2020, 1, 2, 14, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2020, 1, 2, 16, 30), event.getEndDate());
        assertEquals("[E] [ ] planning (from: Jan 02 2020 14:00 to: Jan 02 2020 16:30)",
                event.toString());
    }

    @Test
    void constructor_invalidDate_throwsException() {
        assertThrows(DateTimeParseException.class, () ->
                new Event("planning", "2020-01-02 14:00", "not-a-date"));
    }
}
