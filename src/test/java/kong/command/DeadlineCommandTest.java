package kong.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.Deadline;
import kong.task.TaskList;
import kong.ui.Ui;

class DeadlineCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validDeadline_addsTaskSavesAndOutputs() throws Exception {
        TaskList tasks = new TaskList();
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        DeadlineCommand command = new DeadlineCommand("return book", LocalDate.parse("2019-10-15"));

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(0).toString());
        assertTrue(output.contains("Splendid addition! I have inscribed this task into your archives:"));
        assertEquals(1, storage.loadTasks().size());
    }

    @Test
    void execute_duplicateDeadline_throwsKongException() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDate.parse("2019-10-15")));
        Ui ui = new Ui(message -> { });
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        DeadlineCommand command = new DeadlineCommand("return book", LocalDate.parse("2019-10-15"));

        KongException exception = assertThrows(KongException.class, () ->
                command.execute(tasks, ui, storage));

        assertEquals("This task already exists in your list.", exception.getMessage());
    }
}
