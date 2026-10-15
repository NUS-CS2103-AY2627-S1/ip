package probe.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DeadlineTest {

    private static final LocalDateTime DEADLINE =
            LocalDateTime.of(2026, 8, 28, 14, 30);

    @Test
    void toStringIncludesDescriptionAndDeadline() {
        Deadline deadline = new Deadline("Submit report", DEADLINE);

        assertEquals("[D][ ] Submit report (by: Aug 28 2026, 2:30pm)",
                deadline.toString());
    }

    @Test
    void toFileFormatIncludesIncompleteStatus() {
        Deadline deadline = new Deadline("Submit report", DEADLINE);

        assertEquals("D | 0 | Submit report | 2026-08-28T14:30",
                deadline.toFileFormat());
    }

    @Test
    void toFileFormatIncludesCompletedStatus() {
        Deadline deadline = new Deadline("Submit report", DEADLINE);
        deadline.markAsDone();

        assertEquals("D | 1 | Submit report | 2026-08-28T14:30",
                deadline.toFileFormat());
    }
}
