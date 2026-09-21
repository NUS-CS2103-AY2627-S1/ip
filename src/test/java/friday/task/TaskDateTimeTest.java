package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests strict date parsing and stable display independently of the user's locale.
 */
class TaskDateTimeTest {
    @ParameterizedTest
    @CsvSource({
            "2019-12-02 18:00, 2019-12-02T18:00",
            "2/12/2019 1800, 2019-12-02T18:00",
            "02/12/2019 1800, 2019-12-02T18:00",
            "2019-12-02T18:00, 2019-12-02T18:00",
            "2019-10-15, 2019-10-15T00:00",
            "2024-02-29 23:59, 2024-02-29T23:59",
            "29/2/2024 0000, 2024-02-29T00:00",
            "2000-02-29, 2000-02-29T00:00"
    })
    void parse_supportedFormat_returnsExpectedDateTime(String input, String expected) {
        assertEquals(LocalDateTime.parse(expected), TaskDateTime.parse(input));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {
            "Sunday", "2019-02-29", "1900-02-29", "2019-04-31", "2019-13-01",
            "2019-00-01", "2019-12-00", "2019-1-2", "2019-12-02 24:00", "2019-12-02 18:60",
            "2019-02-29 18:00", "29/2/2019 1800", "31/4/2019 1800", "2/12/2019 2400",
            "2/12/2019 1860", "2/12/2019 180", "2019-12-02T18:00:30", "2019-12-02 18:00 extra"
    })
    void parse_invalidDateOrTime_throwsHelpfulException(String input) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> TaskDateTime.parse(input));
        assertEquals("Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, "
                + "or d/M/yyyy HHmm (e.g., 2/12/2019 1800).", error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "2019-10-15T00:00 | Oct 15 2019",
            "2019-12-02T18:00 | Dec 02 2019, 18:00",
            "2019-12-02T00:01 | Dec 02 2019, 00:01",
            "2024-02-29T23:59 | Feb 29 2024, 23:59"
    })
    void format_midnightAndTimedValues_formatsConsistently(String input, String expected) {
        assertEquals(expected, TaskDateTime.format(LocalDateTime.parse(input)));
    }

    /**
     * Restores the process-wide locale even if an assertion fails.
     */
    @Test
    @ResourceLock(Resources.LOCALE)
    void format_frenchDefaultLocale_stillUsesEnglishMonthNames() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRENCH);
            assertEquals("Oct 15 2019", TaskDateTime.format(LocalDateTime.of(2019, 10, 15, 0, 0)));
            assertEquals("Dec 02 2019, 18:00", TaskDateTime.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
        } finally {
            Locale.setDefault(original);
        }
    }
}
