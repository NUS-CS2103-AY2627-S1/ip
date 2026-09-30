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

class DeleteCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validIndex_removesTaskSavesAndOutputs() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        DeleteCommand command = new DeleteCommand("1");

        command.execute(tasks, ui, storage);

        assertTrue(tasks.isEmpty());
        assertTrue(output.contains("Expunged from the archives! The following task has been removed:"));
        assertEquals(0, storage.loadTasks().size());
    }

    @Test
    void execute_outOfBoundsIndex_throwsKongException() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui(message -> { });
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        DeleteCommand command = new DeleteCommand("1");

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
        DeleteCommand command = new DeleteCommand("abc");

        KongException exception = assertThrows(KongException.class, () ->
                command.execute(tasks, ui, storage));

        assertEquals("Invalid command. A delete command needs to be followed by a number.",
                exception.getMessage());
    }
}
