package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void storageString_newEvent_expectedCanonicalDateTimeFormat() {
        Event event = new Event("study", LocalDateTime.of(2026, 1, 1, 9, 0),
                LocalDateTime.of(2026, 1, 1, 10, 30));

        assertEquals("E | 0 | study | 2026-01-01 0900 | 2026-01-01 1030",
                event.storageString());
    }

    @Test
    public void toString_newEvent_expectedReadableDateTimeFormat() {
        Event event = new Event("study", LocalDateTime.of(2026, 1, 1, 9, 0),
                LocalDateTime.of(2026, 1, 1, 10, 30));

        assertEquals("[E] [ ] study (from: Jan 01 2026, 9:00 AM to: Jan 01 2026, 10:30 AM)",
                event.toString());
    }

    @Test
    public void constructorWithDateStrings_slashDateTimes_expectedParsedStorageFormat() {
        Event event = new Event("exam", "2/12/2019 0900", "3/12/2019 1100");

        assertEquals("E | 0 | exam | 2019-12-02 0900 | 2019-12-03 1100",
                event.storageString());
    }

    @Test
    public void markAsDone_eventTask_expectedDoneMarkerInBothFormats() {
        Event event = new Event("study", LocalDateTime.of(2026, 1, 1, 9, 0),
                LocalDateTime.of(2026, 1, 1, 10, 30));
        event.markAsDone();

        assertEquals("E | 1 | study | 2026-01-01 0900 | 2026-01-01 1030",
                event.storageString());
        assertEquals("[E] [X] study (from: Jan 01 2026, 9:00 AM to: Jan 01 2026, 10:30 AM)",
                event.toString());
    }

    @Test
    public void constructorWithDateStrings_invalidDate_expectedParseException() {
        assertThrows(DateTimeParseException.class, () ->
                new Event("exam", "2019-02-30 0900", "2019-03-01 1100"));
    }
}
