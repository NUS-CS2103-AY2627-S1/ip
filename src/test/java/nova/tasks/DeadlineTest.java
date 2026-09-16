package nova.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour of Deadline tasks.
 */
public class DeadlineTest {

    @Test
    public void toString_validDeadline_returnsFormattedDeadline() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDateTime.of(2026, 9, 10, 23, 59));

        assertEquals(
                "[D][ ] submit assignment (by: Sep 10 2026, 11:59 PM)",
                deadline.toString());
    }

    @Test
    public void toFileString_notDoneDeadline_returnsFileFormat() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDateTime.of(2026, 9, 10, 23, 59));

        assertEquals(
                "D | O | submit assignment | 2026-09-10T23:59",
                deadline.toFileString());
    }

    @Test
    public void toFileString_doneDeadline_returnsDoneFileFormat() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDateTime.of(2026, 9, 10, 23, 59));
        deadline.markDone();

        assertEquals(
                "D | X | submit assignment | 2026-09-10T23:59",
                deadline.toFileString());
    }
}