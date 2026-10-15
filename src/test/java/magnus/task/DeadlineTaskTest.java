package magnus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline construction, identity, serialization, and display formatting.
 */
public class DeadlineTaskTest {
    @Test
    public void constructor_stringDeadline_parsesDeadline() {
        DeadlineTask task = new DeadlineTask("submit report", "20/09/2026 1730");

        assertEquals(LocalDateTime.of(2026, 9, 20, 17, 30), task.getDeadline());
        assertFalse(task.isDone());
    }

    @Test
    public void constructor_nullDeadline_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new DeadlineTask("submit report", (LocalDateTime) null));
    }

    @Test
    public void hasSameDetails_matchingScheduleIgnoresStatus() {
        DeadlineTask firstTask = new DeadlineTask("submit report", "20/09/2026 1730");
        DeadlineTask secondTask = new DeadlineTask("submit report", "20/09/2026 1730");
        secondTask.markAsDone();

        assertTrue(firstTask.hasSameDetails(secondTask));
    }

    @Test
    public void hasSameDetails_differentScheduleOrType_returnsFalse() {
        DeadlineTask task = new DeadlineTask("submit report", "20/09/2026 1730");

        assertFalse(task.hasSameDetails(new DeadlineTask("submit report", "20/09/2026 1800")));
        assertFalse(task.hasSameDetails(new ToDoTask("submit report")));
    }

    @Test
    public void serialize_completedTaskWithComma_escapesDescription() {
        DeadlineTask task = new DeadlineTask("submit report, appendix", "20/09/2026 1730");
        task.markAsDone();

        assertEquals("D,1,\"submit report, appendix\",20/09/2026 1730", task.serialize());
    }

    @Test
    public void toString_deadlineTask_usesDisplayDateFormat() {
        DeadlineTask task = new DeadlineTask("submit report", "02/09/2026 0507");

        assertEquals("[D][ ] submit report (by: Sep 02, 2026 05:07)", task.toString());
    }
}
