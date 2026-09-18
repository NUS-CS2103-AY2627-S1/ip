package bot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior Task implements itself (status icon, marking done/not
 * done, {@link Task#toString()}, and the default {@link Task#occursOn}).
 * Task is abstract, so these run against a minimal concrete stub rather
 * than any real task type - each real subclass's own overrides (e.g.
 * Deadline's occursOn) are covered by that subclass's own test.
 */
class TaskTest {

    /** The simplest possible concrete Task, with a placeholder save format. */
    private static class StubTask extends Task {
        StubTask(String description) {
            super(description);
        }

        @Override
        public String toSaveFormat() {
            return "STUB | " + (isDone() ? "1" : "0") + " | " + getDescription();
        }
    }

    @Test
    void getStatusIcon_newTask_returnsSpace() {
        Task task = new StubTask("read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getStatusIcon_afterMarkAsDone_returnsX() {
        Task task = new StubTask("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_afterMarkAsDone_returnsToSpace() {
        Task task = new StubTask("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void toString_notDone_showsSpaceIconAndDescription() {
        Task task = new StubTask("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toString_done_showsXIconAndDescription() {
        Task task = new StubTask("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void occursOn_defaultImplementation_alwaysReturnsFalse() {
        Task task = new StubTask("read book");
        assertFalse(task.occursOn(LocalDate.of(2019, 10, 15)));
    }
}
