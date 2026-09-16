package miku.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import miku.MikuException;
import miku.task.Deadline;
import miku.task.Event;
import miku.task.Task;
import miku.task.TaskType;
import miku.task.Todo;

/** Tests assumptions that protect Storage's fixed task schema. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveTasks_unsupportedTaskType_assertionThrown() {
        Storage storage = new Storage(temporaryDirectory.resolve("miku.json"));

        assertThrows(AssertionError.class, () -> storage.saveTasks(List.of(new OtherTask())));
    }

    @Test
    void saveTasks_supportedTypes_roundTripWithMarkers() throws IOException, MikuException {
        Path saveFile = temporaryDirectory.resolve("miku.json");
        Storage storage = new Storage(saveFile);
        Deadline deadline = new Deadline("submit work", LocalDateTime.of(2026, 9, 8, 10, 0), true);
        deadline.markAsDone();
        List<Task> tasks = List.of(
                new Todo("read book"),
                deadline,
                new Event("concert", LocalDateTime.of(2026, 9, 9, 18, 0), true,
                        LocalDateTime.of(2026, 9, 9, 20, 0), true));

        storage.saveTasks(tasks);
        List<Task> loadedTasks = storage.loadTasks();
        String savedJson = Files.readString(saveFile);

        assertEquals(List.of("T", "D", "E"), loadedTasks.stream().map(Task::getType).toList());
        assertTrue(loadedTasks.get(1).isDone());
        assertTrue(savedJson.contains("\"type\": \"T\""));
        assertTrue(savedJson.contains("\"type\": \"D\""));
        assertTrue(savedJson.contains("\"type\": \"E\""));
    }

    @Test
    void loadTasks_missingCompletionStatus_exceptionThrown() throws IOException {
        Path saveFile = temporaryDirectory.resolve("miku.json");
        Files.writeString(saveFile, "[{\"type\": \"T\", \"description\": \"read book\"}]");
        Storage storage = new Storage(saveFile);

        assertThrows(MikuException.class, storage::loadTasks);
    }

    @Test
    void loadTasks_legacyDeadlineWithoutTimeFlag_infersTimeFromTimestamp() throws IOException, MikuException {
        Path saveFile = temporaryDirectory.resolve("miku.json");
        Files.writeString(saveFile, "[{\"type\": \"D\", \"description\": \"submit work\", "
                + "\"isDone\": false, \"datetime\": \"2026-09-08T00:00\"}]");
        Storage storage = new Storage(saveFile);

        Task loadedTask = storage.loadTasks().get(0);
        Deadline deadline = assertInstanceOf(Deadline.class, loadedTask);

        assertFalse(deadline.hasDueTime());
    }

    /** Represents a future task subtype that Storage has not yet learned to serialize. */
    private static class OtherTask extends Task {
        private OtherTask() {
            super(TaskType.TODO, "unserializable task");
        }
    }
}
