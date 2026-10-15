package samantha.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.exception.InputException;
import samantha.model.TaskList;
import samantha.model.Todo;
import samantha.storage.Storage;
import samantha.ui.Ui;

class MarkCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_middleTaskId_marksOnlyMiddleTaskAndPersists() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = taskListWithThreeTodos();

        new MarkCommand(2).execute(tasks, new Ui(), storage);

        assertFalse(tasks.getTask(1).isDone());
        assertTrue(tasks.getTask(2).isDone());
        assertFalse(tasks.getTask(3).isDone());
        assertTrue(storage.load().get(1).isDone());
    }

    @Test
    void execute_invalidTaskId_inputExceptionAndNoTaskChanged() throws Exception {
        TaskList tasks = taskListWithThreeTodos();

        assertThrows(InputException.class, () -> new MarkCommand(4).execute(tasks, new Ui(),
                new Storage(temporaryDirectory.resolve("tasks.txt"))));
        assertFalse(tasks.getTask(1).isDone());
        assertFalse(tasks.getTask(2).isDone());
        assertFalse(tasks.getTask(3).isDone());
    }

    private TaskList taskListWithThreeTodos() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));
        return tasks;
    }
}
