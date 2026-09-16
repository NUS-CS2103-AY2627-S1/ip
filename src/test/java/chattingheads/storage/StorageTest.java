package chattingheads.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chattingheads.exception.InvalidInputException;
import chattingheads.exception.StorageException;
import chattingheads.task.Deadline;
import chattingheads.task.Event;
import chattingheads.task.Task;
import chattingheads.task.TaskList;
import chattingheads.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() throws StorageException {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_emptyTaskList_createsEmptyFile() throws StorageException, IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        storage.save(new TaskList());

        assertTrue(Files.exists(file));
        assertTrue(Files.readAllLines(file).isEmpty());
    }

    @Test
    public void saveAndLoad_multipleTaskTypes_preservesTasks()
            throws StorageException, InvalidInputException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book", true);
        Deadline deadline = new Deadline(
                "submit report",
                LocalDateTime.of(2026, 8, 28, 18, 0));
        Event event = new Event(
                "meeting",
                true,
                LocalDateTime.of(2026, 8, 29, 10, 0),
                LocalDateTime.of(2026, 8, 29, 12, 0));
        taskList.add(todo);
        taskList.add(deadline);
        taskList.add(event);

        storage.save(taskList);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals(todo.toCsv(), loaded.get(0).toCsv());
        assertEquals(deadline.toCsv(), loaded.get(1).toCsv());
        assertEquals(event.toCsv(), loaded.get(2).toCsv());
    }

    @Test
    public void saveAndLoad_descriptionContainsComma_preservesDescription()
            throws StorageException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk,bread"));

        storage.save(taskList);
        List<Task> loaded = storage.load();

        assertEquals("buy milk,bread", loaded.getFirst().getDescription());
    }

    @Test
    public void load_corruptedLineAmongValidLines_skipsCorruptedLine()
            throws IOException, StorageException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of(
                "T,first,false",
                "corrupted data",
                "T,second,true"));
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("first", loaded.get(0).getDescription());
        assertEquals("second", loaded.get(1).getDescription());
    }

    @Test
    public void load_invalidBooleanLine_skipsInvalidLine()
            throws IOException, StorageException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of("T,test,yes"));
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_invalidDateTimeLine_skipsInvalidLine()
            throws IOException, StorageException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of("D,test,false,not-a-date"));
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_invalidEventTimeRange_skipsInvalidLine()
            throws IOException, StorageException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of(
                "E,test,false,2026-08-28T20:00,2026-08-28T18:00"));
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_existingFile_overwritesPreviousContents()
            throws IOException, StorageException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of("T,old,false"));
        Storage storage = new Storage(file.toString());
        TaskList taskList = new TaskList();
        taskList.add(new Todo("new"));

        storage.save(taskList);

        assertEquals(List.of("T,new,false"), Files.readAllLines(file));
    }

    @Test
    public void save_parentDirectoryDoesNotExist_throwsStorageException() {
        Path file = tempDir.resolve("missing-directory").resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        assertThrows(StorageException.class, () -> storage.save(new TaskList()));
        assertFalse(Files.exists(file));
    }

    @Test
    public void load_directoryPath_throwsStorageException() {
        Storage storage = new Storage(tempDir.toString());
        assertThrows(StorageException.class, storage::load);
    }

    @Test
    public void taskListConstructor_validStorage_loadsStoredTasks()
            throws IOException, StorageException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of("T,loaded,true"));

        TaskList taskList = new TaskList(new Storage(file.toString()));

        assertEquals(1, taskList.size());
        assertEquals("T,loaded,true", taskList.get(0).toCsv());
    }

}
