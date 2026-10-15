package orion.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    @Test
    public void toFileFormat_uncompletedDeadline_success() {
        LocalDateTime date = LocalDateTime.of(2026, 9, 4, 18, 30);
        Deadline deadline = new Deadline("submit assignment", date);
        assertEquals("D | 0 | submit assignment | 2026-09-04 1830", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_completedDeadline_success() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 26, 10, 0);
        Deadline deadline = new Deadline("submit assignment", date, true);
        assertEquals("D | 1 | submit assignment | 2026-08-26 1000", deadline.toFileFormat());
    }
}
