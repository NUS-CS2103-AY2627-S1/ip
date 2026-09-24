package hampster.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class DateTimeParserTest {

    @Test
    void parse_validAssignmentDeadline_returnsExpectedDateTime() {
        LocalDateTime result = DateTimeParser.parse("15/9/2026 1730");

        assertEquals(
                LocalDateTime.of(2026, 9, 15, 17, 30),
                result
        );
    }

    @Test
    void parse_validMorningAppointment_returnsExpectedDateTime() {
        LocalDateTime result = DateTimeParser.parse("27/8/2026 0930");

        assertEquals(
                LocalDateTime.of(2026, 8, 27, 9, 30),
                result
        );
    }

    @Test
    void parse_invalidFormatting_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("15 September 2026, 05:30 PM"));
    }

    @Test
    void parse_invalidHour_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("15/9/2026 2560"));
    }

    @Test
    void parse_impossibleDate_throwsDateTimeParseExceptionWithFormatReminder() {
        DateTimeParseException exception = assertThrows(
                DateTimeParseException.class, () -> DateTimeParser.parse("30/2/2026 1800")
        );

        assertTrue(exception.getMessage().contains("d/M/uuuu HHmm"));
    }

    @Test
    void parseFromSave_validSavedDeadline_returnsExpectedDateTime() {
        LocalDateTime result = DateTimeParser.parseFromSave(
                "Sep 15 2026, 05:30 PM");

        assertEquals(
                LocalDateTime.of(2026, 9, 15, 17, 30),
                result
        );
    }

    @Test
    void parseFromSave_validSavedEventTime_returnsExpectedDateTime() {
        LocalDateTime result = DateTimeParser.parseFromSave(
                "Aug 27 2026, 09:30 AM");

        assertEquals(
                LocalDateTime.of(2026, 8, 27, 9, 30),
                result
        );
    }

    @Test
    void parseFromSave_invalidFormatting_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseFromSave("15/9/2026 1730"));
    }

    @Test
    void deparse_validDateTime_returnsExpectedDisplayString() {
        LocalDateTime dateTime = LocalDateTime.of(
                2026, 9, 15, 17, 30
        );

        String result = DateTimeParser.deparse(dateTime);

        assertEquals("Sep 15 2026, 05:30 PM", result);
    }

    @Test
    void deparse_midnightDateTime_formatsAsTwelveAM() {
        LocalDateTime dateTime = LocalDateTime.of(
                2026, 1, 8, 0, 0
        );

        String result = DateTimeParser.deparse(dateTime);

        assertEquals("Jan 08 2026, 12:00 AM", result);
    }

    @Test
    void parseAndDeparse_validDateTime_preservesDateAndTime() {
        String input = "27/8/2026 0930";

        LocalDateTime parsedDateTime = DateTimeParser.parse(input);
        String result = DateTimeParser.deparse(parsedDateTime);

        assertEquals("Aug 27 2026, 09:30 AM", result);
    }
}
