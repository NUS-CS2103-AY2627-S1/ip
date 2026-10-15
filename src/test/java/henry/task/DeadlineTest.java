package henry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests supported deadline formats and strict date validation in {@link Deadline}.
 */
public class DeadlineTest {
    @Test
    public void parseBy_dateAndTime_returnsParsedDateTime() {
        LocalDateTime result = Deadline.parseBy("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    public void parseBy_singleDigitDateAndTime_returnsParsedDateTime() {
        LocalDateTime result = Deadline.parseBy("1/2/2025 0905");

        assertEquals(LocalDateTime.of(2025, 2, 1, 9, 5), result);
    }

    @Test
    public void parseBy_isoDate_returnsStartOfDay() {
        LocalDateTime result = Deadline.parseBy("2019-12-02");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), result);
    }

    @Test
    public void parseBy_leapDay_returnsParsedDateTime() {
        LocalDateTime result = Deadline.parseBy("29/2/2024 2359");

        assertEquals(LocalDateTime.of(2024, 2, 29, 23, 59), result);
    }

    @Test
    public void parseBy_nonLeapDay_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                Deadline.parseBy("29/2/2023 1200"));
    }

    @Test
    public void parseBy_invalidTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                Deadline.parseBy("2/12/2019 2400"));
    }

    @Test
    public void parseBy_unsupportedFormat_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                Deadline.parseBy("December 2, 2019"));
    }

    @Test
    public void toFileString_markedDeadlineWithSpecialCharacters_escapesDescription() {
        Deadline deadline = new Deadline(
                "submit | draft \\ report", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.setDone(true);

        assertEquals("D | 1 | submit \\| draft \\\\ report | 2019-12-02T18:00",
                deadline.toFileString());
    }

    @Test
    public void toString_deadline_returnsReadableDateAndTime() {
        Deadline deadline = new Deadline(
                "submit report", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("[D][ ] submit report (by: Dec 2 2019 6:00 PM)", deadline.toString());
    }
}
