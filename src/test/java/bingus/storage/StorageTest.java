package bingus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Task;
import bingus.task.Todo;

/**
 * Tests saving and loading task data through {@link Storage}.
 */
class StorageTest {
    /** Directory created by JUnit for this test and removed after the test run. */
    @TempDir
    Path temporaryDirectory;

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifies that persistence retains each task's type, content, dates, and completion status.
     */
    @Test
    void saveTasks_thenLoadTasks_preservesTaskData() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        Todo todo = new Todo("buy milk | eggs");
        todo.mark();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 30, 23, 59));
        Event event = new Event("camp", LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));

        storage.saveTasks(List.of(todo, deadline, event));

        List<Task> loadedTasks = storage.loadTasks();
        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));

        assertEquals("buy milk | eggs", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        assertEquals(LocalDateTime.of(2026, 8, 30, 23, 59), loadedDeadline.getBy());
        assertEquals(LocalDateTime.of(2026, 8, 20, 9, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 22, 18, 0), loadedEvent.getTo());
    }

    /**
     * Verifies that loading a file that has not been created returns an empty task list.
     */
    @Test
    void loadTasks_missingSaveFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/tasks.txt").toString());

        assertEquals(List.of(), storage.loadTasks());
    }

    /**
     * Verifies that saving an empty task list persists an empty list.
     */
    @Test
    void saveTasks_emptyListThenLoadTasks_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());

        storage.saveTasks(List.of());

        assertEquals(List.of(), storage.loadTasks());
    }

    /**
     * Verifies that a later save replaces the previously saved complete task list.
     */
    @Test
    void saveTasks_secondSave_replacesPreviouslySavedTasks() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());

        storage.saveTasks(List.of(new Todo("first task")));
        storage.saveTasks(List.of(new Todo("replacement task")));

        List<Task> loadedTasks = storage.loadTasks();
        assertEquals(1, loadedTasks.size());
        assertEquals("replacement task", loadedTasks.get(0).getDescription());
    }

    /**
     * Verifies that storage supports a save-file path without a parent directory.
     *
     * @throws IOException if the test save file cannot be deleted.
     */
    @Test
    void saveTasks_parentlessFilePath_savesAndLoadsTask() throws IOException {
        Path saveFile = Path.of("storage-" + temporaryDirectory.getFileName() + ".txt");
        Storage storage = new Storage(saveFile.toString());

        try {
            storage.saveTasks(List.of(new Todo("read book")));

            assertEquals("read book", storage.loadTasks().get(0).getDescription());
        } finally {
            Files.deleteIfExists(saveFile);
        }
    }

    /**
     * Verifies that malformed records are rejected with a user-facing loading error.
     *
     * @throws IOException if the test save file cannot be written.
     */
    @Test
    void loadTasks_malformedRecords_throwsFriendlyException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data/tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        String eventWithReversedDateRange = String.join(
                "|",
                "E",
                "0",
                encode("camp"),
                encode("2026-09-21T18:00"),
                encode("2026-09-20T09:00"));
        List<String> malformedRecords = List.of(
                "X|0|" + encode("read book"),
                "T|2|" + encode("read book"),
                "D|0|" + encode("submit report"),
                "T|0|not-valid-base64!",
                "D|0|" + encode("submit report") + "|" + encode("not-a-time"),
                eventWithReversedDateRange);

        Files.createDirectories(saveFile.getParent());
        for (String malformedRecord : malformedRecords) {
            Files.writeString(saveFile, malformedRecord);

            BingusException exception = assertThrows(BingusException.class, storage::loadTasks);

            assertEquals("I could not load your saved tasks. I have started with an empty list.",
                    exception.getMessage());
        }
    }
}
