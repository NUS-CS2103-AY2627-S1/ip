package nova.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the detection of clashing events.
 */
public class EventTest {

    @Test
    public void findClashingEvent_overlappingAtEnd_returnsExistingEvent() {
        TaskList taskList = new TaskList();

        Event existingEvent = new Event(
                "Lecture",
                LocalDateTime.of(2026, 9, 10, 10, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0));

        Event newEvent = new Event(
                "Meeting",
                LocalDateTime.of(2026, 9, 10, 11, 0),
                LocalDateTime.of(2026, 9, 10, 13, 0));

        taskList.add(existingEvent);

        assertEquals(existingEvent, taskList.findClashingEvent(newEvent));
    }

    @Test
    public void findClashingEvent_overlappingAtStart_returnsExistingEvent() {
        TaskList taskList = new TaskList();

        Event existingEvent = new Event(
                "Lecture",
                LocalDateTime.of(2026, 9, 10, 10, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0));

        Event newEvent = new Event(
                "Breakfast",
                LocalDateTime.of(2026, 9, 10, 9, 0),
                LocalDateTime.of(2026, 9, 10, 11, 0));

        taskList.add(existingEvent);

        assertEquals(existingEvent, taskList.findClashingEvent(newEvent));
    }

    @Test
    public void findClashingEvent_containedWithinExistingEvent_returnsExistingEvent() {
        TaskList taskList = new TaskList();

        Event existingEvent = new Event(
                "Workshop",
                LocalDateTime.of(2026, 9, 10, 10, 0),
                LocalDateTime.of(2026, 9, 10, 14, 0));

        Event newEvent = new Event(
                "Discussion",
                LocalDateTime.of(2026, 9, 10, 11, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0));

        taskList.add(existingEvent);

        assertEquals(existingEvent, taskList.findClashingEvent(newEvent));
    }

    @Test
    public void findClashingEvent_touchingBoundary_returnsNull() {
        TaskList taskList = new TaskList();

        Event existingEvent = new Event(
                "Lecture",
                LocalDateTime.of(2026, 9, 10, 10, 0),
                LocalDateTime.of(2026, 9, 10, 11, 0));

        Event newEvent = new Event(
                "Meeting",
                LocalDateTime.of(2026, 9, 10, 11, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0));

        taskList.add(existingEvent);

        assertNull(taskList.findClashingEvent(newEvent));
    }

    @Test
    public void findClashingEvent_separateEvents_returnsNull() {
        TaskList taskList = new TaskList();

        Event existingEvent = new Event(
                "Lecture",
                LocalDateTime.of(2026, 9, 10, 10, 0),
                LocalDateTime.of(2026, 9, 10, 11, 0));

        Event newEvent = new Event(
                "Meeting",
                LocalDateTime.of(2026, 9, 10, 12, 0),
                LocalDateTime.of(2026, 9, 10, 13, 0));

        taskList.add(existingEvent);

        assertNull(taskList.findClashingEvent(newEvent));
    }

    @Test
    public void toString_validEvent_returnsFormattedEvent() {
        Event event = new Event(
                "lecture",
                LocalDateTime.of(2026, 9, 10, 10, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0));

        assertEquals(
                "[E][ ] lecture "
                        + "(from: Sep 10 2026, 10:00 AM "
                        + "to: Sep 10 2026, 12:00 PM)",
                event.toString());
    }
}

