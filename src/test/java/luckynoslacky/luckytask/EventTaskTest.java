package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.Period;

import org.junit.jupiter.api.Test;


/**
 * Tests the event task subclass.
 */
class EventTaskTest {

    /** Verifies that an event retains its description, start, and end times. */
    @Test
    void construct_eventTask_retainsDescriptionAndTimes() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 6, 16, 0);
        EventTask task = new EventTask("project meeting", start, end);

        assertEquals("project meeting", task.getDescription());
        assertEquals(TaskTimes.makeEventTimes(start, end), task.getTaskTimes());
    }

    /** Verifies that an event formats its type, status, start, and end times. */
    @Test
    void eventTask_incompleteAndDoneStates_returnsFormattedTaskText() {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0));

        assertEquals("[📆][❗] project meeting (from: Wed Aug 26 2026, 2.00pm"
                        + " to: Wed Aug 26 2026, 4.00pm)",
                task.toString());
        task.markAsDone();
        assertEquals("[📆][✅] project meeting (from: Wed Aug 26 2026, 2.00pm"
                        + " to: Wed Aug 26 2026, 4.00pm)",
                task.toString());
    }

    /** Verifies that an event ending before it starts is rejected. */
    @Test
    void eventTask_endBeforeStart_throwsIllegalArgumentException() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 6, 16, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 6, 14, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new EventTask("project meeting", start, end));

        assertEquals("Event end cannot be before its start.", exception.getMessage());
    }

    /** Verifies that snoozing changes only the event end time. */
    @Test
    void snoozeBy_duration_updatesEndWithoutChangingStart() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 6, 16, 0);
        EventTask task = new EventTask("project meeting", start, end);

        task.snoozeBy(new DurationPeriod(
                java.time.Period.ZERO, java.time.Duration.ofHours(2)));

        assertEquals(start, task.getStartTime());
        assertEquals(LocalDateTime.of(2026, 8, 6, 18, 0), task.getEndTime());
    }

    /** Verifies a null snooze amount uses the exact validation message. */
    @Test
    void snoozeBy_nullAmount_throwsExactException() {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> task.snoozeBy(null));

        assertEquals("Snooze amount cannot be null.", exception.getMessage());
    }

    /** Verifies that an end-time schedule preserves the event start time. */
    @Test
    void reschedule_newEndTimeOnly_preservesEventStart() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 6, 14, 0);
        EventTask task = new EventTask(
                "project meeting", start, LocalDateTime.of(2026, 8, 6, 16, 0));
        LocalDateTime newEnd = LocalDateTime.of(2026, 8, 6, 18, 0);

        task.reschedule(TaskTimes.makeEventTimes(start, newEnd));

        assertEquals(start, task.getStartTime());
        assertEquals(newEnd, task.getEndTime());
    }

    /** Verifies that event rescheduling updates both times together. */
    @Test
    void reschedule_validTimes_updatesStartAndEnd() {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0));
        LocalDateTime newStart = LocalDateTime.of(2026, 8, 7, 10, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 8, 7, 11, 0);

        task.reschedule(TaskTimes.makeEventTimes(newStart, newEnd));

        assertEquals(newStart, task.getStartTime());
        assertEquals(newEnd, task.getEndTime());
    }

    /** Verifies an invalid event replacement uses the exact message. */
    @Test
    void reschedule_nullTimes_throwsExactException() {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> task.reschedule(null));

        assertEquals("Invalid event times.", exception.getMessage());
    }

    /** Verifies that invalid event rescheduling leaves both times unchanged. */
    @Test
    void reschedule_endBeforeStart_preservesOriginalTimes() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 6, 16, 0);
        EventTask task = new EventTask("project meeting", start, end);

        assertThrows(IllegalArgumentException.class, () -> task.reschedule(
                TaskTimes.makeEventTimes(
                        LocalDateTime.of(2026, 8, 7, 16, 0),
                        LocalDateTime.of(2026, 8, 7, 14, 0))));

        assertEquals(start, task.getStartTime());
        assertEquals(end, task.getEndTime());
    }

    /** Verifies that an event rejects deadline-only timing information. */
    @Test
    void construct_deadlineTimes_throwsIllegalArgumentException() {
        TaskTimes deadlineTimes = TaskTimes.makeDeadlineTimes(
                LocalDateTime.of(2026, 8, 26, 13, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new EventTask("project meeting", deadlineTimes));

        assertEquals("Invalid event times.", exception.getMessage());
    }

    /** Verifies that an event accepts scheduling commands. */
    @Test
    void verifyCanBeScheduled_eventTask_completesNormally() {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0));

        assertDoesNotThrow(task::verifyCanBeScheduled);
    }

    /** Verifies that event snooze timing preserves its start time without mutation. */
    @Test
    void createSnoozedTimes_hourDuration_preservesStartAndExtendsEnd() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 26, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 26, 16, 0);
        EventTask task = new EventTask("project meeting", start, end);

        TaskTimes snoozedTimes = task.createSnoozedTimes(new DurationPeriod(
                Period.ZERO, java.time.Duration.ofHours(2)));

        assertEquals(TaskTimes.makeEventTimes(start, end.plusHours(2)), snoozedTimes);
        assertEquals(TaskTimes.makeEventTimes(start, end), task.getTaskTimes());
    }

    /** Verifies that invalid event replacements use the domain failure reason. */
    @Test
    void createRescheduledTimes_endBeforeStart_throwsEndBeforeStartReason() {
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0));

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> task.createRescheduledTimes(
                        LocalDateTime.of(2026, 8, 27, 16, 0),
                        LocalDateTime.of(2026, 8, 27, 14, 0),
                        LocalDateTime.of(2026, 8, 26, 12, 0)));

        assertEquals(TaskSchedulingException.Reason.END_BEFORE_START, exception.getReason());
    }

}
