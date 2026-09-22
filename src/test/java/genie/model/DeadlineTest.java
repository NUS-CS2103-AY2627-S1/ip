package genie.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_completedDeadline_returnsFormattedDeadline() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 7, 21, 18, 0);
        Deadline deadline = new Deadline("do dishes", dueDate);
        deadline.markAsDone();

        assertEquals("[D][X] do dishes (by: Jul 21 2026, 6:00 PM)", deadline.toString());
    }
}
