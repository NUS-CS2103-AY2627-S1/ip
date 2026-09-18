package epi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Protects real event times and a strictly positive event duration. */
class EventTest {
    @Test
    void constructor_equalOrEarlierEnd_rejectsEvent() {
        for (String end : List.of("2020-03-01 1200", "2020-03-01 1159", "2020-02-29 1200")) {
            assertEquals("Meow! An event must end after it starts.",
                    assertThrows(IllegalArgumentException.class, () ->
                            new Event("meeting", "2020-03-01 1200", end)).getMessage());
        }
    }

    @Test
    void constructor_invalidStartOrEnd_rejectsDate() {
        assertThrows(DateTimeParseException.class, () ->
                new Event("meeting", "2020-02-30 1200", "2020-03-01 1300"));
        assertThrows(DateTimeParseException.class, () ->
                new Event("meeting", "2020-02-29 1200", "2020-02-30 1300"));
        assertThrows(DateTimeParseException.class, () ->
                new Event("meeting", "2020-02-29 1200", "2020-02-29 2400"));
        assertThrows(DateTimeParseException.class, () ->
                new Event("meeting", "0000-01-01 1200", "2020-02-29 1200"));
    }

    @Test
    void constructor_overnightAndOneMinuteEvents_acceptsPositiveDurations() {
        assertEquals("E | 0 | meeting | 2020-02-29 2359 | 2020-03-01 0000",
                new Event("meeting", "2020-02-29 2359", "2020-03-01 0000").toFileFormat());
        assertEquals("E | 0 | meeting | 2020-03-01 1200 | 2020-03-01 1201",
                new Event("meeting", "2020-03-01 1200", "2020-03-01 1201").toFileFormat());
    }
}
