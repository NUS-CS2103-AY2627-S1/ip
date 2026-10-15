package avon.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import avon.exception.StorageException;
import avon.task.Deadline;
import avon.task.Event;
import avon.task.Task;
import avon.task.TaskList;
import avon.task.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void constructor_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Storage(null));
    }

    @Test
    void saveAndLoad_multipleTaskTypes_restoresTasks() throws IOException, StorageException {
        Path dataFile = temporaryDirectory.resolve("data/avon.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read Hamlet");
        todo.markAsDone();
        TaskList taskList = new TaskList(List.of(
                todo,
                new Deadline("return book", LocalDateTime.of(2026, 8, 20, 18, 0)),
                new Event("lecture", LocalDateTime.of(2026, 8, 20, 14, 0),
                        LocalDateTime.of(2026, 8, 20, 16, 0))));

        storage.save(taskList);
        List<Task> loadedTasks = storage.load();

        assertEquals(List.of(
                "T\ttrue\tread Hamlet",
                "D\tfalse\treturn book\t2026-08-20T18:00",
                "E\tfalse\tlecture\t2026-08-20T14:00\t2026-08-20T16:00"),
                Files.readAllLines(dataFile));
        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals("[D][ ] return book (by: Aug 20 2026, 6:00PM)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] lecture (from: Aug 20 2026, 2:00PM"
                + " to: Aug 20 2026, 4:00PM)", loadedTasks.get(2).toString());
    }

    @Test
    void load_legacyDateOnlyDeadline_restoresDeadlineAtMidnight()
            throws IOException, StorageException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "D\tfalse\treturn book\t2026-08-20");

        Storage storage = new Storage(dataFile);

        List<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertEquals("[D][ ] return book (by: Aug 20 2026)", loadedTasks.get(0).toString());
    }

    @Test
    void load_malformedTaskData_throwsStorageException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "D\tfalse\tmissing date");

        Storage storage = new Storage(dataFile);

        assertThrows(StorageException.class, storage::load);
    }

    @Test
    void load_invalidStoredDate_throwsStorageException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "E\tfalse\tparty\tnow\ttomorrow");

        Storage storage = new Storage(dataFile);

        assertThrows(StorageException.class, storage::load);
    }

    @Test
    void saveAndLoad_descriptionWithDelimiters_restoresDescription()
            throws IOException, StorageException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Storage storage = new Storage(dataFile);
        String description = "read\tHamlet\\notes\nnext line";

        storage.save(new TaskList(List.of(new Todo(description))));
        List<Task> loadedTasks = storage.load();

        assertEquals("T\tfalse\tread\\tHamlet\\\\notes\\nnext line",
                Files.readString(dataFile).strip());
        assertEquals(description, loadedTasks.get(0).getDescription());
    }

    @Test
    void load_invalidEscapeSequence_throwsStorageException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "T\tfalse\tread\\qHamlet");

        Storage storage = new Storage(dataFile);

        assertThrows(StorageException.class, storage::load);
    }

    @Test
    void load_invalidCompletionState_throwsStorageException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "T\tyes\tread Hamlet");

        Storage storage = new Storage(dataFile);

        assertThrows(StorageException.class, storage::load);
    }

    @Test
    void load_versionedTaskType_throwsStorageException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "T2\tfalse\tread Hamlet");

        Storage storage = new Storage(dataFile);

        assertThrows(StorageException.class, storage::load);
    }

    @Test
    void load_duplicateTasksWithDifferentCompletionStates_throwsStorageException()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "T\tfalse\tread Hamlet\nT\ttrue\tread Hamlet");

        Storage storage = new Storage(dataFile);

        assertThrows(StorageException.class, storage::load);
    }
}
