package goop.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the user-facing date-time formatting of {@link Deadline}.
 */
class DeadlineTest {
    @Test
    void toString_completedDeadline_formatsDateAndTwelveHourTime() {
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 1, 5, 0, 5));
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Jan 5 2026, 12:05 AM)",
                deadline.toString());
    }
}
