package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the immutable timing value object used by timed tasks. */
class TaskTimesTest {

    /** Verifies that no-time values contain neither a start nor an end time. */
    @Test
    void none_noTaskTimes_returnsEmptyTimingInformation() {
        TaskTimes times = TaskTimes.none();

        assertNull(times.getStartTime());
        assertNull(times.getEndTime());
        assertFalse(times.hasStartTime());
        assertFalse(times.hasEndTime());
    }

    /** Verifies that deadline values contain only their end time. */
    @Test
    void makeDeadlineTimes_endTime_returnsEndOnlyTimingInformation() {
        LocalDateTime endTime = LocalDateTime.of(2026, 8, 26, 12, 0);

        TaskTimes times = TaskTimes.makeDeadlineTimes(endTime);

        assertNull(times.getStartTime());
        assertEquals(endTime, times.getEndTime());
        assertFalse(times.hasStartTime());
        assertTrue(times.hasEndTime());
    }

    /** Verifies that a null deadline end time is rejected. */
    @Test
    void makeDeadlineTimes_nullEndTime_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.makeDeadlineTimes(null));

        assertEquals("Deadline cannot be empty.", exception.getMessage());
    }

    /** Verifies that event values retain both event times. */
    @Test
    void makeEventTimes_validTimes_returnsBothEventTimes() {
        LocalDateTime startTime = LocalDateTime.of(2026, 8, 26, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 8, 26, 13, 0);

        TaskTimes times = TaskTimes.makeEventTimes(startTime, endTime);

        assertEquals(startTime, times.getStartTime());
        assertEquals(endTime, times.getEndTime());
        assertTrue(times.hasStartTime());
        assertTrue(times.hasEndTime());
    }

    /** Verifies that an event ending early is rejected. */
    @Test
    void makeEventTimes_endBeforeStart_throwsIllegalArgumentException() {
        LocalDateTime startTime = LocalDateTime.of(2026, 8, 26, 13, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 8, 26, 12, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.makeEventTimes(startTime, endTime));

        assertEquals("Event end cannot be before its start.", exception.getMessage());
    }

    /** Verifies that an event with a missing time is rejected. */
    @Test
    void makeEventTimes_nullTime_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.makeEventTimes(
                        null, LocalDateTime.of(2026, 8, 26, 12, 0)));

        assertEquals("Event times cannot be empty.", exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.makeEventTimes(
                        LocalDateTime.of(2026, 8, 26, 12, 0), null));

        assertEquals("Event times cannot be empty.", exception.getMessage());
    }

    /** Verifies that deadline-shaped timing passes deadline verification. */
    @Test
    void verifyDeadlineTimes_deadlineShape_acceptsTimingInformation() {
        TaskTimes times = TaskTimes.makeDeadlineTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0));

        TaskTimes.verifyDeadlineTimes(times);
    }

    /** Verifies that event-shaped timing is rejected as a deadline. */
    @Test
    void verifyDeadlineTimes_eventShape_throwsIllegalArgumentException() {
        TaskTimes times = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 13, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.verifyDeadlineTimes(times));

        assertEquals("Invalid deadline times.", exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.verifyDeadlineTimes(null));

        assertEquals("Invalid deadline times.", exception.getMessage());
    }

    /** Verifies that event-shaped timing passes event verification. */
    @Test
    void verifyEventTimes_eventShape_acceptsTimingInformation() {
        TaskTimes times = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 13, 0));

        TaskTimes.verifyEventTimes(times);
    }

    /** Verifies that deadline-shaped timing is rejected as an event. */
    @Test
    void verifyEventTimes_deadlineShape_throwsIllegalArgumentException() {
        TaskTimes times = TaskTimes.makeDeadlineTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.verifyEventTimes(times));

        assertEquals("Invalid event times.", exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.verifyEventTimes(null));

        assertEquals("Invalid event times.", exception.getMessage());
    }

    /** Verifies that replacing a schedule end preserves its shape. */
    @Test
    void withEndTime_replacementEnd_preservesScheduleShape() {
        LocalDateTime startTime = LocalDateTime.of(2026, 8, 26, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 8, 26, 13, 0);
        LocalDateTime replacementEnd = LocalDateTime.of(2026, 8, 26, 14, 0);

        TaskTimes eventTimes = TaskTimes.makeEventTimes(startTime, endTime);
        TaskTimes deadlineTimes = TaskTimes.makeDeadlineTimes(endTime);

        assertEquals(
                TaskTimes.makeEventTimes(startTime, replacementEnd),
                eventTimes.withEndTime(replacementEnd));
        assertEquals(
                TaskTimes.makeDeadlineTimes(replacementEnd),
                deadlineTimes.withEndTime(replacementEnd));
        assertEquals(endTime, eventTimes.getEndTime());
        assertEquals(endTime, deadlineTimes.getEndTime());

        assertEquals(
                TaskTimes.makeEventTimes(startTime, startTime),
                eventTimes.withEndTime(startTime));
    }

    /** Verifies that replacing the end of a ToDo is rejected. */
    @Test
    void withEndTime_noEndTime_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.none().withEndTime(
                        LocalDateTime.of(2026, 8, 26, 14, 0)));

        assertEquals("Task has no ending time.", exception.getMessage());
    }

    /** Verifies that null and earlier replacement ends use exact messages. */
    @Test
    void withEndTime_invalidReplacement_throwsExactException() {
        TaskTimes deadlineTimes = TaskTimes.makeDeadlineTimes(
                LocalDateTime.of(2026, 8, 26, 14, 0));
        TaskTimes eventTimes = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                deadlineTimes.withEndTime(null));
        assertEquals("Deadline cannot be empty.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
                eventTimes.withEndTime(null));
        assertEquals("Event times cannot be empty.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
                eventTimes.withEndTime(LocalDateTime.of(2026, 8, 26, 13, 0)));
        assertEquals("Event end cannot be before its start.", exception.getMessage());
    }

    /** Verifies that ToDo timing never matches a date. */
    @Test
    void occursOn_noTaskTimes_returnsFalse() {
        assertFalse(TaskTimes.none().occursOn(LocalDate.of(2026, 8, 26)));
    }

    /** Verifies that a deadline matches only its end date. */
    @Test
    void occursOn_deadlineDate_matchesOnlyEndDate() {
        TaskTimes times = TaskTimes.makeDeadlineTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0));

        assertTrue(times.occursOn(LocalDate.of(2026, 8, 26)));
        assertFalse(times.occursOn(LocalDate.of(2026, 8, 27)));
        assertFalse(times.occursOn(LocalDate.of(2026, 8, 25)));
    }

    /** Verifies that an event matches every date in its inclusive range. */
    @Test
    void occursOn_eventDate_matchesInclusiveDateRange() {
        TaskTimes times = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 28, 13, 0));

        assertTrue(times.occursOn(LocalDate.of(2026, 8, 26)));
        assertTrue(times.occursOn(LocalDate.of(2026, 8, 27)));
        assertTrue(times.occursOn(LocalDate.of(2026, 8, 28)));
        assertFalse(times.occursOn(LocalDate.of(2026, 8, 29)));
        assertFalse(times.occursOn(LocalDate.of(2026, 8, 25)));

        assertTrue(TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 12, 0))
                .occursOn(LocalDate.of(2026, 8, 26)));
    }

    /** Verifies that date matching rejects a null query date. */
    @Test
    void occursOn_nullDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                TaskTimes.none().occursOn(null));

        assertEquals("Search date cannot be null.", exception.getMessage());
    }

    /** Verifies timing equality and hash codes include both schedule fields. */
    @Test
    void equalsAndHashCode_matchingOrDifferentTimes_returnsExpectedComparison() {
        TaskTimes first = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 13, 0));
        TaskTimes equal = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 13, 0));
        TaskTimes differentStart = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 11, 0),
                LocalDateTime.of(2026, 8, 26, 13, 0));
        TaskTimes differentEnd = TaskTimes.makeEventTimes(
                LocalDateTime.of(2026, 8, 26, 12, 0),
                LocalDateTime.of(2026, 8, 26, 14, 0));

        assertEquals(first, equal);
        assertEquals(first.hashCode(), equal.hashCode());
        assertNotEquals(first, differentStart);
        assertNotEquals(first, differentEnd);
        assertNotEquals(first, TaskTimes.none());
        assertNotEquals(first, "not timing information");
    }
}
