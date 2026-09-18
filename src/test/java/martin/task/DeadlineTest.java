package martin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import martin.exception.IllegalCommandException;

/**
 * Unit tests for {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void createDeadline_validCommand_success() {
        Deadline deadline = Deadline.parseDeadlineFromInputString("deadline return book /by 2026/08/26 1830");
        assertEquals("[D][ ][LOW] return book (by: Aug 26 2026, 18:30)", deadline.toString());
        assertEquals("D | 0 | return book | 2026/08/26 1830 | L", deadline.toDataFormat());
    }

    @Test
    public void createDeadline_priorityParameter_createsDeadlineWithPriority() {
        Deadline deadline = Deadline.parseDeadlineFromInputString(
                "deadline return book /by 2026/08/26 1830 /p medium");

        assertEquals(Priority.MEDIUM, deadline.getPriority());
        assertEquals("[D][ ][MEDIUM] return book (by: Aug 26 2026, 18:30)", deadline.toString());
    }

    @Test
    public void createDeadline_missingByDelimiter_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () ->
                Deadline.parseDeadlineFromInputString("deadline return book 2026/08/26 1830"));
    }

    @Test
    public void createDeadline_emptyDescription_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () ->
                Deadline.parseDeadlineFromInputString("deadline /by 2026/08/26 1830"));
    }

    @Test
    public void createDeadline_invalidPriority_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () ->
                Deadline.parseDeadlineFromInputString("deadline return book /by 2026/08/26 1830 /p urgent"));
    }

    @Test
    public void createDeadline_repeatedByDelimiter_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () -> Deadline.parseDeadlineFromInputString(
                "deadline return book /by 2026/08/26 1830 /by 2026/08/27 1830"));
    }
}
