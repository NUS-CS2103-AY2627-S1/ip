package ramly.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ramly.exception.TaskValidationException;

/** Tests deadline parsing, display formatting, and storage serialization. */
public class DeadlineTest {
    @Test
    public void constructor_dateOnly_storesStartOfDay() {
        Deadline deadline = new Deadline("return book", "2019-10-15");

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), deadline.getBy());
        assertEquals("[⏳][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void constructor_dateAndTime_formatsDisplayAndStorage() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("[⏳][ ] return book (by: Dec 02 2019, 6:00pm)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-12-02T18:00:00", deadline.toFileString());
    }

    @Test
    public void constructor_isoDateAndTime_parsesValue() {
        Deadline deadline = new Deadline("return book", "2019-12-02 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    public void fromStorage_canonicalDateTime_parsesValue() {
        Deadline deadline = Deadline.fromStorage("return book", "2019-12-02T18:00:00");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    public void constructor_invalidDate_throwsValidationException() {
        assertThrows(TaskValidationException.class,
                () -> new Deadline("return book", "not a date"));
    }

    @Test
    public void constructor_nonExistentDate_throwsValidationException() {
        assertThrows(TaskValidationException.class,
                () -> new Deadline("return book", "2026-02-30"));
    }
}
