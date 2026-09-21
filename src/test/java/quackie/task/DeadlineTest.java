package quackie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests parsing and display of structured and free-form deadline values. */
class DeadlineTest {
    /** Verifies that ISO dates are parsed and formatted for users. */
    @Test
    void parsesIsoDate() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");

        assertEquals(LocalDate.of(2019, 10, 15), deadline.getDate());
        assertNull(deadline.getDateTime());
        assertEquals("[D][ ] submit report (by: Oct 15 2019)", deadline.toString());
    }

    /** Verifies that day-month-year values with times are parsed and formatted. */
    @Test
    void parsesDateAndTime() {
        Deadline deadline = new Deadline("attend meeting", "2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDateTime());
        assertNull(deadline.getDate());
        assertEquals("[D][ ] attend meeting (by: Dec 02 2019, 6:00 PM)", deadline.toString());
    }

    /** Verifies that malformed structured values are rejected. */
    @Test
    void rejectsInvalidStructuredDate() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("report", "2019-02-30"));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("report", "2/12/2019 2500"));
    }

    /** Verifies legacy free-form deadlines remain available for backwards compatibility. */
    @Test
    void preservesFreeFormDeadline() {
        Deadline deadline = new Deadline("return book", "next Sunday");

        assertNull(deadline.getDate());
        assertNull(deadline.getDateTime());
        assertEquals("next Sunday", deadline.getBy());
        assertEquals("[D][ ] return book (by: next Sunday)", deadline.toString());
    }
}
