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

class UnmarkCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_doneTask_marksTaskIncompleteAndPersists() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        Todo todo = new Todo("read book");
        todo.markDone();
        TaskList tasks = new TaskList();
        tasks.add(todo);

        new UnmarkCommand(1).execute(tasks, new Ui(), storage);

        assertFalse(tasks.getTask(1).isDone());
        assertFalse(storage.load().getFirst().isDone());
    }

    @Test
    void execute_invalidTaskId_inputExceptionAndTaskRemainsDone() throws Exception {
        Todo todo = new Todo("read book");
        todo.markDone();
        TaskList tasks = new TaskList();
        tasks.add(todo);

        assertThrows(InputException.class, () -> new UnmarkCommand(2).execute(tasks, new Ui(),
                new Storage(temporaryDirectory.resolve("tasks.txt"))));
        assertTrue(tasks.getTask(1).isDone());
    }
}
