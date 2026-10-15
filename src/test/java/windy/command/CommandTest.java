package windy.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import windy.storage.Storage;
import windy.task.Task;
import windy.task.TaskList;
import windy.task.Todo;
import windy.ui.Ui;

/** Tests command execution and task persistence. */
public class CommandTest {
    @TempDir
    Path tempDirectory;

    @Test
    public void execute_addTaskCommand_addsAndSavesTask()
            throws Exception {
        TaskList tasks = new TaskList(new ArrayList<>());
        Storage storage = createStorage();
        CommandContext context = new CommandContext(tasks, new Ui(), storage);

        new AddTaskCommand(new Todo("read book", false)).execute(context);

        assertEquals(1, tasks.getNumTasks());
        assertEquals(List.of("T | 0 | read book"),
                storage.loadTasks().stream().map(Task::toDataString).toList());
    }

    @Test
    public void execute_markUnmarkAndDeleteCommands_updateTaskList() {
        Todo todo = new Todo("read book", false);
        TaskList tasks = new TaskList(new ArrayList<>(List.of(todo)));
        Storage storage = createStorage();
        Ui ui = new Ui();
        CommandContext context = new CommandContext(tasks, ui, storage);

        new MarkCommand(0).execute(context);
        assertTrue(tasks.getTask(0).isDone());

        new UnmarkCommand(0).execute(context);
        assertFalse(tasks.getTask(0).isDone());

        new DeleteCommand(0).execute(context);
        assertEquals(0, tasks.getNumTasks());
    }

    @Test
    public void execute_byeCommand_requestsExit() {
        TaskList tasks = new TaskList(new ArrayList<>());
        CommandContext context = new CommandContext(tasks, new Ui(), createStorage());

        new ByeCommand().execute(context);

        assertTrue(context.isExitRequested());
    }

    @Test
    public void execute_invalidTaskIndexes_throwAssertionError() {
        TaskList tasks = new TaskList(new ArrayList<>());
        Storage storage = createStorage();
        Ui ui = new Ui();

        CommandContext context = new CommandContext(tasks, ui, storage);

        assertThrows(AssertionError.class, () -> new MarkCommand(0).execute(context));
        assertThrows(AssertionError.class, () -> new UnmarkCommand(0).execute(context));
        assertThrows(AssertionError.class, () -> new DeleteCommand(0).execute(context));
    }

    private Storage createStorage() {
        return new Storage(tempDirectory.resolve("data/windy.txt").toString());
    }
}
