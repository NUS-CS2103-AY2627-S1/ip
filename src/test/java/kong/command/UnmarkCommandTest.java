package kong.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.TaskList;
import kong.task.Todo;
import kong.ui.Ui;

class UnmarkCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validIndex_marksTaskUndoneSavesAndOutputs() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book", true));
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        UnmarkCommand command = new UnmarkCommand("1");

        command.execute(tasks, ui, storage);

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertTrue(output.contains(
                "Back to the drawing board! I have restored this task to pending status:"));
        assertEquals(1, storage.loadTasks().size());
        assertEquals("[T][ ] read book", storage.loadTasks().get(0).toString());
    }

    @Test
    void execute_outOfBoundsIndex_throwsKongException() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui(message -> { });
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        UnmarkCommand command = new UnmarkCommand("1");

        KongException exception = assertThrows(KongException.class, () ->
                command.execute(tasks, ui, storage));

        assertEquals("This task number is invalid. You currently have 0 tasks in your list.",
                exception.getMessage());
    }

    @Test
    void execute_nonNumericIndex_throwsKongException() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui(message -> { });
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        UnmarkCommand command = new UnmarkCommand("xyz");

        KongException exception = assertThrows(KongException.class, () ->
                command.execute(tasks, ui, storage));

        assertEquals("Invalid command. A unmark command needs to be followed by a number.",
                exception.getMessage());
    }
}
