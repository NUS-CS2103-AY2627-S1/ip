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
import kong.task.Event;
import kong.task.TaskList;
import kong.ui.Ui;

class EventCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validEvent_addsTaskSavesAndOutputs() throws Exception {
        TaskList tasks = new TaskList();
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        EventCommand command = new EventCommand("project meeting",
                LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16"));

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                tasks.get(0).toString());
        assertTrue(output.contains("Splendid addition! I have inscribed this task into your archives:"));
        assertEquals(1, storage.loadTasks().size());
    }

    @Test
    void execute_duplicateEvent_throwsKongException() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("project meeting", LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16")));
        Ui ui = new Ui(message -> { });
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        EventCommand command = new EventCommand("project meeting",
                LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16"));

        KongException exception = assertThrows(KongException.class, () ->
                command.execute(tasks, ui, storage));

        assertEquals("This task already exists in your list.", exception.getMessage());
    }
}
