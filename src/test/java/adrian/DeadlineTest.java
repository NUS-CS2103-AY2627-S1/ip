package adrian;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and storage representations of {@link Deadline} objects.
 */
public class DeadlineTest {

    /**
     * Verifies the display representation of an incomplete deadline.
     */
    @Test
    public void toString_unmarkedDeadline_correctString() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 6, 6, 18, 0);
        Deadline deadline = new Deadline("return book", dueDateTime);

        assertEquals(
                "[D][ ] return book (by: Jun 06 2026, 6:00 pm)",
                deadline.toString());
    }

    /**
     * Verifies the display representation of a completed deadline.
     */
    @Test
    public void toString_markedDeadline_correctString() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 6, 6, 18, 0);
        Deadline deadline = new Deadline("return book", dueDateTime);
        deadline.markAsDone();

        assertEquals(
                "[D][X] return book (by: Jun 06 2026, 6:00 pm)",
                deadline.toString());
    }

    /**
     * Verifies the storage representation of a completed deadline.
     */
    @Test
    public void toDataString_markedDeadline_correctString() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 6, 6, 18, 0);
        Deadline deadline = new Deadline("return book", dueDateTime);
        deadline.markAsDone();

        assertEquals(
                "D | 1 | return book | 2026-06-06T18:00",
                deadline.toDataString());
    }
}
