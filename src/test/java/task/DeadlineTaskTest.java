package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for DeadlineTask: constructors, formatting, signatures and equality.
 */
public class DeadlineTaskTest {
    private static final LocalDateTime DEADLINE = LocalDateTime.of(2026, 9, 9, 10, 0);

    @Test
    public void constructor_withLocalDateTime_isNotDone() {
        DeadlineTask task = new DeadlineTask("submit report", DEADLINE);
        assertFalse(task.isDone());
        assertEquals(DEADLINE, task.getDeadline());
    }

    @Test
    public void constructor_withDefaultFormatString_parsesDeadline() {
        DeadlineTask task = new DeadlineTask("submit report", "09/09/2026 10:00");
        assertEquals(DEADLINE, task.getDeadline());
    }

    @Test
    public void constructor_withCustomFormatString_parsesDeadline() {
        DeadlineTask task = new DeadlineTask("submit report", "2026-09-09 10:00", "yyyy-MM-dd HH:mm");
        assertEquals(DEADLINE, task.getDeadline());
    }

    @Test
    public void constructor_withDoneFlagAndLocalDateTime_setsCompletionState() {
        DeadlineTask task = new DeadlineTask("submit report", true, DEADLINE);
        assertTrue(task.isDone());
    }

    @Test
    public void constructor_withDoneFlagAndDefaultFormatString_parsesDeadline() {
        DeadlineTask task = new DeadlineTask("submit report", true, "09/09/2026 10:00");
        assertTrue(task.isDone());
        assertEquals(DEADLINE, task.getDeadline());
    }

    @Test
    public void constructor_withDoneFlagAndCustomFormatString_parsesDeadline() {
        DeadlineTask task = new DeadlineTask(
                "submit report", true, "2026-09-09 10:00", "yyyy-MM-dd HH:mm");
        assertTrue(task.isDone());
        assertEquals(DEADLINE, task.getDeadline());
    }

    @Test
    public void toSignature_reflectsCompletionState() {
        assertEquals(
                "D|0|submit report|09/09/2026 10:00",
                new DeadlineTask("submit report", DEADLINE).toSignature());
        assertEquals(
                "D|1|submit report|09/09/2026 10:00",
                new DeadlineTask("submit report", true, DEADLINE).toSignature());
    }

    @Test
    public void toString_reflectsCompletionState() {
        assertEquals(
                "[D][ ] submit report (by: 09/09/2026 10:00)",
                new DeadlineTask("submit report", DEADLINE).toString());
        assertEquals(
                "[D][X] submit report (by: 09/09/2026 10:00)",
                new DeadlineTask("submit report", true, DEADLINE).toString());
    }

    @Test
    public void equals_sameDescriptionAndDeadlineIsEqual() {
        assertEquals(
                new DeadlineTask("submit report", DEADLINE),
                new DeadlineTask("submit report", true, DEADLINE));
    }

    @Test
    public void equals_differentDeadlineIsNotEqual() {
        assertFalse(new DeadlineTask("submit report", DEADLINE)
                .equals(new DeadlineTask("submit report", DEADLINE.plusHours(1))));
    }

    @Test
    public void equals_differentTaskTypeIsNotEqual() {
        assertFalse(new DeadlineTask("submit report", DEADLINE)
                .equals(new Task("submit report")));
    }
}
