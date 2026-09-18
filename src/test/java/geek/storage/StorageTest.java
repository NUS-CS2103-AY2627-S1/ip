package geek.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import geek.task.Task;

/**
 * Tests task persistence without reading or writing the real data file.
 */
class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void loadTasks_missingFile_createsEmptyFile() throws IOException {
        Path filePath = tempDirectory.resolve("data/geek.txt");
        Storage storage = new Storage(filePath);

        Storage.LoadResult result = storage.loadTasks();

        assertAll(() -> assertTrue(Files.exists(filePath)), () ->
                assertTrue(result.tasks().isEmpty()), () ->
                assertTrue(result.corruptedLineNumbers().isEmpty()));
    }

    @Test
    void saveThenLoadTasks_validTasks_roundTripsAllData()
            throws IOException {
        Path filePath = tempDirectory.resolve("geek.txt");
        Storage storage = new Storage(filePath);
        Task todo = Task.newTodo("read book");
        todo.mark();
        Task deadline = Task.newDeadline(
                "submit report",
                "2/12/2019 1800"
        );
        Task event = Task.newEvent(
                "project meeting",
                "2/12/2019 1800",
                "2/12/2019 2000"
        );
        List<Task> originalTasks = List.of(todo, deadline, event);

        storage.saveTasks(originalTasks);
        Storage.LoadResult result = storage.loadTasks();

        assertAll(() -> assertEquals(
                        originalTasks.stream()
                                .map(Task::toDataString)
                                .toList(),
                        result.tasks().stream()
                                .map(Task::toDataString)
                                .toList()
                ), () -> assertTrue(
                        result.corruptedLineNumbers().isEmpty()
                )
        );
    }

    @Test
    void loadTasks_blankAndCorruptedLines_skipsAndReportsLines()
            throws IOException {
        Path filePath = tempDirectory.resolve("geek.txt");
        Task first = Task.newTodo("first");
        Task second = Task.newDeadline(
                "second",
                "2/12/2019"
        );
        Files.write(
                filePath,
                List.of(
                        first.toDataString(),
                        "",
                        "not a saved task",
                        second.toDataString()
                ),
                StandardCharsets.UTF_8
        );
        Storage storage = new Storage(filePath);

        Storage.LoadResult result = storage.loadTasks();

        assertAll(() -> assertEquals(
                        List.of(
                                first.toDataString(),
                                second.toDataString()
                        ),
                        result.tasks().stream()
                                .map(Task::toDataString)
                                .toList()
                ), () -> assertEquals(
                        List.of(3),
                        result.corruptedLineNumbers()
                )
        );
    }

    @Test
    void saveTasks_existingContents_replacesOldData()
            throws IOException {
        Path filePath = tempDirectory.resolve("geek.txt");
        Files.writeString(
                filePath,
                "obsolete data",
                StandardCharsets.UTF_8
        );
        Task replacement = Task.newTodo("replacement");
        Storage storage = new Storage(filePath);

        storage.saveTasks(List.of(replacement));

        assertEquals(
                List.of(replacement.toDataString()),
                Files.readAllLines(
                        filePath,
                        StandardCharsets.UTF_8
                )
        );
    }

    @Test
    void saveTasks_nestedMissingDirectory_createsPath()
            throws IOException {
        Path filePath = tempDirectory.resolve(
                "nested/data/geek.txt"
        );
        Storage storage = new Storage(filePath);

        storage.saveTasks(List.of());

        assertTrue(Files.exists(filePath));
    }
}
