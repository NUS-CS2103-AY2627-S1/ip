package shannon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import shannon.exception.InvalidDateException;
import shannon.exception.InvalidEventTimesException;

/** Tests event boundaries, date-time validation, and duplicate semantics. */
class EventTest {
    @Test
    void parse_dateOnly_includesEntireDay() throws Exception {
        EventTime time = EventTime.parse(" 2026-08-09 ");
        assertEquals(LocalDateTime.of(2026, 8, 9, 0, 0), time.toStartDateTime());
        assertEquals(LocalDateTime.of(2026, 8, 10, 0, 0), time.toEndDateTime());
        assertEquals("Aug 09 2026", time.toString());
        assertEquals("2026-08-09", time.toFileFormat());
        Event event = new Event("Camp", time, time);
        assertEquals("E | 0 | Camp | 2026-08-09 | 2026-08-09", event.toFileFormat());
    }

    @Test
    void parse_explicitTime_preservesExactBoundary() throws Exception {
        EventTime time = EventTime.parse("2026-08-09   9:30");
        assertEquals(LocalDateTime.of(2026, 8, 9, 9, 30), time.toStartDateTime());
        assertEquals(time.toStartDateTime(), time.toEndDateTime());
        assertEquals("Aug 09 2026 9:30", time.toString());
        assertEquals("2026-08-09 9:30", time.toFileFormat());
    }

    @Test
    void parse_invalidDatesAndTimes_rejected() {
        for (String text : new String[] {
            "", "tomorrow", "2026-08-09 9:30 extra", "2026-02-30", "2026-00-01",
            "2026-08-09 24:00", "2026-08-09 9:60", "2026-08-09 2pm"
        }) {
            assertThrows(InvalidDateException.class, () -> EventTime.parse(text), text);
        }
    }

    @Test
    void construct_equalOrReversedTimes_rejected() throws Exception {
        EventTime start = EventTime.parse("2026-08-09 10:00");
        EventTime earlier = EventTime.parse("2026-08-09 9:00");
        assertThrows(InvalidEventTimesException.class, () -> new Event("Meeting", start, start));
        assertThrows(InvalidEventTimesException.class, () -> new Event("Meeting", start, earlier));
    }

    @Test
    void hasSameDetails_event_comparesDescriptionAndBothTimes() throws Exception {
        EventTime start = EventTime.parse("2026-08-09 9:00");
        EventTime end = EventTime.parse("2026-08-09 10:00");
        EventTime later = EventTime.parse("2026-08-09 11:00");
        Event event = new Event("Meeting", start, end);
        Event duplicate = new Event("MEETING", start, end);
        duplicate.markDone();
        assertTrue(event.hasSameDetails(duplicate));
        assertFalse(event.hasSameDetails(new Event("Meeting", start, later)));
        assertFalse(event.hasSameDetails(new Event("Meeting", end, later)));
        assertFalse(event.hasSameDetails(new Event("Lunch", start, end)));
        assertFalse(event.hasSameDetails(new Todo("Meeting")));
        assertFalse(event.hasSameDetails(null));
    }

    @Test
    void hasSameDetails_deadline_comparesDateAndTaskType() {
        LocalDate date = LocalDate.of(2026, 8, 9);
        Deadline deadline = new Deadline("Report", date);
        assertTrue(deadline.hasSameDetails(new Deadline("REPORT", date)));
        assertFalse(deadline.hasSameDetails(new Deadline("Report", date.plusDays(1))));
        assertFalse(deadline.hasSameDetails(new Todo("Report")));
    }
}
