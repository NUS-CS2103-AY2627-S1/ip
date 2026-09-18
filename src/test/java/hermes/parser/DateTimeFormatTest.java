package hermes.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hermes.HermesException;

/**
 * Tests reading the dates and times a user may type.
 *
 * <p>The twelve-hour formats are left out on purpose. Whether "pm" or "PM" is
 * accepted depends on the locale of the machine running the program, so a case
 * that passes on one machine would fail on another.
 */
public class DateTimeFormatTest {

    private static final LocalDateTime AFTERNOON = LocalDateTime.of(2026, 8, 27, 15, 0);
    private static final LocalDateTime MIDNIGHT = LocalDateTime.of(2026, 8, 27, 0, 0);

    @Test
    public void parseDateTime_eachDateOnlyFormat_readAsStartOfDay() throws HermesException {
        Map<String, LocalDateTime> inputs = Map.of(
                "2026-08-27", MIDNIGHT,
                "2026 08 27", MIDNIGHT,
                "27/8/2026", MIDNIGHT,
                "27 Aug 2026", MIDNIGHT,
                "Aug 27 2026", MIDNIGHT);

        for (Map.Entry<String, LocalDateTime> input : inputs.entrySet()) {
            assertEquals(input.getValue(), DateTimeFormat.parseDateTime(input.getKey()), input.getKey());
        }
    }

    @Test
    public void parseDateTime_eachDateTimeFormat_readWithItsTime() throws HermesException {
        String[] inputs = {
            "2026-08-27 1500", "2026-08-27 15:00",
            "2026/08/27 1500", "2026/08/27 15:00",
            "27/8/2026 1500", "27/8/2026 15:00",
            "27-8-2026 1500", "27-8-2026 15:00",
            "27 Aug 2026 1500", "27 Aug 2026 15:00",
            "Aug 27 2026 1500", "Aug 27 2026 15:00",
        };

        for (String input : inputs) {
            assertEquals(AFTERNOON, DateTimeFormat.parseDateTime(input), input);
        }
    }

    @Test
    public void parseDateTime_unrecognisedText_throwsExceptionQuotingIt() {
        for (String input : new String[] {"tomorrow", "2026-13-40", "32/8/2026", ""}) {
            HermesException exception = assertThrows(
                    HermesException.class, () -> DateTimeFormat.parseDateTime(input), input);

            assertEquals("'" + input + "' is no date I can read. "
                    + "Write it thus, for instance: 17/08/2026 1500", exception.getMessage());
        }
    }

    @Test
    public void parseDateTime_dateThatDoesNotExist_refusedRatherThanAdjusted() {
        // Resolving leniently would quietly turn 30 February into 28 February,
        // storing a day the user never typed.
        for (String input : new String[] {"2026-02-30", "30/2/2026", "30 Feb 2026", "31/4/2026 1500"}) {
            HermesException exception = assertThrows(
                    HermesException.class, () -> DateTimeFormat.parseDateTime(input), input);

            assertEquals("'" + input + "' is no date I can read. "
                    + "Write it thus, for instance: 17/08/2026 1500", exception.getMessage());
        }
    }

    @Test
    public void parseDateTime_lastDayOfEachMonthLength_stillAccepted() throws HermesException {
        // Strict resolving must refuse only the days that do not exist.
        assertEquals(LocalDateTime.of(2026, 2, 28, 0, 0), DateTimeFormat.parseDateTime("2026-02-28"));
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), DateTimeFormat.parseDateTime("2024-02-29"));
        assertEquals(LocalDateTime.of(2026, 4, 30, 0, 0), DateTimeFormat.parseDateTime("2026-04-30"));
        assertEquals(LocalDateTime.of(2026, 12, 31, 0, 0), DateTimeFormat.parseDateTime("2026-12-31"));
    }

    @Test
    public void parseDateTime_formatSuggestedInError_isAccepted() throws HermesException {
        // The error message tells the user to write a date this way, so it must work.
        assertEquals(LocalDateTime.of(2026, 8, 17, 15, 0),
                DateTimeFormat.parseDateTime("17/08/2026 1500"));
    }
}
