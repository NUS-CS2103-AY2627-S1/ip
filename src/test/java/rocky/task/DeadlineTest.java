package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests date-only and date-time deadline behavior. */
class DeadlineTest {
    /** Verifies that a date-only deadline preserves its date-only presentation. */
    @Test
    void constructor_dateOnlyDeadline_displaysStoresAndSortsByDate() {
        LocalDate date = LocalDate.of(2025, 3, 5);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals("[D][ ] submit report (by: Mar 05 2025)", deadline.toString());
        assertEquals("D | 0 | submit report | 2025-03-05", deadline.toStorageString());
        assertEquals(date.atStartOfDay(), deadline.getSortDate().orElseThrow());
    }

    /** Verifies that a timed deadline preserves its time in every representation. */
    @Test
    void constructor_timedDeadline_displaysStoresAndSortsByDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 3, 5, 18, 30);
        Deadline deadline = new Deadline("submit report", dateTime);

        assertEquals("[D][ ] submit report (by: Mar 05 2025, 6:30PM)", deadline.toString());
        assertEquals("D | 0 | submit report | 2025-03-05T18:30", deadline.toStorageString());
        assertEquals(dateTime, deadline.getSortDate().orElseThrow());
    }

    /** Verifies that completing a deadline updates display and storage status. */
    @Test
    void markAsDone_incompleteDeadline_usesDoneStatus() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2025, 3, 5));

        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Mar 05 2025)", deadline.toString());
        assertEquals("D | 1 | submit report | 2025-03-05", deadline.toStorageString());
    }
}
