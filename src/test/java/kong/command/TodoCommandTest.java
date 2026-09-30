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

class TodoCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validTodo_addsTaskSavesAndOutputs() throws Exception {
        TaskList tasks = new TaskList();
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        TodoCommand command = new TodoCommand("read book");

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertTrue(output.contains("Splendid addition! I have inscribed this task into your archives:"));
        assertEquals(1, storage.loadTasks().size());
    }

    @Test
    void execute_duplicateTodo_throwsKongException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Ui ui = new Ui(message -> { });
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        TodoCommand command = new TodoCommand("read book");

        KongException exception = assertThrows(KongException.class, () ->
                command.execute(tasks, ui, storage));

        assertEquals("This task already exists in your list.", exception.getMessage());
    }
}
