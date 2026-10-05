package gatsby.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the deadline-specific display and save-file representations. */
class DeadlineTest {

    private static final LocalDateTime DEADLINE = LocalDateTime.of(2019, 12, 2, 18, 0);

    /** Verifies the human-readable and persisted forms of an unfinished deadline. */
    @Test
    void unfinishedDeadline_formatsDateAndPendingStatus() {
        Deadline deadline = new Deadline("return book", DEADLINE);

        assertEquals("[D][ ] return book (by: Dec 02 2019 18:00:00)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-12-02T18:00", deadline.toFileFormat());
    }

    /** Verifies that a completed deadline retains its date and records its done status. */
    @Test
    void finishedDeadline_formatsDateAndDoneStatus() {
        Deadline deadline = new Deadline("return book", DEADLINE);
        deadline.markDone();

        assertEquals("[D][X] return book (by: Dec 02 2019 18:00:00)", deadline.toString());
        assertEquals("D | 1 | return book | 2019-12-02T18:00", deadline.toFileFormat());
    }
}
