package magnus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the non-null endpoint invariant of {@link DateRange}.
 */
public class DateRangeTest {
    @Test
    public void constructor_validEndpoints_exposesBothDates() {
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 30);

        DateRange range = new DateRange(startDate, endDate);

        assertEquals(startDate, range.startDate());
        assertEquals(endDate, range.endDate());
    }

    @Test
    public void constructor_nullEndpoint_throwsNullPointerException() {
        LocalDate date = LocalDate.of(2026, 9, 1);

        assertThrows(NullPointerException.class, () -> new DateRange(null, date));
        assertThrows(NullPointerException.class, () -> new DateRange(date, null));
    }
}
