package nova.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingDataFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
        assertEquals(0, storage.getSkippedRecordCount());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesTaskDataAndCreatesParentDirectory() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("nova.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | café");
        todo.markAsDone();
        List<Task> originalTasks = List.of(
                todo,
                new Deadline("submit report", LocalDate.of(2026, 8, 24)),
                new Event("project meeting", "2pm", "4pm"));

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertTrue(Files.exists(dataFile));
        assertEquals(originalTasks.stream().map(Task::toString).toList(),
                loadedTasks.stream().map(Task::toString).toList());
        assertTrue(loadedTasks.get(0).isDone());
        assertFalse(loadedTasks.get(1).isDone());
        assertEquals(0, storage.getSkippedRecordCount());
    }

    @Test
    void load_legacyAndMalformedRecords_loadsValidTasksAndCountsSkippedRecords() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        Files.write(dataFile, List.of(
                "T | 1 | legacy todo",
                "D | 0 | return book | 2019-06-06",
                "E | 0 | meeting | 2pm | 4pm",
                "V2 | T | 2 | aW52YWxpZCBzdGF0dXM=",
                "V2 | T | 0 | not_base64!",
                "V2 | D | 0 | aW52YWxpZCBkYXRl | MjAxOS0wMi0zMA==",
                "V2 | X | 0 | dW5rbm93biB0eXBl",
                "corrupted task data"), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        List<Task> tasks = storage.load();

        assertEquals(List.of(
                "[T][X] legacy todo",
                "[D][ ] return book (by: Jun 6 2019)",
                "[E][ ] meeting (from: 2pm to: 4pm)"),
                tasks.stream().map(Task::toString).toList());
        assertEquals(5, storage.getSkippedRecordCount());
    }
}
