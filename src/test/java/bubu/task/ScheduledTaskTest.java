package bubu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline and event task behavior. */
class ScheduledTaskTest {
    private static final LocalDateTime START_TIME = LocalDateTime.of(2026, 9, 18, 10, 0);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2026, 9, 18, 11, 0);

    @Test
    void deadline_getDeadlineAndReminderTime_returnDueTime() {
        Deadline deadline = new Deadline("submit report", END_TIME);

        assertEquals(END_TIME, deadline.getDeadline());
        assertEquals(END_TIME, deadline.getReminderTime().orElseThrow());
    }

    @Test
    void deadline_toString_returnsFormattedDescription() {
        Deadline deadline = new Deadline("submit report", END_TIME);

        assertEquals("[D][ ] submit report (by: Sep 18 2026, 11:00AM)", deadline.toString());
    }

    @Test
    void event_getTimesAndReminderTime_returnScheduledValues() {
        Event event = new Event("team meeting", START_TIME, END_TIME);

        assertEquals(START_TIME, event.getStart());
        assertEquals(END_TIME, event.getEnd());
        assertEquals(START_TIME, event.getReminderTime().orElseThrow());
    }

    @Test
    void event_toString_returnsFormattedDescription() {
        Event event = new Event("team meeting", START_TIME, END_TIME);

        assertEquals("[E][ ] team meeting (from: Sep 18 2026, 10:00AM to: "
                + "Sep 18 2026, 11:00AM)", event.toString());
    }
}
