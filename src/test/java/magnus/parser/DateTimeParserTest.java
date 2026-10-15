package magnus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests strict date and date-time parsing.
 */
public class DateTimeParserTest {
    private final DateTimeParser parser = new DateTimeParser();

    @Test
    public void parseDate_validDate_returnsLocalDate() {
        LocalDate date = this.parser.parseDate("02/09/2026");

        assertEquals(LocalDate.of(2026, 9, 2), date);
    }

    @Test
    public void parseDate_invalidCalendarDate_throwsDateTimeParseException() {
        assertThrows(
                DateTimeParseException.class, () -> this.parser.parseDate("31/09/2026"));
    }

    @Test
    public void parseDateRange_twoDates_returnsNamedDateRange() {
        DateRange dateRange = this.parser.parseDateRange("01/09/2026 30/09/2026");

        assertEquals(LocalDate.of(2026, 9, 1), dateRange.startDate());
        assertEquals(LocalDate.of(2026, 9, 30), dateRange.endDate());
    }

    @Test
    public void parseDateRange_wrongDateCount_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseDateRange("01/09/2026"));
        assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseDateRange(
                        "01/09/2026 02/09/2026 03/09/2026"));
    }

    @Test
    public void parseDateRange_surroundingAndRepeatedWhitespace_parsesBothDates() {
        DateRange dateRange = this.parser.parseDateRange("  01/09/2026   30/09/2026  ");

        assertEquals(new DateRange(
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)), dateRange);
    }

    @Test
    public void parseDateRange_invalidSecondDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () ->
                this.parser.parseDateRange("01/09/2026 31/09/2026"));
    }

    @Test
    public void parseDateTime_validDateTime_returnsLocalDateTime() {
        LocalDateTime dateTime = this.parser.parseDateTime("02/09/2026 1500", "deadline");

        assertEquals(LocalDateTime.of(2026, 9, 2, 15, 0), dateTime);
    }

    @Test
    public void parseDateTime_invalidDateTime_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseDateTime(
                        "02/09/2026 2500", "deadline"));
    }

    @Test
    public void parseDateTime_nullDateTime_usesFieldNameInException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> this.parser.parseDateTime(null, "start time"));

        assertEquals("start time cannot be null", exception.getMessage());
    }
}
