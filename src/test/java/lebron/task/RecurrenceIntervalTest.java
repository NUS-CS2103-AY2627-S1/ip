package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import lebron.exception.LeBronException;

public class RecurrenceIntervalTest {
    private final LocalDateTime start = LocalDateTime.of(2019, 10, 15, 18, 0);

    @Test
    public void advance_daily_addsOneDay() {
        assertEquals(LocalDateTime.of(2019, 10, 16, 18, 0), RecurrenceInterval.DAILY.advance(start));
    }

    @Test
    public void advance_weekly_addsOneWeek() {
        assertEquals(LocalDateTime.of(2019, 10, 22, 18, 0), RecurrenceInterval.WEEKLY.advance(start));
    }

    @Test
    public void advance_monthly_addsOneMonth() {
        assertEquals(LocalDateTime.of(2019, 11, 15, 18, 0), RecurrenceInterval.MONTHLY.advance(start));
    }

    @Test
    public void fromKeyword_validWords_returnsMatchingInterval() throws LeBronException {
        assertEquals(RecurrenceInterval.DAILY, RecurrenceInterval.fromKeyword("day"));
        assertEquals(RecurrenceInterval.WEEKLY, RecurrenceInterval.fromKeyword("week"));
        assertEquals(RecurrenceInterval.MONTHLY, RecurrenceInterval.fromKeyword("month"));
    }

    @Test
    public void fromKeyword_unknownWord_exceptionThrown() {
        assertThrows(LeBronException.class, () -> RecurrenceInterval.fromKeyword("fortnight"));
    }
}
