package jimbo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jimbo.exception.JimboException;

/**
 * Tests {@link Deadline}, specifically the {@code Deadline(String, String)}
 * constructor, which is the only place a user-typed date/time string is
 * parsed and validated against the "d/M/yyyy HHmm" format. The
 * {@code Deadline(String, LocalDateTime)} constructor and the formatting
 * methods are not covered here, as they involve no parsing/validation logic.
 */
public class DeadlineTest {
    @Test
    public void deadline_validByTime_parsesDateTimeCorrectly() throws JimboException {
        Deadline deadline = new Deadline("submit report", "2/12/2019 1800");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("D | 0 | submit report | 2019-12-02T18:00:00", deadline.toSaveFormat());
    }

    @Test
    public void deadline_singleDigitDayAndMonth_parsesDateTimeCorrectly() throws JimboException {
        Deadline deadline = new Deadline("submit report", "2/1/2019 0900");

        assertEquals("D | 0 | submit report | 2019-01-02T09:00:00", deadline.toSaveFormat());
    }

    @Test
    public void deadline_emptyByTime_exceptionThrown() {
        assertThrows(JimboException.class, () -> new Deadline("submit report", ""));
    }

    @Test
    public void deadline_nonDateByTime_exceptionThrown() {
        assertThrows(JimboException.class, () -> new Deadline("submit report", "tomorrow"));
    }

    @Test
    public void deadline_wrongDateFormat_exceptionThrown() {
        // Missing the HHmm time component required by the "d/M/yyyy HHmm" format.
        assertThrows(JimboException.class, () -> new Deadline("submit report", "2/12/2019"));
    }

    @Test
    public void deadline_invalidByTime_exceptionMessageIncludesOffendingValue() {
        JimboException exception = assertThrows(JimboException.class, () -> new Deadline("submit report", "tomorrow"));

        assertTrue(exception.getMessage().contains("tomorrow"));
    }
}
