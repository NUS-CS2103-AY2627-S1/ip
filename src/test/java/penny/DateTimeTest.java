package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests supported date formats and invalid date input.
 */
public class DateTimeTest {
    @Test
    void parse_supportedFormats_returnsDateTime() throws PennyException {
        assertEquals(LocalDateTime.of(2026, 9, 15, 0, 0), DateTime.parse(" 15092026 "));
        assertEquals(LocalDateTime.of(2026, 9, 15, 23, 59), DateTime.parse(" 15092026 2359 "));
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), DateTime.parse("29022024 0000"));
    }

    @Test
    void format_midnightAndTime_usesDisplayFormats() {
        assertEquals("15 09 2026", DateTime.format(LocalDateTime.of(2026, 9, 15, 0, 0)));
        assertEquals("15 09 2026 0001", DateTime.format(LocalDateTime.of(2026, 9, 15, 0, 1)));
    }

    @Test
    void parse_invalidFormats_reportsExpectedFormat() {
        for (String input : new String[]{"", "abc", "2026-09-15", "00132026", "15092026 2560"}) {
            PennyException error = assertThrows(PennyException.class, () -> DateTime.parse(input), input);
            assertEquals("Use 'ddMMyyyy HHmm' or 'ddMMyyyy'.", error.getMessage());
        }
    }
}
