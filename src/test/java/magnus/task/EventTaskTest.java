package magnus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests validation of event scheduling invariants.
 */
public class EventTaskTest {
    @Test
    public void constructor_stringTimes_parsesStartAndEnd() {
        EventTask task = new EventTask(
                "meeting", "20/09/2026 0900", "20/09/2026 1030");

        assertEquals(LocalDateTime.of(2026, 9, 20, 9, 0), task.getStart());
        assertEquals(LocalDateTime.of(2026, 9, 20, 10, 30), task.getEnd());
    }

    @Test
    public void constructor_equalStartAndEnd_throwsIllegalArgumentException() {
        LocalDateTime eventTime = LocalDateTime.of(2026, 9, 2, 15, 0);

        assertThrows(IllegalArgumentException.class, () ->
                new EventTask("meeting", eventTime, eventTime));
    }

    @Test
    public void constructor_startAfterEnd_throwsIllegalArgumentException() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 2, 16, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 2, 15, 0);

        assertThrows(IllegalArgumentException.class, () ->
                new EventTask("meeting", start, end));
    }

    @Test
    public void constructor_nullStartOrEnd_throwsNullPointerException() {
        LocalDateTime eventTime = LocalDateTime.of(2026, 9, 20, 9, 0);

        assertThrows(NullPointerException.class, () ->
                new EventTask("meeting", null, eventTime));
        assertThrows(NullPointerException.class, () ->
                new EventTask("meeting", eventTime, null));
    }

    @Test
    public void hasSameDetails_matchingScheduleIgnoresStatus() {
        EventTask firstTask = new EventTask(
                "meeting", "20/09/2026 0900", "20/09/2026 1030");
        EventTask secondTask = new EventTask(
                "meeting", "20/09/2026 0900", "20/09/2026 1030");
        secondTask.markAsDone();

        assertTrue(firstTask.hasSameDetails(secondTask));
    }

    @Test
    public void hasSameDetails_differentStartEndOrType_returnsFalse() {
        EventTask task = new EventTask(
                "meeting", "20/09/2026 0900", "20/09/2026 1030");

        assertFalse(task.hasSameDetails(new EventTask(
                "meeting", "20/09/2026 0930", "20/09/2026 1030")));
        assertFalse(task.hasSameDetails(new EventTask(
                "meeting", "20/09/2026 0900", "20/09/2026 1100")));
        assertFalse(task.hasSameDetails(new ToDoTask("meeting")));
    }

    @Test
    public void serialize_completedTaskWithQuote_escapesDescription() {
        EventTask task = new EventTask(
                "meet \"Alice\"", "20/09/2026 0900", "20/09/2026 1030");
        task.markAsDone();

        assertEquals("E,1,\"meet \"\"Alice\"\"\",20/09/2026 0900-20/09/2026 1030",
                task.serialize());
    }

    @Test
    public void toString_eventTask_usesDisplayDateFormat() {
        EventTask task = new EventTask(
                "meeting", "02/09/2026 0507", "03/09/2026 1823");

        assertEquals("[E][ ] meeting (from: Sep 02, 2026 05:07 to: Sep 03, 2026 18:23)",
                task.toString());
    }
}
