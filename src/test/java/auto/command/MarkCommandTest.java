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

class MarkCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_incompleteTask_marksPersistsAndAcknowledgesTask() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = writableStorage(tempDirectory);

        CommandResult result = new MarkCommand(1).execute(tasks, storage);

        assertTrue(task.isCompleted());
        assertTrue(storage.load().tasks().getFirst().isCompleted());
        assertTrue(result.message().contains("Steady lah! Marked this task as done:"));
    }

    @Test
    void execute_saveFails_restoresIncompleteStatus() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        CommandResult result = new MarkCommand(1).execute(tasks, failingStorage(tempDirectory));

        assertFalse(task.isCompleted());
        assertTrue(result.message().contains("Paiseh, I couldn't save your tasks."));
    }

    @Test
    void execute_alreadyCompletedAndSaveFails_keepsCompletedStatus() throws Exception {
        Task task = new ToDo("read book");
        task.mark();
        TaskList tasks = new TaskList(List.of(task));

        new MarkCommand(1).execute(tasks, failingStorage(tempDirectory));

        assertTrue(task.isCompleted());
    }

    @Test
    void execute_invalidNumber_throwsWithoutChangingTasks() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(AutoException.class,
                () -> new MarkCommand(2).execute(tasks, writableStorage(tempDirectory)));

        assertFalse(task.isCompleted());
    }
}
