package potatobot.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

public class UndoableCommandTest {
    private final Storage unusedStorage = new Storage("unused.txt");

    @Test
    public void undo_executedAddCommand_addedTaskRemoved() throws PotatoBotException {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(new Task("read book"));
        command.execute(tasks, unusedStorage);

        command.undo(tasks);

        assertEquals(0, tasks.size());
    }

    @Test
    public void undo_executedMarkCommand_taskMarkedAsIncomplete() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        MarkCommand command = new MarkCommand(1);
        command.execute(tasks, unusedStorage);

        command.undo(tasks);

        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void undo_executedUnmarkCommand_taskMarkedAsComplete() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book"));
        tasks.markDone(0);
        UnmarkCommand command = new UnmarkCommand(1);
        command.execute(tasks, unusedStorage);

        command.undo(tasks);

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void undo_executedDeleteCommand_taskRestoredAtOriginalIndex()
            throws PotatoBotException {
        TaskList tasks = new TaskList();
        Task firstTask = new Task("first");
        Task secondTask = new Task("second");
        tasks.add(firstTask, secondTask);
        DeleteCommand command = new DeleteCommand(1);
        command.execute(tasks, unusedStorage);

        command.undo(tasks);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }
}
