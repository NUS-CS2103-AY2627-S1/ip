package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests the supported date and date-time input formats. */
public class DateTimeParserTest {
    @Test
    public void parseDate_isoDate_returnsLocalDate() {
        assertEquals(LocalDate.of(2019, 12, 8),
                DateTimeParser.parseDate("2019-12-08"));
    }

    @Test
    public void parseDateTime_dayMonthYearAndTime_returnsLocalDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseDateTime("2/12/2019 1800"));
    }

    @Test
    public void parseDate_impossibleDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseDate("2019-02-30"));
    }

    @Test
    public void formatDateAndDateTime_validValues_returnsDisplayText() {
        assertEquals("Dec 08 2019",
                DateTimeParser.formatDate(LocalDate.of(2019, 12, 8)));
        assertEquals("Dec 08 2019 18:00",
                DateTimeParser.formatDateTime(LocalDateTime.of(2019, 12, 8, 18, 0)));
    }
}
