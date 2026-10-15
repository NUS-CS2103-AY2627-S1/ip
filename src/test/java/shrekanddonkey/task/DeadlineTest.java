package shrekanddonkey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task operations and string representations of {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void toString_validDeadline_formattedWithDateAndMarker() {
        LocalDateTime by = LocalDateTime.of(2026, 10, 15, 18, 30);
        Deadline deadline = new Deadline("submit report", by);
        assertEquals("[D][ ] submit report (by: Oct 15 2026, 6:30 PM)", deadline.toString());
    }

    @Test
    public void toString_completedDeadline_formattedWithDoneMarker() {
        LocalDateTime by = LocalDateTime.of(2026, 10, 15, 18, 30);
        Deadline deadline = new Deadline("submit report", by);
        deadline.markAsDone();
        assertEquals("[D][X] submit report (by: Oct 15 2026, 6:30 PM)", deadline.toString());
    }
}
