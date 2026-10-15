package happybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DatedTaskTest {
    @Test
    public void formatDate_date_monthNameAndPaddedDayShown() {
        assertEquals("Dec 02 2019", DatedTask.formatDate(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void formatDate_twoDigitDay_dayNotPadded() {
        assertEquals("Oct 15 2019", DatedTask.formatDate(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void formatDate_firstAndLastMonths_englishMonthNamesUsed() {
        // The formatter names Locale.ENGLISH, so the month reads the same on every machine.
        assertEquals("Jan 01 2020", DatedTask.formatDate(LocalDate.of(2020, 1, 1)));
        assertEquals("Dec 31 2020", DatedTask.formatDate(LocalDate.of(2020, 12, 31)));
    }

    @Test
    public void formatDate_dateTime_dateAndTwelveHourTimeShown() {
        assertEquals("Dec 02 2019 6:05PM", DatedTask.formatDate(LocalDateTime.of(2019, 12, 2, 18, 5)));
    }
}
