package serangooner.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import serangooner.SerangoonerException;

public class DateRangeTest {
    @Test
    public void constructor_endAfterStart_keepsBothDates() {
        DateRange range = new DateRange(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));
        assertEquals(LocalDate.of(2026, 9, 1), range.start());
        assertEquals(LocalDate.of(2026, 9, 5), range.end());
    }

    @Test
    public void constructor_sameStartAndEnd_isAllowedAsASingleDay() {
        LocalDate day = LocalDate.of(2026, 9, 1);
        DateRange range = new DateRange(day, day);
        assertEquals(day, range.start());
        assertEquals(day, range.end());
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                new DateRange(LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 1)));
    }
}
