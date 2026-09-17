package nova.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

/**
 * Tests persistence round-tripping and validation at the storage boundary.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() throws NovaException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesOrderStatusDatesAndEscapedText()
            throws NovaException {
        Path dataFile = temporaryDirectory.resolve("nested/nova.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | review \\ notes");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 0, 0), false);
        Event event = new Event("conference",
                LocalDateTime.of(2019, 12, 3, 14, 0), true,
                LocalDateTime.of(2019, 12, 4, 0, 0), false);

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertTrue(Files.exists(dataFile));
        assertEquals(List.of(
                "T | 1 | read \\| review \\\\ notes",
                "D | 0 | return book | 2019-12-02",
                "E | 0 | conference | 2019-12-03 1400 | 2019-12-04"),
                loadedTasks.stream().map(Task::toFileString).toList());
    }

    @Test
    public void save_replacingExistingFile_removesStaleTasks() throws NovaException, IOException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        Files.writeString(dataFile, "T | 0 | stale task\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        storage.save(List.of(new Todo("current task")));

        assertEquals("T | 0 | current task" + System.lineSeparator(),
                Files.readString(dataFile, StandardCharsets.UTF_8));
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith("nova-")));
        }
    }

    @Test
    public void load_blankLines_ignoresBlankLinesAndPreservesTaskOrder()
            throws IOException, NovaException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        Files.writeString(dataFile, "\nT | 0 | first\n   \nT | 1 | second\n",
                StandardCharsets.UTF_8);

        List<Task> tasks = new Storage(dataFile).load();

        assertEquals(List.of("T | 0 | first", "T | 1 | second"),
                tasks.stream().map(Task::toFileString).toList());
    }

    @Test
    public void load_invalidCompletionState_reportsLineAndCause() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        Files.writeString(dataFile, "T | 0 | valid\nT | yes | invalid\n",
                StandardCharsets.UTF_8);

        NovaException exception = assertThrows(NovaException.class, () ->
                new Storage(dataFile).load());

        assertEquals("I couldn't load your saved tasks because line 2 is invalid: "
                + "the completion state must be 0 or 1. Starting with an empty task list.",
                exception.getMessage());
    }

    @Test
    public void load_eventEndingBeforeStart_reportsInvalidStoredRange() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        Files.writeString(dataFile,
                "E | 0 | meeting | 2019-12-03 | 2019-12-02\n", StandardCharsets.UTF_8);

        NovaException exception = assertThrows(NovaException.class, () ->
                new Storage(dataFile).load());

        assertEquals("I couldn't load your saved tasks because line 1 is invalid: "
                + "the stored event ends before it starts. Starting with an empty task list.",
                exception.getMessage());
    }
}
