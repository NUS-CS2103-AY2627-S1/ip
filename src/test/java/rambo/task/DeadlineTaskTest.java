package rambo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the storage and display formats of {@link DeadlineTask}.
 */
public class DeadlineTaskTest {
    @Test
    void toDataString_deadlineTask_returnsStorageFormat() {
        DeadlineTask task = new DeadlineTask("submit report", "2026-09-15");

        assertEquals("D||submit report|2026-09-15|0", task.toDataString());
    }

    @Test
    void toDataString_completedDeadlineTask_includesDoneStatus() {
        DeadlineTask task = new DeadlineTask("submit report", "2026-09-15");
        task.toggleDone();

        assertEquals("D|X|submit report|2026-09-15|0", task.toDataString());
    }

    @Test
    void toString_deadlineTask_returnsFriendlyDateFormat() {
        DeadlineTask task = new DeadlineTask("submit report", "2026-09-15");

        assertEquals("[D][] submit report (by: Sep 15 2026)", task.toString());
    }

    @Test
    void toString_prioritizedDeadlineTask_includesPriority() {
        DeadlineTask task = new DeadlineTask("submit report", "2026-09-15");
        task.setPriorityLevel(2);

        assertEquals("[D][][P2] submit report (by: Sep 15 2026)", task.toString());
    }
}
