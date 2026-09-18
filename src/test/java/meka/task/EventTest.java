package meka.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the persisted and displayed representations of {@link Event} tasks.
 */
public class EventTest {

    @Test
    public void toDataString_newEvent_returnsIncompleteEventData() {
        Event event = new Event("team retreat",
                LocalDateTime.of(2026, 9, 3, 9, 5),
                LocalDateTime.of(2026, 9, 4, 17, 30));

        assertEquals("E | 0 | team retreat | 2026-09-03T09:05 | 2026-09-04T17:30",
                event.toDataString());
    }

    @Test
    public void toDataString_markedEvent_returnsCompletedEventData() {
        Event event = new Event("team retreat",
                LocalDateTime.of(2026, 9, 3, 9, 5),
                LocalDateTime.of(2026, 9, 4, 17, 30));
        event.markAsDone();

        assertEquals("E | 1 | team retreat | 2026-09-03T09:05 | 2026-09-04T17:30",
                event.toDataString());
    }

    @Test
    public void toString_eventAcrossMidnightAndNoon_returnsTwelveHourDisplayTimes() {
        Event event = new Event("team retreat",
                LocalDateTime.of(2026, 9, 3, 0, 0),
                LocalDateTime.of(2026, 9, 4, 12, 0));

        assertEquals("[E][ ] team retreat (from: Sep 03 2026, 12:00 AM "
                        + "to: Sep 04 2026, 12:00 PM)",
                event.toString());
    }

    @Test
    public void toString_markedEvent_returnsDoneIconAndFormattedRange() {
        Event event = new Event("team retreat",
                LocalDateTime.of(2026, 9, 3, 9, 5),
                LocalDateTime.of(2026, 9, 4, 17, 30));
        event.markAsDone();

        assertEquals("[E][X] team retreat (from: Sep 03 2026, 9:05 AM "
                        + "to: Sep 04 2026, 5:30 PM)",
                event.toString());
    }
}
