package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.Period;

import org.junit.jupiter.api.Test;


/**
 * Tests the deadline task subclass.
 */
class DeadlineTaskTest {

    /** Verifies that a deadline retains its description and due time. */
    @Test
    void construct_deadlineTask_retainsDescriptionAndDeadline() {
        LocalDateTime deadline = LocalDateTime.of(2019, 10, 15, 14, 15);
        DeadlineTask task = new DeadlineTask("return book", deadline);

        assertEquals("return book", task.getDescription());
        assertEquals(deadline, task.getEndTime());
        assertEquals(TaskTimes.makeDeadlineTimes(deadline), task.getTaskTimes());
    }

    /** Verifies that a deadline formats its type, status, and ending time. */
    @Test
    void deadlineTask_incompleteAndDoneStates_returnsFormattedTaskText() {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 14, 0));

        assertEquals("[⏳][❗] return book (by: Wed Aug 26 2026, 2.00pm)",
                task.toString());
        task.markAsDone();
        assertEquals("[⏳][✅] return book (by: Wed Aug 26 2026, 2.00pm)",
                task.toString());
    }

    /** Verifies that snoozing extends the deadline by the requested amount. */
    @Test
    void snoozeBy_duration_updatesDeadline() {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 14, 0));

        task.snoozeBy(new DurationPeriod(
                java.time.Period.ZERO, java.time.Duration.ofHours(2)));

        assertEquals(LocalDateTime.of(2026, 8, 26, 16, 0), task.getByTime());
    }

    /** Verifies a null snooze amount uses the exact validation message. */
    @Test
    void snoozeBy_nullAmount_throwsExactException() {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 14, 0));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> task.snoozeBy(null));

        assertEquals("Snooze amount cannot be null.", exception.getMessage());
    }

    /** Verifies that rescheduling replaces the deadline. */
    @Test
    void reschedule_newDeadlineTimes_replacesDeadline() {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 14, 0));
        LocalDateTime newDeadline = LocalDateTime.of(2026, 8, 27, 10, 0);

        task.reschedule(TaskTimes.makeDeadlineTimes(newDeadline));

        assertEquals(newDeadline, task.getByTime());
    }

    /** Verifies an invalid deadline replacement uses the exact message. */
    @Test
    void reschedule_nullTimes_throwsExactException() {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 14, 0));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> task.reschedule(null));

        assertEquals("Invalid deadline times.", exception.getMessage());
    }

    /** Verifies that a deadline rejects timing information with a start time. */
    @Test
    void construct_eventTimes_throwsIllegalArgumentException() {
        TaskTimes eventTimes = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 13, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new DeadlineTask("return book", eventTimes));

        assertEquals("Invalid deadline times.", exception.getMessage());
    }

    /** Verifies that a deadline accepts scheduling commands. */
    @Test
    void verifyCanBeScheduled_deadlineTask_completesNormally() {
        DeadlineTask task = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 14, 0));

        assertDoesNotThrow(task::verifyCanBeScheduled);
    }

    /** Verifies that deadline snooze timing is calculated without mutation. */
    @Test
    void createSnoozedTimes_hourDuration_returnsExtendedDeadlineTimes() {
        LocalDateTime deadline = LocalDateTime.of(2026, 8, 26, 14, 0);
        DeadlineTask task = new DeadlineTask("return book", deadline);

        TaskTimes snoozedTimes = task.createSnoozedTimes(new DurationPeriod(
                Period.ZERO, java.time.Duration.ofHours(2)));

        assertEquals(TaskTimes.makeDeadlineTimes(deadline.plusHours(2)), snoozedTimes);
        assertEquals(deadline, task.getEndTime());
    }

    /** Verifies that past deadline replacements use the domain failure reason. */
    @Test
    void createRescheduledTimes_pastDeadline_throwsPastDeadlineReason() {
        LocalDateTime currentTime = LocalDateTime.of(2026, 8, 26, 14, 0);
        DeadlineTask task = new DeadlineTask("return book", currentTime.plusHours(1));

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> task.createRescheduledTimes(
                        null, currentTime.minusMinutes(1), currentTime));

        assertEquals(TaskSchedulingException.Reason.PAST_DEADLINE, exception.getReason());
    }

}
