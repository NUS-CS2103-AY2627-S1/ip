package alfred.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alfred.exception.AlfredException;
import alfred.task.Deadline;
import alfred.task.Event;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.task.TaskType;
import alfred.task.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyTaskList() throws AlfredException {
        Storage storage = createStorage();

        assertEquals(0, storage.load().size());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesTaskDataAndStatus() throws AlfredException {
        Todo todo = new Todo("read\\notes\tcarefully\nsoon");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2024, 2, 29, 18, 0));
        Event event = new Event("team meeting", LocalDateTime.of(2024, 3, 1, 9, 0),
                LocalDateTime.of(2024, 3, 1, 10, 30));
        deadline.markAsDone();
        event.markAsDone();
        TaskList tasks = new TaskList(new ArrayList<>(java.util.List.of(todo, deadline, event)));

        Storage storage = createStorage();
        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("read\\notes\tcarefully\nsoon", loadedTasks.get(0).getDescription());
        assertEquals("[T][ ] read\\notes\tcarefully\nsoon", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Feb 29 2024 18:00)", loadedTasks.get(1).toString());
        assertEquals("[E][X] team meeting (from: Mar 01 2024 09:00 to: Mar 01 2024 10:30)",
                loadedTasks.get(2).toString());
    }

    @Test
    void save_taskTypeAndClassDisagree_assertionErrorThrown() {
        TaskList tasks = new TaskList(new ArrayList<>(java.util.List.of(new IncorrectEventTask())));

        assertThrows(AssertionError.class, () -> createStorage().save(tasks));
    }

    @Test
    void load_invalidTaskFormat_exceptionWithStorageErrorThrown() throws IOException {
        writeSavedData("T\t0");

        assertStorageError();
    }

    @Test
    void load_invalidTaskStatus_exceptionWithStorageErrorThrown() throws IOException {
        writeSavedData("T\t2\tread book");

        assertStorageError();
    }

    @Test
    void load_invalidEscapeSequence_exceptionWithStorageErrorThrown() throws IOException {
        writeSavedData("T\t0\tread\\qbook");

        assertStorageError();
    }

    @Test
    void load_invalidStoredDate_exceptionWithStorageErrorThrown() throws IOException {
        writeSavedData("D\t0\tsubmit report\tnot-a-date");

        assertStorageError();
    }

    @Test
    void load_eventEndNotAfterStart_exceptionWithStorageErrorThrown() throws IOException {
        writeSavedData("E\t0\tmeeting\t2024-03-02T10:00\t2024-03-02T10:00");

        assertStorageError();
    }

    @Test
    void save_temporaryWriteFails_keepsExistingTaskFile() throws IOException {
        String savedData = "T\t0\tkeep existing task";
        writeSavedData(savedData);
        Path filePath = temporaryDirectory.resolve("data").resolve("alfred.txt");
        Storage storage = new Storage(filePath) {
            @Override
            void writeTemporaryFile(Path temporaryFile, ArrayList<String> lines) throws IOException {
                Files.writeString(temporaryFile, "incomplete data", StandardCharsets.UTF_8);
                throw new IOException("Simulated temporary write failure");
            }
        };

        TaskList newTasks = new TaskList(java.util.List.of(new Todo("new task")));
        AlfredException exception = assertThrows(AlfredException.class, () -> storage.save(newTasks));

        assertEquals("Alfred could not save the tasks.", exception.getMessage());
        assertEquals(savedData, Files.readString(filePath, StandardCharsets.UTF_8));
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data").resolve("alfred.txt"));
    }

    private void writeSavedData(String content) throws IOException {
        Path filePath = temporaryDirectory.resolve("data").resolve("alfred.txt");
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content, StandardCharsets.UTF_8);
    }

    private void assertStorageError() {
        AlfredException exception = assertThrows(AlfredException.class, () -> createStorage().load());

        assertEquals("Alfred could not load the saved tasks.", exception.getMessage());
    }

    /** Represents a deliberately malformed task for assertion verification. */
    private static class IncorrectEventTask extends Task {
        IncorrectEventTask() {
            super(TaskType.EVENT, "incorrect event task");
        }
    }
}
