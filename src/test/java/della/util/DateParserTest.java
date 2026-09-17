package della.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DateParserTest {
    @Test
    public void parseDateTime_validDateAndPattern_dateTimeReturned() {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 26, 14, 30);

        LocalDateTime actual = DateParser.parseDateTime("26/08/2026 14:30", "dd/MM/yyyy HH:mm");

        assertEquals(expected, actual);
    }

    @Test
    public void parseDateTime_invalidTime_exceptionThrown() {
        assertThrows(
                DateTimeParseException.class, () -> DateParser.parseDateTime("26/08/2026 14:60", "dd/MM/yyyy HH:mm"));
    }

    @Test
    public void parseDateTime_inputDoesNotMatchPattern_exceptionThrown() {
        assertThrows(
                DateTimeParseException.class, () -> DateParser.parseDateTime("2026-08-26 14:30", "dd/MM/yyyy HH:mm"));
    }

    @Test
    public void printDateTime_validDateTime_formattedStringReturned() {
        assertEquals("Aug 26 2026 2:30pm",
                DateParser.printDateTime(LocalDateTime.of(2026, 8, 26, 14, 30)));
    }
}
