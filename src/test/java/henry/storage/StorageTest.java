package henry.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import henry.task.Deadline;
import henry.task.Event;
import henry.task.Task;
import henry.task.Todo;

/**
 * Tests saving, loading, and recovery from malformed task records in {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyResult() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        Storage.LoadResult result = storage.load();

        assertEquals(0, result.tasks().size());
        assertEquals(0, result.skippedLineCount());
    }

    @Test
    public void save_tasks_createsParentDirectoryAndWritesAllTaskTypes() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("henry.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read book");
        todo.setDone(true);
        List<Task> tasks = List.of(
                todo,
                new Deadline("submit report", LocalDateTime.of(2025, 8, 26, 18, 0)),
                new Event("meeting", LocalDateTime.of(2025, 8, 27, 14, 0),
                        LocalDateTime.of(2025, 8, 27, 15, 0)));

        storage.save(tasks);

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | submit report | 2025-08-26T18:00",
                "E | 0 | meeting | 2025-08-27T14:00 | 2025-08-27T15:00"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void saveThenLoad_specialCharacters_roundTripsAllFields() throws IOException {
        Path dataFile = temporaryDirectory.resolve("henry.txt");
        Storage storage = new Storage(dataFile);
        List<Task> originalTasks = List.of(
                new Todo("read | revise \\ notes"),
                new Event("team | meeting", LocalDateTime.of(2025, 8, 27, 14, 0),
                        LocalDateTime.of(2025, 8, 27, 15, 0)));

        storage.save(originalTasks);
        Storage.LoadResult result = storage.load();

        assertEquals(0, result.skippedLineCount());
        assertEquals(originalTasks.stream().map(Task::toFileString).toList(),
                result.tasks().stream().map(Task::toFileString).toList());
    }

    @Test
    public void load_validAndMalformedRecords_loadsValidRecordsAndCountsSkippedRecords()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("henry.txt");
        Files.write(dataFile, List.of(
                "T | 1 | completed todo",
                "",
                "D | 0 | submit report | 2025-08-26T18:00",
                "E | 0 | meeting | 2025-08-27T14:00 | 2025-08-27T15:00",
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "D | 0 | invalid deadline | tomorrow",
                "T | 0 | "), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.load();

        assertEquals(4, result.skippedLineCount());
        assertEquals(List.of(
                "T | 1 | completed todo",
                "D | 0 | submit report | 2025-08-26T18:00",
                "E | 0 | meeting | 2025-08-27T14:00 | 2025-08-27T15:00"),
                result.tasks().stream().map(Task::toFileString).toList());
    }

    @Test
    public void load_unmarkedRecord_keepsTaskNotDone() throws IOException {
        Path dataFile = temporaryDirectory.resolve("henry.txt");
        Files.writeString(dataFile, "T | 0 | pending todo", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.load();

        assertFalse(result.tasks().get(0).isDone());
    }
}
