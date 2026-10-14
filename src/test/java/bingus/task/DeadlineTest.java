package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching and display formatting for {@link Deadline} tasks.
 */
class DeadlineTest {

    /**
     * Verifies that a deadline matches only its due date.
     */
    @Test
    void matchesDate_dueDateAndAdjacentDates_returnsExpectedResult() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 18, 0));

        assertTrue(deadline.matchesDate(LocalDate.of(2026, 9, 20)));
        assertFalse(deadline.matchesDate(LocalDate.of(2026, 9, 19)));
        assertFalse(deadline.matchesDate(LocalDate.of(2026, 9, 21)));
    }

    /**
     * Verifies that a deadline displays its type, completion status, and due date and time.
     */
    @Test
    void getDisplayString_deadline_returnsFormattedDetails() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 18, 0));

        assertEquals("[D][○] submit report (by: Sep 20 2026, 6:00 PM)", deadline.getDisplayString());
        deadline.mark();
        assertEquals("[D][✓] submit report (by: Sep 20 2026, 6:00 PM)", deadline.getDisplayString());
    }
}
