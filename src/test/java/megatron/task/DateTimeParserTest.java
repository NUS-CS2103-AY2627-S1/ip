package megatron.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests strict date and event validation. */
class DateTimeParserTest {
    @Test
    void parse_nonexistentDate_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.parse("2026-02-30"));
    }

    @Test
    void createEvent_nonIncreasingTime_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2026-09-17 1400", "2026-09-17 1400"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2026-09-18", "2026-09-17"));
    }
}
