package bags.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bags.exception.BagsException;

/**
 * Tests the {@link Deadline} class.
 */
class DeadlineTest {

    @Test
    void constructor_validDeadline_parsesDateAndFormatsTask() throws BagsException {
        Deadline deadline = new Deadline("Submit report", "2026-09-12 23:59");

        assertEquals(LocalDateTime.of(2026, 9, 12, 23, 59), deadline.getDeadline());
        assertEquals("[D][ ] Submit report (by: 12/09/2026 11:59PM)", deadline.toString());
        assertEquals("D | [ ] | Submit report | 2026-09-12 23:59", deadline.parseEvent());
    }

    @Test
    void createTask_validCommand_createsDeadline() throws BagsException {
        Deadline deadline = Deadline.createTask("deadline Submit report /by 2026-09-12 23:59");

        assertEquals("Submit report", deadline.getDescription());
        assertEquals(TaskType.DEADLINE, deadline.getType());
        assertEquals(LocalDateTime.of(2026, 9, 12, 23, 59), deadline.getDeadline());
    }

    @Test
    void createTask_missingDescription_throwsException() {
        BagsException exception = assertThrows(
                BagsException.class, () -> Deadline.createTask("deadline /by 2026-09-12 23:59")
        );

        assertTrue(exception.getMessage().contains("Missing task description"));
    }

    @Test
    void createTask_missingByKeyword_throwsException() {
        BagsException exception = assertThrows(
                BagsException.class, () -> Deadline.createTask("deadline Submit report")
        );

        assertTrue(exception.getMessage().contains("Missing /by"));
    }

    @Test
    void constructor_invalidDeadline_throwsException() {
        BagsException exception = assertThrows(
                BagsException.class, () -> new Deadline("Submit report", "2026-02-30 10:00")
        );

        assertTrue(exception.getMessage().contains("Invalid date"));
    }
}
