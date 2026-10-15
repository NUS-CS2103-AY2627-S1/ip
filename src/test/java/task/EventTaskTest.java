package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for EventTask: constructors, formatting, mutators, signatures and equality.
 */
public class EventTaskTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 9, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 9, 11, 0);

    @Test
    public void constructor_withLocalDateTimes_isNotDone() {
        EventTask task = new EventTask("team meeting", START, END);
        assertFalse(task.isDone());
        assertEquals(START, task.getStartTime());
        assertEquals(END, task.getEndTime());
    }

    @Test
    public void constructor_withDefaultFormatStrings_parsesTimes() {
        EventTask task = new EventTask("team meeting", "09/09/2026 10:00", "09/09/2026 11:00");
        assertEquals(START, task.getStartTime());
        assertEquals(END, task.getEndTime());
    }

    @Test
    public void constructor_withCustomFormatStrings_parsesTimes() {
        EventTask task = new EventTask(
                "team meeting", "2026-09-09 10:00", "2026-09-09 11:00", "yyyy-MM-dd HH:mm");
        assertEquals(START, task.getStartTime());
        assertEquals(END, task.getEndTime());
    }

    @Test
    public void constructor_withDoneFlagAndLocalDateTimes_setsCompletionState() {
        EventTask task = new EventTask("team meeting", true, START, END);
        assertTrue(task.isDone());
    }

    @Test
    public void constructor_withDoneFlagAndDefaultFormatStrings_parsesTimes() {
        EventTask task = new EventTask(
                "team meeting", true, "09/09/2026 10:00", "09/09/2026 11:00");
        assertTrue(task.isDone());
        assertEquals(START, task.getStartTime());
    }

    @Test
    public void constructor_withDoneFlagAndCustomFormatStrings_parsesTimes() {
        EventTask task = new EventTask(
                "team meeting", true, "2026-09-09 10:00", "2026-09-09 11:00", "yyyy-MM-dd HH:mm");
        assertTrue(task.isDone());
        assertEquals(END, task.getEndTime());
    }

    @Test
    public void setters_updateStartAndEndTime() {
        EventTask task = new EventTask("team meeting", START, END);
        LocalDateTime newStart = START.plusDays(1);
        LocalDateTime newEnd = END.plusDays(1);

        task.setStartTime(newStart);
        task.setEndTime(newEnd);

        assertEquals(newStart, task.getStartTime());
        assertEquals(newEnd, task.getEndTime());
    }

    @Test
    public void toSignature_reflectsCompletionState() {
        assertEquals(
                "E|0|team meeting|09/09/2026 10:00|09/09/2026 11:00",
                new EventTask("team meeting", START, END).toSignature());
        assertEquals(
                "E|1|team meeting|09/09/2026 10:00|09/09/2026 11:00",
                new EventTask("team meeting", true, START, END).toSignature());
    }

    @Test
    public void toString_reflectsCompletionState() {
        assertEquals(
                "[E][ ] team meeting (from: 09/09/2026 10:00 to: 09/09/2026 11:00)",
                new EventTask("team meeting", START, END).toString());
        assertEquals(
                "[E][X] team meeting (from: 09/09/2026 10:00 to: 09/09/2026 11:00)",
                new EventTask("team meeting", true, START, END).toString());
    }

    @Test
    public void equals_sameDescriptionAndTimesIsEqual() {
        assertEquals(
                new EventTask("team meeting", START, END),
                new EventTask("team meeting", true, START, END));
    }

    @Test
    public void equals_differentStartTimeIsNotEqual() {
        assertFalse(new EventTask("team meeting", START, END)
                .equals(new EventTask("team meeting", START.plusHours(1), END)));
    }

    @Test
    public void equals_differentEndTimeIsNotEqual() {
        assertFalse(new EventTask("team meeting", START, END)
                .equals(new EventTask("team meeting", START, END.plusHours(1))));
    }

    @Test
    public void equals_differentTaskTypeIsNotEqual() {
        assertFalse(new EventTask("team meeting", START, END)
                .equals(new Task("team meeting")));
    }
}
