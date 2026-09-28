package baron.core.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadline}.
 */
class DeadlineTest {

    /**
     * Verifies the persistent and user-facing representations of a deadline task.
     */
    @Test
    void formatting_deadlineProvided_expectedFormatsReturned() {
        Deadline deadline = new Deadline(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "submit report",
                LocalDateTime.of(2026, 8, 30, 18, 0));

        assertEquals(
                "00000000-0000-0000-0000-000000000001 | D | 0 | submit report | "
                        + " | 2026-08-30T18:00",
                deadline.toFileString());
        assertEquals(
                """
                [D][ ] submit report
                by: 06:00 PM, 30 Aug 2026""",
                deadline.toString());
    }
}
