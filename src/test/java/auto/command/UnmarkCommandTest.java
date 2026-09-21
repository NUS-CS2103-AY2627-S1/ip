package auto.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;
import auto.task.ToDo;

class UnmarkCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_completedTask_unmarksPersistsAndAcknowledgesTask() throws Exception {
        Task task = completedTask();
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = writableStorage(tempDirectory);

        CommandResult result = new UnmarkCommand(1).execute(tasks, storage);

        assertFalse(task.isCompleted());
        assertFalse(storage.load().tasks().getFirst().isCompleted());
        assertTrue(result.message().contains("No worries, marked this as not done yet. Take your time lah:"));
    }

    @Test
    void execute_saveFails_restoresCompletedStatus() throws Exception {
        Task task = completedTask();
        TaskList tasks = new TaskList(List.of(task));

        CommandResult result = new UnmarkCommand(1).execute(tasks, failingStorage(tempDirectory));

        assertTrue(task.isCompleted());
        assertTrue(result.message().contains("Paiseh, I couldn't save your tasks."));
    }

    @Test
    void execute_alreadyIncompleteAndSaveFails_keepsIncompleteStatus() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        new UnmarkCommand(1).execute(tasks, failingStorage(tempDirectory));

        assertFalse(task.isCompleted());
    }

    @Test
    void execute_invalidNumber_throwsWithoutChangingTasks() {
        Task task = completedTask();
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(AutoException.class,
                () -> new UnmarkCommand(0).execute(tasks, writableStorage(tempDirectory)));

        assertTrue(task.isCompleted());
    }

    private Task completedTask() {
        Task task = new ToDo("read book");
        task.mark();
        return task;
    }
}
