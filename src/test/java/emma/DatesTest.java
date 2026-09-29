package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Checks that dates are shown with the month in words, in the same wording wherever
 * Emma happens to be run.
 */
public class DatesTest {

    private final Locale originalLocale = Locale.getDefault();

    @AfterEach
    public void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    @Test
    public void format_date_showsTheMonthInWords() {
        assertEquals("Oct 15 2019", Dates.format(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void format_dayBelowTen_isPaddedToTwoDigits() {
        assertEquals("Jan 05 2019", Dates.format(LocalDate.of(2019, 1, 5)));
    }

    @Test
    public void format_firstAndLastMonths_areNamedNotNumbered() {
        assertEquals("Jan 01 2020", Dates.format(LocalDate.of(2020, 1, 1)));
        assertEquals("Dec 31 2020", Dates.format(LocalDate.of(2020, 12, 31)));
    }

    @Test
    public void format_leapDay_isShownLikeAnyOtherDay() {
        assertEquals("Feb 29 2020", Dates.format(LocalDate.of(2020, 2, 29)));
    }

    @Test
    public void format_underANonEnglishDefaultLocale_stillReadsInEnglish() {
        Locale.setDefault(Locale.FRANCE);
        assertEquals("Oct 15 2019", Dates.format(LocalDate.of(2019, 10, 15)));
    }
}
