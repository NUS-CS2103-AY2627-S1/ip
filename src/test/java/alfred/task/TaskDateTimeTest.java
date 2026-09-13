package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import alfred.exception.AlfredException;

class TaskDateTimeTest {
    @Test
    void parse_dateOnlyInput_returnsDateAtMidnight() throws AlfredException {
        LocalDateTime result = TaskDateTime.parse("2024-02-29");

        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), result);
    }

    @Test
    void parse_dateAndTimeInput_returnsSpecifiedDateAndTime() throws AlfredException {
        LocalDateTime result = TaskDateTime.parse("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    void parse_invalidDateOrFormat_exceptionWithUserGuidanceThrown() {
        AlfredException exception = assertThrows(AlfredException.class, () ->
                TaskDateTime.parse("2023-02-29"));

        assertEquals("Alfred needs a valid date in yyyy-MM-dd or d/M/yyyy HHmm format.",
                exception.getMessage());
    }

    @Test
    void parseStored_validIsoDateTime_returnsStoredDateAndTime() throws AlfredException {
        LocalDateTime result = TaskDateTime.parseStored("2024-02-29T23:59:58");

        assertEquals(LocalDateTime.of(2024, 2, 29, 23, 59, 58), result);
    }

    @Test
    void parseStored_invalidValue_exceptionWithStorageErrorThrown() {
        AlfredException exception = assertThrows(AlfredException.class, () ->
                TaskDateTime.parseStored("29/02/2024 2359"));

        assertEquals("Alfred could not load the saved tasks.", exception.getMessage());
    }

    @Test
    void format_midnightDateTime_returnsDateOnly() {
        String result = TaskDateTime.format(LocalDateTime.of(2024, 2, 29, 0, 0));

        assertEquals("Feb 29 2024", result);
    }

    @Test
    void format_nonMidnightDateTime_returnsDateAndTime() {
        String result = TaskDateTime.format(LocalDateTime.of(2024, 2, 29, 9, 5));

        assertEquals("Feb 29 2024 09:05", result);
    }
}
