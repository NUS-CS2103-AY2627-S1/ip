package herta.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline reconstruction, date queries, scheduling, and formatting.
 */
class DeadlineTest {

    @Test
    void fromStorage_validDateTime_reconstructsDeadline() {
        Deadline deadline = Deadline.fromStorage("submit report", "2019-12-02T18:00:00");

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    void fromStorage_invalidDateTime_throwsHelpfulException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Deadline.fromStorage("submit report", "2019-02-30"));

        assertEquals("Invalid saved deadline date/time: 2019-02-30.", exception.getMessage());
    }

    @Test
    void deadlineQueries_matchDueDateAndTimeBoundaries() {
        LocalDateTime due = LocalDateTime.of(2019, 10, 15, 18, 0);
        Deadline deadline = new Deadline("submit report", due);

        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
        assertEquals(due, deadline.getScheduledDateTime().orElseThrow());
        assertTrue(deadline.isUpcoming(due, due.plusDays(1)));
        assertFalse(deadline.isUpcoming(due.plusDays(1), due.plusDays(2)));
    }

    @Test
    void deadlineFormatting_includesStatusDescriptionAndDate() {
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("D | 0 | submit report | 2019-12-02T18:00:00",
                deadline.toStorageString());
        assertEquals("[D][ ] submit report (by: Dec 02 2019, 6:00 PM)",
                deadline.toString());
    }

    @Test
    void deadlineConstructor_nullDateTime_rejectsInput() {
        assertThrows(NullPointerException.class, () -> new Deadline("deadline", null));
    }

    @Test
    void fromStorage_nullDateTime_rejectsInput() {
        assertThrows(NullPointerException.class, () -> Deadline.fromStorage("deadline", null));
    }

    @Test
    void fromStorage_dateOnlyValue_defaultsToMidnight() {
        Deadline deadline = Deadline.fromStorage("deadline", "2019-12-02");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
        assertEquals("D | 0 | deadline | 2019-12-02T00:00:00", deadline.toStorageString());
    }

    @Test
    void fromStorage_outOfRangeAndNullValues_rejectThem() {
        assertThrows(IllegalArgumentException.class, () ->
                Deadline.fromStorage("deadline", "0000-01-01"));
        assertThrows(IllegalArgumentException.class, () ->
                Deadline.fromStorage("deadline", "10000-01-01"));
        assertThrows(NullPointerException.class, () ->
                new Deadline("deadline", LocalDateTime.of(2019, 12, 2, 0, 0)).occursOn(null));
    }

    @Test
    void deadlineSerialization_afterCompletion_writesCompletedStatus() {
        Deadline deadline = new Deadline("deadline", LocalDateTime.of(2019, 12, 2, 0, 0));

        deadline.markAsDone();

        assertEquals("D | 1 | deadline | 2019-12-02T00:00:00", deadline.toStorageString());
        assertEquals("[D][X] deadline (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    void deadline_supportedBoundaryYears_preserveStorageSerialization() {
        Deadline minimumDeadline = new Deadline("minimum",
                LocalDateTime.of(1, 1, 1, 0, 0));
        Deadline maximumDeadline = new Deadline("maximum",
                LocalDateTime.of(9999, 12, 31, 23, 59));

        assertEquals("D | 0 | minimum | 0001-01-01T00:00:00",
                minimumDeadline.toStorageString());
        assertEquals("D | 0 | maximum | 9999-12-31T23:59:00",
                maximumDeadline.toStorageString());
    }
}
