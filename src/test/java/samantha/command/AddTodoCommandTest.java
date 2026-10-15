package samantha.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.exception.TaskValidationException;
import samantha.model.TaskList;
import samantha.model.Todo;
import samantha.storage.Storage;
import samantha.ui.Ui;

class AddTodoCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validTodo_addsAndPersistsTask() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();

        String output = CommandTestOutput.capture(() ->
                new AddTodoCommand("read book").execute(tasks, new Ui(), storage));

        assertEquals(1, tasks.size());
        assertInstanceOf(Todo.class, tasks.getTask(1));
        assertEquals("T | 0 | read book", storage.load().getFirst().toFileString());
        assertTrue(output.contains("I'll remember that"));
    }

    @Test
    void execute_blankDescription_taskValidationExceptionAndNoTaskAdded() {
        TaskList tasks = new TaskList();

        assertThrows(TaskValidationException.class, () -> new AddTodoCommand(" ").execute(
                tasks, new Ui(), new Storage(temporaryDirectory.resolve("tasks.txt"))));
        assertEquals(0, tasks.size());
    }
}
