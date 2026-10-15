package lumi.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import lumi.exception.LumiException;

/** Tests strict user date parsing and friendly date-time formatting. */
public class DateTimeParserTest {
    @Test
    public void parseUserInput_supportedDateFormats_correctDateTimesReturned()
            throws LumiException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimeParser.parseUserInput("2019-10-15"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                DateTimeParser.parseUserInput("2/12/2019"));
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                DateTimeParser.parseUserInput("2019-10-15 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 9, 5),
                DateTimeParser.parseUserInput("2/12/2019 0905"));
        assertEquals(LocalDateTime.of(2020, 2, 29, 23, 59),
                DateTimeParser.parseUserInput("2020-02-29 2359"));
    }

    @Test
    public void parseUserInput_invalidOrUnsupportedValues_exceptionThrown() {
        List<String> invalidValues = List.of(
                "2019-02-29",
                "2019-13-01",
                "2019-04-31",
                "2019/10/15",
                "15-10-2019",
                "2019-10-15 2400",
                "2019-10-15 18:00",
                "2019-10-15 900",
                " 2019-10-15",
                "2019-10-15 ",
                "tomorrow",
                ""
        );

        for (String value : invalidValues) {
            LumiException exception = assertThrows(LumiException.class, () ->
                    DateTimeParser.parseUserInput(value), "Value: " + value);
            assertEquals(
                    "Signal unclear: use a date like 2019-10-15 or 2/12/2019, "
                            + "optionally followed by a 24-hour time such as 1800.",
                    exception.getMessage(), "Value: " + value);
        }
    }

    @Test
    public void format_midnight_timeOmitted() {
        assertEquals("Jan 05 2026",
                DateTimeParser.format(LocalDateTime.of(2026, 1, 5, 0, 0)));
    }

    @Test
    public void format_nonMidnightTimes_twelveHourTimeIncluded() {
        assertEquals("Jan 05 2026, 9:07AM",
                DateTimeParser.format(LocalDateTime.of(2026, 1, 5, 9, 7)));
        assertEquals("Jan 05 2026, 12:00PM",
                DateTimeParser.format(LocalDateTime.of(2026, 1, 5, 12, 0)));
        assertEquals("Jan 05 2026, 11:59PM",
                DateTimeParser.format(LocalDateTime.of(2026, 1, 5, 23, 59)));
    }
}
