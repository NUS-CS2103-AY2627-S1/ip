package zaru.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zaru.exception.ZaruException;
import zaru.task.Deadline;
import zaru.task.Event;
import zaru.task.Task;
import zaru.task.ToDo;

/** Tests saving and loading tasks from the hard disk. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that all supported task types survive a save/load round trip. */
    @Test
    public void saveAndLoad_allTaskTypes_roundTrip() throws ZaruException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFile);
        List<Task> tasks = List.of(
                new ToDo("read book", true),
                new Deadline("submit report", false, LocalDateTime.of(2026, 8, 19, 14, 30)),
                new Event("project meeting", true,
                        LocalDateTime.of(2026, 8, 20, 10, 0),
                        LocalDateTime.of(2026, 8, 20, 11, 0)));

        storage.save(tasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());

        ToDo loadedToDo = assertInstanceOf(ToDo.class, loadedTasks.get(0));
        assertEquals("read book", loadedToDo.getDescription());
        assertTrue(loadedToDo.isCompleted());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("submit report", loadedDeadline.getDescription());
        assertFalse(loadedDeadline.isCompleted());
        assertEquals(LocalDateTime.of(2026, 8, 19, 14, 30), loadedDeadline.getDueDate());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertTrue(loadedEvent.isCompleted());
        assertEquals(LocalDateTime.of(2026, 8, 20, 10, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 20, 11, 0), loadedEvent.getTo());
    }

    /** Verifies that starting without a save file yields an empty task list. */
    @Test
    public void load_missingFile_returnsEmptyList() throws ZaruException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    /** Verifies that malformed saved task data is rejected. */
    @Test
    public void load_malformedTask_throwsException() throws Exception {
        Path saveFile = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(saveFile, "D | 0 | missing date");
        Storage storage = new Storage(saveFile);

        ZaruException exception = assertThrows(ZaruException.class, storage::load);

        assertEquals("Invalid data on line 1 of the save file: Invalid task data in save file!",
                exception.getMessage());
    }

    /** Verifies that duplicate tasks in a save file are treated as corrupt data. */
    @Test
    public void load_duplicateTask_throwsException() throws Exception {
        Path saveFile = temporaryDirectory.resolve("duplicates.txt");
        Files.writeString(saveFile, "T | 0 | read book\nT | 1 | READ BOOK");
        Storage storage = new Storage(saveFile);

        ZaruException exception = assertThrows(ZaruException.class, storage::load);

        assertEquals("Invalid data on line 2 of the save file: Duplicate task.", exception.getMessage());
    }

    /** Verifies that a directory cannot be mistaken for the save file. */
    @Test
    public void load_directoryPath_throwsException() {
        Storage storage = new Storage(temporaryDirectory);

        ZaruException exception = assertThrows(ZaruException.class, storage::load);

        assertEquals("The save-file path is not a regular file: %s".formatted(temporaryDirectory),
                exception.getMessage());
    }

    /** Verifies that a directory destination produces a controlled write error. */
    @Test
    public void save_directoryPath_throwsException() {
        Storage storage = new Storage(temporaryDirectory);

        ZaruException exception = assertThrows(ZaruException.class, () ->
                storage.save(List.of(new ToDo("read book"))));

        assertEquals("Unable to write the save file: %s".formatted(temporaryDirectory),
                exception.getMessage());
    }
}
