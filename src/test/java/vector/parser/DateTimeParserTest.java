package vector.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import vector.VectorException;

public class DateTimeParserTest {

    @Test
    public void parse_validFormatWithTime_success() throws VectorException {
        LocalDateTime dt = DateTimeParser.parse("2/12/2023 1800");
        assertEquals(2023, dt.getYear());
        assertEquals(12, dt.getMonthValue());
        assertEquals(2, dt.getDayOfMonth());
        assertEquals(18, dt.getHour());
        assertEquals(0, dt.getMinute());
    }

    @Test
    public void parse_validFormatWithoutTime_defaultsToStartOfDay() throws VectorException {
        LocalDateTime dt = DateTimeParser.parse("2023-12-02");
        assertEquals(2023, dt.getYear());
        assertEquals(12, dt.getMonthValue());
        assertEquals(2, dt.getDayOfMonth());
        assertEquals(0, dt.getHour());
        assertEquals(0, dt.getMinute());
    }

    @Test
    public void parse_invalidFormat_throwsVectorException() {
        assertThrows(VectorException.class, () -> {
            DateTimeParser.parse("2nd Dec 2023");
        });
    }

    @Test
    public void parseDate_validFormat_success() throws VectorException {
        LocalDate d = DateTimeParser.parseDate("2/12/2023");
        assertEquals(2023, d.getYear());
        assertEquals(12, d.getMonthValue());
        assertEquals(2, d.getDayOfMonth());
    }

    @Test
    public void parseDate_invalidFormat_throwsVectorException() {
        assertThrows(VectorException.class, () -> {
            DateTimeParser.parseDate("Dec 2nd 2023");
        });
    }

    @Test
    public void format_validDateTime_success() {
        LocalDateTime dt = LocalDateTime.of(2023, 12, 2, 18, 30);
        String formatted = DateTimeParser.format(dt);
        assertEquals("Dec 2 2023, 6:30 PM", formatted); // Handle OS-specific AM/PM case
    }
}
