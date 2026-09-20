package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests parsing event boundaries for schedule calculations. */
class EventTest {
    @Test
    void event_datedBoundaries_returnsParsedDateTimes() {
        Event event = new Event("lecture", "2026-09-12 0900", "2026-09-12 1100");

        assertTrue(event.hasScheduledTimes());
        assertEquals(LocalDateTime.of(2026, 9, 12, 9, 0), event.getFromDateTime().orElseThrow());
        assertEquals(LocalDateTime.of(2026, 9, 12, 11, 0), event.getToDateTime().orElseThrow());
    }

    @Test
    void event_legacyFreeTextBoundaries_isNotScheduledButKeepsDisplayText() {
        Event event = new Event("meeting", "Mon 2pm", "4pm");

        assertFalse(event.hasScheduledTimes());
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void event_endBeforeStart_isNotScheduled() {
        Event event = new Event("meeting", "2026-09-12 1200", "2026-09-12 1100");

        assertFalse(event.hasScheduledTimes());
    }

    @Test
    void event_invalidBoundary_returnsEmptyWithoutAdjustingDates() {
        for (String boundary : List.of("2026-02-29 0900", "2026-04-31 0900", "2026-09-12 2400",
                "2026-09-12 1260", "2026-9-12 0900", "", "tomorrow")) {
            Event invalidStart = new Event("meeting", boundary, "2026-09-12 1000");
            Event invalidEnd = new Event("meeting", "2026-09-12 0900", boundary);

            assertTrue(invalidStart.getFromDateTime().isEmpty(), boundary);
            assertTrue(invalidEnd.getToDateTime().isEmpty(), boundary);
            assertFalse(invalidStart.hasScheduledTimes(), boundary);
            assertFalse(invalidEnd.hasScheduledTimes(), boundary);
        }
    }

    @Test
    void event_equalBoundaries_isNotScheduled() {
        Event event = new Event("meeting", "2026-09-12 0900", "2026-09-12 0900");

        assertTrue(event.getFromDateTime().isPresent());
        assertTrue(event.getToDateTime().isPresent());
        assertFalse(event.hasScheduledTimes());
    }

    @Test
    void event_leapDayAcrossMidnight_parsesTrimmedBoundariesAndPreservesOriginalText() {
        Event event = new Event("meeting", " 2028-02-29 2330 ", " 2028-03-01 0015 ");

        assertTrue(event.hasScheduledTimes());
        assertEquals(LocalDateTime.of(2028, 2, 29, 23, 30), event.getFromDateTime().orElseThrow());
        assertEquals(LocalDateTime.of(2028, 3, 1, 0, 15), event.getToDateTime().orElseThrow());
        assertEquals(" 2028-02-29 2330 ", event.getFrom());
        assertEquals(" 2028-03-01 0015 ", event.getTo());
    }

    @Test
    void event_completedTask_displaysStatusAndBothBoundaries() {
        Event event = new Event("meeting", "2026-09-12 0900", "2026-09-12 1000");
        event.markAsDone();

        assertEquals("[E][X] meeting (from: 2026-09-12 0900 to: 2026-09-12 1000)", event.toString());
    }
}
