package doge.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import doge.exception.DogeException;
import doge.model.Deadline;
import doge.model.Event;
import doge.model.Task;
import doge.model.TaskList;
import doge.model.Todo;

/** Tests persistence and validation of task data. */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    /** Verifies that loading a missing file returns an empty task list. */
    @Test
    void load_missingFile_returnsEmptyTaskList() throws DogeException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(0, storage.load().size());
    }

    /** Verifies that all task types, status values, and priorities survive a round trip. */
    @Test
    void saveThenLoad_mixedTasks_preservesTaskData() throws DogeException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        Task todo = new Todo("Buy treats");
        todo.markDone();
        todo.setPriority(2);
        tasks.add(todo);
        tasks.add(new Deadline("Submit report", LocalDateTime.of(2026, 6, 5, 14, 30)));
        tasks.add(new Event("Team lunch", LocalDateTime.of(2026, 6, 5, 12, 0),
                LocalDateTime.of(2026, 6, 5, 13, 30)));

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(todo.toString(), loadedTasks.get(1).toString());
        assertEquals(tasks.get(2).toString(), loadedTasks.get(2).toString());
        assertEquals(tasks.get(3).toString(), loadedTasks.get(3).toString());
    }

    /** Verifies that blank lines are ignored while loading valid data. */
    @Test
    void loadFile_withBlankLines_ignoresBlankLines() throws IOException, DogeException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT | 0 | Walk Doge\n\n", StandardCharsets.UTF_8);

        TaskList tasks = new Storage(dataFile).load();

        assertEquals(1, tasks.size());
        assertEquals("Walk Doge", tasks.get(1).getDescription());
    }

    /** Verifies that an invalid status is rejected. */
    @Test
    void loadFile_invalidStatus_throwsDogeException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 2 | Walk Doge", StandardCharsets.UTF_8);

        assertThrows(DogeException.class, () -> new Storage(dataFile).load());
    }

    /** Verifies that an unknown persisted task type is rejected. */
    @Test
    void loadFile_unknownTaskType_throwsDogeException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "X | 0 | Unknown", StandardCharsets.UTF_8);

        assertThrows(DogeException.class, () -> new Storage(dataFile).load());
    }

    /** Verifies that invalid persisted priority data is rejected. */
    @Test
    void loadFile_invalidPriority_throwsDogeException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | Walk Doge | nope", StandardCharsets.UTF_8);

        assertThrows(DogeException.class, () -> new Storage(dataFile).load());
    }

    /** Verifies that persisted tasks with blank descriptions are rejected. */
    @Test
    void loadFile_blankDescription_throwsDogeException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 |   ", StandardCharsets.UTF_8);

        assertThrows(DogeException.class, () -> new Storage(dataFile).load());
    }
}
