package edith.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the user-facing and persistence representations of deadline tasks. */
class DeadlineTest {
    @Test
    void toString_parsedDateTime_usesReadableDateAndTime() {
        Deadline deadline = new Deadline("submit report", "28/8/2026 1430");

        assertEquals("[D][ ] submit report (by: Aug 28 2026 2:30pm)", deadline.toString());
    }

    @Test
    void toString_freeFormDeadline_preservesOriginalText() {
        Deadline deadline = new Deadline("buy groceries", "before dinner");

        assertEquals("[D][ ] buy groceries (by: before dinner)", deadline.toString());
    }

    @Test
    void toString_afterMarkingDone_showsDoneIcon() {
        Deadline deadline = new Deadline("submit report", "2026-08-28");
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Aug 28 2026)", deadline.toString());
    }

    @Test
    void toFileFormat_parsedDateTime_persistsIsoValueAndStatus() {
        Deadline deadline = new Deadline("submit report", "28/8/2026 1430");
        deadline.markAsDone();

        assertEquals("D | 1 | submit report | 2026-08-28T14:30", deadline.toFileFormat());
    }
}
