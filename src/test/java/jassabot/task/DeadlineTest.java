package jassabot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests Deadline display and storage representations.
 */
public class DeadlineTest {

    @Test
    public void toString_deadlineWithTime_formatsFriendlyDueDate() {
        Deadline deadline = new Deadline(
                "return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)",
                deadline.toString());
    }

    @Test
    public void toDataString_markedDeadline_usesStableFormatAndEscapesDescription() {
        Deadline deadline = new Deadline(
                "return | book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();

        assertEquals("D | 1 | return \\| book | 2019-12-02T18:00",
                deadline.toDataString());
    }
}
