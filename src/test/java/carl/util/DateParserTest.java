package carl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DateParserTest {
    @Test
    public void parseDate_validLeapDay_returnsDate() {
        assertEquals(LocalDate.of(2028, 2, 29), DateParser.parseDate("2028-02-29"));
    }

    @Test
    public void parseDate_invalidValues_throwDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("2026-02-29"));
        assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("2026-13-01"));
        assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("26-01-01"));
        assertThrows(DateTimeParseException.class, () -> DateParser.parseDate(null));
    }

    @Test
    public void parseDateTime_validBoundaryTimes_returnDateTimes() {
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0),
                DateParser.parseDateTime("2026-01-01 0000"));
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59),
                DateParser.parseDateTime("2026-12-31 2359"));
    }

    @Test
    public void parseDateTime_invalidFormatDateOrTime_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () ->
                DateParser.parseDateTime("2026-01-01 2400"));
        assertThrows(DateTimeParseException.class, () ->
                DateParser.parseDateTime("2026-04-31 1200"));
        assertThrows(DateTimeParseException.class, () ->
                DateParser.parseDateTime("2026-01-01 12:00"));
        assertThrows(DateTimeParseException.class, () -> DateParser.parseDateTime(null));
    }

    @Test
    public void formattersAndErrorMessages_returnDocumentedFormats() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 10, 1, 9, 5);

        assertEquals("Oct 01 2026, 9:05 AM", dateTime.format(DateParser.getDisplayFormatter()));
        assertEquals("2026-10-01 0905", dateTime.format(DateParser.getStorageFormatter()));
        assertTrueContains(DateParser.getDateErrorMessage(), "yyyy-MM-dd");
        assertTrueContains(DateParser.getDateTimeErrorMessage(), "yyyy-MM-dd HHmm");
    }

    private void assertTrueContains(String text, String expected) {
        org.junit.jupiter.api.Assertions.assertTrue(text.contains(expected));
    }
}
