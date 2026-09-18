package blud.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import blud.exception.DateTimeInputException;

/** Tests parsing and formatting of task date and time values. */
public class DateUtilsTest {
    /** Verifies that valid input is parsed and formatted consistently. */
    @Test
    public void parseInput_validDate_returnsDateTime() {
        LocalDateTime dateTime = DateUtils.parseInput("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dateTime);
        assertEquals("2/12/2019 1800", DateUtils.formatForStorage(dateTime));
        assertEquals("Dec 02 2019, 6:00 pm", DateUtils.formatOutput(dateTime));
    }

    /** Verifies that missing and impossible dates are rejected. */
    @Test
    public void parseInput_invalidDate_throwsDateTimeInputException() {
        assertThrows(DateTimeInputException.class, () -> DateUtils.parseInput(""));
        assertThrows(DateTimeInputException.class, () -> DateUtils.parseInput("31/2/2025 1000"));
    }
}
