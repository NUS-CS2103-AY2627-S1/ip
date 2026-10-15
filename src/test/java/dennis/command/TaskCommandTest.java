package dennis.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dennis.DennisException;
import dennis.storage.Storage;
import dennis.task.Task;
import dennis.task.TaskList;
import dennis.task.Todo;
import dennis.ui.Ui;

/**
 * Tests for {@link TaskCommand#checkInRange(TaskList)}, the guard that every
 * {@code mark} / {@code unmark} / {@code delete} runs before touching a task.
 *
 * <p>The task number is 1-based, so the valid range is {@code 1..size}
 * inclusive. {@code checkInRange} is {@code protected}, so a tiny concrete
 * subclass exposes it for testing.</p>
 */
public class TaskCommandTest {

    /** Minimal concrete {@link TaskCommand} that just exposes checkInRange. */
    private static final class RangeProbe extends TaskCommand {
        private RangeProbe(int taskNumber) {
            super(taskNumber);
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) {
            // Not used by these tests.
        }

        private void check(TaskList tasks) throws DennisException {
            checkInRange(tasks);
        }
    }

    /**
     * Builds a task list containing {@code count} throwaway todos.
     *
     * @return the built list
     */
    private static TaskList listOf(int count) throws DennisException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < count; i++) {
            Task t = new Todo("task " + i);
            tasks.add(t);
        }
        return tasks;
    }

    @Test
    public void checkInRange_firstPosition_passes() throws DennisException {
        TaskList tasks = listOf(3);
        assertDoesNotThrow(() -> new RangeProbe(1).check(tasks));
    }

    @Test
    public void checkInRange_lastPosition_passes() throws DennisException {
        TaskList tasks = listOf(3);
        assertDoesNotThrow(() -> new RangeProbe(3).check(tasks));
    }

    @Test
    public void checkInRange_onePastTheEnd_throwsWithExceedsMessage()
            throws DennisException {
        TaskList tasks = listOf(3);
        DennisException e = assertThrows(DennisException.class, () ->
                new RangeProbe(4).check(tasks));
        assertEquals("Index is out of range!!", e.getMessage());
    }

    @Test
    public void checkInRange_zero_throwsDennisException() throws DennisException {
        TaskList tasks = listOf(3);
        assertThrows(DennisException.class, () ->
                new RangeProbe(0).check(tasks));
    }

    @Test
    public void checkInRange_negative_throwsDennisException()
            throws DennisException {
        TaskList tasks = listOf(3);
        assertThrows(DennisException.class, () ->
                new RangeProbe(-1).check(tasks));
    }

    @Test
    public void checkInRange_anyPositionOnEmptyList_throwsDennisException()
            throws DennisException {
        TaskList empty = listOf(0);
        assertThrows(DennisException.class, () ->
                new RangeProbe(1).check(empty));
    }
}
