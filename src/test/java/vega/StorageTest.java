package vega;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws VegaException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesDetailsAndStatus() throws VegaException {
        Path taskFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 18));
        Event event = new Event("project meeting", "2pm", "4pm");
        deadline.markAsDone();

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.loadTasks();

        assertTrue(Files.exists(taskFile));
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Sep 18 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: 2pm to: 4pm)", loadedTasks.get(2).toString());
        assertFalse(loadedTasks.get(0).isDone());
        assertTrue(loadedTasks.get(1).isDone());
    }

    @Test
    public void loadTasks_malformedLine_throwsVegaException() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "invalid task data", StandardCharsets.UTF_8);
        Storage storage = new Storage(taskFile.toString());

        assertThrows(VegaException.class, storage::loadTasks);
    }
}
