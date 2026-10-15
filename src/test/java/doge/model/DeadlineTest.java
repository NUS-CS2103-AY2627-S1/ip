package doge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline-specific display and persistence behavior. */
class DeadlineTest {

    private static final LocalDateTime DEADLINE = LocalDateTime.of(2026, 6, 5, 14, 30);

    /** Verifies the display format of an incomplete deadline. */
    @Test
    void toString_incompleteDeadline_includesDueDate() {
        Deadline deadline = new Deadline("Submit report", DEADLINE);

        assertEquals("[D][ ] Submit report (by: Jun 05 2026, 2:30 PM)", deadline.toString());
    }

    /** Verifies that a completed deadline persists its status and timestamp. */
    @Test
    void toStorageString_completedDeadline_includesStatusAndDate() {
        Deadline deadline = new Deadline("Submit report", DEADLINE);
        deadline.markDone();

        assertEquals("D | 1 | Submit report | 2026-06-05T14:30", deadline.toStorageString());
    }

    /** Verifies that deadline priority is included in persistence output. */
    @Test
    void toStorageString_prioritizedDeadline_includesPriority() {
        Deadline deadline = new Deadline("Submit report", DEADLINE);
        deadline.setPriority(2);

        assertEquals("D | 0 | Submit report | 2026-06-05T14:30 | 2", deadline.toStorageString());
    }
}
