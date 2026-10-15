package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Exercises every documented input format and the midnight/noon display boundaries. */
class DateTimeFormatsTest {
    /** All supported timed inputs should resolve to the same typed date and time.
     *
     * @param input the equivalent date-time representation.
     */
    @ParameterizedTest
    @ValueSource(strings = {"2024-02-29T18:05", "2024-02-29T18:05:00", "2024-02-29 1805",
        "2024-02-29 18:05", "29/2/2024 1805", "29/2/2024 18:05"})
    void parseSupportsEachDateTimeFormat(String input) throws SevenSixException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse(input);

        assertEquals(LocalDate.of(2024, 2, 29), parsed.getDate());
        assertEquals(LocalTime.of(18, 5), parsed.getTime());
        assertEquals("2024-02-29T18:05", DateTimeParser.formatForStorage(parsed.getDate(), parsed.getTime()));
    }

    /** Both date-only formats should preserve absence of an explicit time.
     *
     * @param input the date-only representation.
     */
    @ParameterizedTest
    @ValueSource(strings = {"2024-02-29", "29/2/2024"})
    void parseDateOnlyPreservesAbsentTime(String input) throws SevenSixException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse(input);

        assertEquals(LocalDate.of(2024, 2, 29), parsed.getDate());
        assertNull(parsed.getTime());
        assertEquals("2024-02-29", DateTimeParser.formatForStorage(parsed.getDate(), parsed.getTime()));
        assertEquals("Feb 29 2024", DateTimeParser.formatForDisplay(parsed.getDate(), parsed.getTime()));
    }

    /** Twelve-hour display must distinguish midnight, noon, and the last minute of a day.
     *
     * @param input the time to format.
     * @param expected the expected English twelve-hour representation.
     */
    @ParameterizedTest
    @CsvSource({"00:00,12:00 AM", "00:01,12:01 AM", "11:59,11:59 AM", "12:00,12:00 PM",
        "12:01,12:01 PM", "23:59,11:59 PM"})
    void formatForDisplayHandlesClockBoundaries(String input, String expected) {
        assertEquals("Feb 29 2024 " + expected,
                DateTimeParser.formatForDisplay(LocalDate.of(2024, 2, 29), LocalTime.parse(input)));
    }
}
