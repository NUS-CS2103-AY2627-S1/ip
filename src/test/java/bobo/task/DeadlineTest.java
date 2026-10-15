package bobo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for Deadline task class.
 */
public class DeadlineTest {

    @Test
    public void testDeadlineCreationAndFormatting() {
        Deadline deadline = new Deadline("submit assignment", "2026-10-15 1800");
        assertEquals("[D][ ] submit assignment (by: Oct 15 2026, 6:00PM)", deadline.toString());
        assertEquals("D | 0 | submit assignment | 2026-10-15 1800", deadline.toTextLine());
    }

    @Test
    public void testIsOnDate() {
        Deadline deadline = new Deadline("submit assignment", "2026-10-15");
        assertTrue(deadline.isOnDate(LocalDate.of(2026, 10, 15)));
        assertFalse(deadline.isOnDate(LocalDate.of(2026, 10, 16)));
    }
}
