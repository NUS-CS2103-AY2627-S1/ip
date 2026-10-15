package windy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests valid and invalid calendar dates accepted by Windy. */
public class TaskDateParserTest {

    @Test
    public void parse_leapDayAndSingleDigitFields_returnsDate() {
        assertEquals(LocalDate.of(2024, 2, 29), TaskDateParser.parse("2024-2-29"));
        assertEquals(LocalDate.of(2026, 9, 3), TaskDateParser.parse("2026-09-03"));
    }

    @Test
    public void parse_nonLeapDayAndOutOfRangeFields_throwsDateTimeParseException() {
        for (String date : new String[] {"2025-2-29", "2026-0-1", "2026-13-1", "2026-4-31"}) {
            assertThrows(DateTimeParseException.class, () -> TaskDateParser.parse(date), date);
        }
    }
}
