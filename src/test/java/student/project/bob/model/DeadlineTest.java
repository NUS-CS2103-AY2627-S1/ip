package student.project.bob.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline parsing, state, and display formatting.
 */
public class DeadlineTest {
    /**
     * Verifies that a date-only deadline is parsed and formatted correctly.
     */
    @Test
    public void constructor_dateOnlyDeadline_storesAndFormatsDate() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Tue, Oct 15 2019)", deadline.toString());
    }

    /**
     * Verifies that a date-time deadline preserves its time and seconds in its display.
     */
    @Test
    public void constructor_dateTimeDeadline_storesAndFormatsDateTime() {
        Deadline deadline = new Deadline("submit report", "2019-10-15 18:00:05");

        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0, 5), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Tue, Oct 15 2019 18:00:05)", deadline.toString());
    }

    /**
     * Verifies that a completed deadline includes its completed status in the display.
     */
    @Test
    public void toString_completedDeadline_includesCompletedStatus() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");

        deadline.setDone();

        assertEquals("[D][X] submit report (by: Tue, Oct 15 2019)", deadline.toString());
    }

    /**
     * Verifies that an invalid deadline detail is rejected during construction.
     */
    @Test
    public void constructor_invalidDeadlineDetail_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("submit report", "not-a-date"));
    }
}
