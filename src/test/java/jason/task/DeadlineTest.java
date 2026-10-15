package jason.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests deadline date-time parsing and display behavior. */
class DeadlineTest {
    @Test
    void constructor_isoDateTime_storesDateTimeAndFormatsDisplay() {
        Deadline deadline = new Deadline("submit report", "2019-10-15 18:00");

        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), deadline.getDeadline());
        assertEquals("[D] [ ] submit report (by: Oct 15 2019 18:00)", deadline.toString());
    }

    @Test
    void constructor_dateOnly_defaultsToMidnight() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), deadline.getDeadline());
    }

    @Test
    void constructor_invalidDate_throwsException() {
        assertThrows(DateTimeParseException.class, () ->
                new Deadline("submit report", "not-a-date"));
    }
}
