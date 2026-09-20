package grower.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Verifies serialization of deadline tasks and their completion status.
 */
public class DeadlineTest {
    @Test
    public void toFileString_incompleteDeadline_returnsIncompleteSerializedDeadline() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDateTime.of(2026, 8, 31, 23, 59));

        assertEquals(
                "D | 0 | submit assignment | 2026-08-31T23:59:00",
                deadline.toFileString());
    }

    @Test
    public void toFileString_completedDeadline_returnsCompletedSerializedDeadline() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDateTime.of(2026, 8, 31, 23, 59));
        deadline.mark();

        assertEquals(
                "D | 1 | submit assignment | 2026-08-31T23:59:00",
                deadline.toFileString());
    }
}
