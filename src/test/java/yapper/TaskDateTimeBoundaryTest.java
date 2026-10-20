package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Covers calendar and clock boundaries beyond ordinary leap-year examples.
 */
public class TaskDateTimeBoundaryTest {
    @Test
    public void parse_centuryLeapYears_appliesGregorianRules() {
        assertEquals(LocalDate.of(2000, 2, 29).atStartOfDay(), TaskDateTime.parse("2000-02-29"));
        for (String value : new String[] {"1900-02-29", "29/2/2100"}) {
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse(value), value);
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parseDate(value), value);
        }
    }

    @Test
    public void parse_clockEndpoints_acceptsFirstAndLastMinute() {
        assertEquals(LocalDateTime.of(2024, 12, 31, 0, 0), TaskDateTime.parse("2024-12-31 0000"));
        assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59), TaskDateTime.parse("31/12/2024 2359"));
    }

    @Test
    public void parse_invalidIsoTimeOrOffset_rejectsUnsupportedValues() {
        for (String value : new String[] {"2024-12-31T24:00:00", "2024-12-31T23:59:60",
            "2024-12-31T23:59:00Z", "2024-12-31T23:59:00+08:00"}) {
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse(value), value);
        }
    }

    @Test
    public void formatForStorage_nanosecondBeforeNewYear_preservesFullPrecision() {
        LocalDateTime value = LocalDateTime.of(2024, 12, 31, 23, 59, 59, 999999999);
        assertEquals("2024-12-31T23:59:59.999999999", TaskDateTime.formatForStorage(value));
        assertEquals(value, TaskDateTime.parse(TaskDateTime.formatForStorage(value)));
    }
}
