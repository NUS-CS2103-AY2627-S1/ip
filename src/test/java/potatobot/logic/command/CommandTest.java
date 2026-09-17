package potatobot.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;
import potatobot.logic.CommandResult;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Exercises command results, task-number boundaries and failure atomicity.
 */
public class CommandTest {
    private final Storage unusedStorage = new Storage("unused.txt");

    @Test
    public void execute_numberOutsideList_rejectsWithoutMutation() throws PotatoBotException {
        TaskList tasks = new TaskList();
        Task task = new Task("original");
        tasks.add(task);
        for (int number : new int[] { Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE }) {
            for (Command command : new Command[] {
                new MarkCommand(number), new UnmarkCommand(number), new DeleteCommand(number)
            }) {
                PotatoBotException exception = assertThrows(PotatoBotException.class,
                        () -> command.execute(tasks, unusedStorage));
                assertEquals(number <= 0 ? "Do you hear yourself??"
                        : "Think again... We only got 1 items in the list...", exception.getMessage());
                assertEquals(1, tasks.size());
                assertSame(task, tasks.get(0));
                assertEquals(" ", task.getStatusIcon());
            }
        }
    }

    @Test
    public void execute_numberOnEmptyList_reportsZeroItems() {
        for (Command command : new Command[] { new MarkCommand(1), new UnmarkCommand(1), new DeleteCommand(1) }) {
            PotatoBotException exception = assertThrows(PotatoBotException.class,
                    () -> command.execute(new TaskList(), unusedStorage));
            assertEquals("Think again... We only got 0 items in the list...", exception.getMessage());
        }
    }

    @Test
    public void execute_lastTaskNumber_updatesOnlySelectedTask() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("first"), new Task("last"));
        assertEquals("Task completed: last\nKeep going!",
                new MarkCommand(2).execute(tasks, unusedStorage).message());
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertEquals("X", tasks.get(1).getStatusIcon());
        assertEquals("Task Reset: last\nKeep going!",
                new UnmarkCommand(2).execute(tasks, unusedStorage).message());
        assertEquals(" ", tasks.get(1).getStatusIcon());
        assertEquals("Task deleted: last\nKeep it going!",
                new DeleteCommand(2).execute(tasks, unusedStorage).message());
        assertEquals(1, tasks.size());
        assertEquals("first", tasks.get(0).toString());
    }

    @Test
    public void execute_find_matchesCaseInsensitivelyWithoutMutation() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("skip"), new Task("Read BOOK"));
        tasks.markDone(1);
        CommandResult result = new FindCommand("book").execute(tasks, unusedStorage);
        assertEquals("Here are the tasks in your potato sack:\n  1.[X] Read BOOK", result.message());
        assertFalse(result.isError());
        assertFalse(result.isExit());
        assertEquals(2, tasks.size());
        assertEquals("Nothing to see here...", new FindCommand("missing").execute(tasks, unusedStorage).message());
    }

    @Test
    public void execute_undoWithoutApplicationHistory_returnsError() {
        CommandResult result = new UndoCommand().execute(new TaskList(), unusedStorage);
        assertEquals(UndoCommand.EMPTY_HISTORY_MESSAGE, result.message());
        assertTrue(result.isError());
        assertFalse(result.isExit());
    }

    @Test
    public void undo_beforeExecution_rejectsInvalidLifecycle() {
        for (UndoableCommand command : new UndoableCommand[] {
            new AddCommand(new Task("task")), new DeleteCommand(1), new MarkCommand(1), new UnmarkCommand(1)
        }) {
            assertThrows(AssertionError.class, () -> command.undo(new TaskList()));
        }
    }
}
