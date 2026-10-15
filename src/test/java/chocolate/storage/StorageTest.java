package chocolate.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chocolate.exception.ChocolateException;
import chocolate.task.Deadline;
import chocolate.task.Event;
import chocolate.task.Task;
import chocolate.task.TaskList;
import chocolate.task.Todo;

/**
 * Tests saving and loading task data through {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void saveThenLoad_allTaskTypes_preservesData() throws IOException, ChocolateException {
        Path dataFile = tempDirectory.resolve("nested").resolve("duke.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.add(new Todo("read book"));
        originalTasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        originalTasks.add(new Event("project meeting", "Monday", "Tuesday"));
        originalTasks.mark(1);

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(Files.exists(dataFile));
        assertEquals(3, loadedTasks.size());

        Todo todo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());

        Deadline deadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getBy());
        assertTrue(deadline.isDone());

        Event event = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", event.getDescription());
        assertEquals("Monday", event.getStart());
        assertEquals("Tuesday", event.getEnd());
        assertFalse(event.isDone());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_corruptedRecords_skipsInvalidLines() throws IOException {
        Path dataFile = tempDirectory.resolve("duke.txt");
        Files.writeString(dataFile,
                "T | 0 | valid task\n"
                        + "invalid record\n"
                        + "D | 0 | impossible date | 2019-02-30\n"
                        + "Z | 1 | unknown type\n");
        Storage storage = new Storage(dataFile);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("valid task", loadedTasks.get(0).getDescription());
    }

    @Test
    public void archive_activeTasks_appendsTasksAndClearsActiveList()
            throws IOException, ChocolateException {
        Path dataFile = tempDirectory.resolve("duke.txt");
        Path archiveFile = tempDirectory.resolve("archive.txt");
        Storage storage = new Storage(dataFile, archiveFile);
        TaskList activeTasks = new TaskList();
        activeTasks.add(new Todo("read book"));
        activeTasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        activeTasks.mark(1);

        storage.archive(activeTasks);
        activeTasks.add(new Todo("buy chocolate"));
        storage.archive(activeTasks);

        ArrayList<Task> archivedTasks = storage.loadArchived();
        assertTrue(activeTasks.getAll().isEmpty());
        assertTrue(storage.load().isEmpty());
        assertEquals(3, archivedTasks.size());
        assertEquals("read book", archivedTasks.get(0).getDescription());
        assertTrue(archivedTasks.get(1).isDone());
        assertEquals("buy chocolate", archivedTasks.get(2).getDescription());
    }

    @Test
    public void loadArchived_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("duke.txt"));

        assertTrue(storage.loadArchived().isEmpty());
    }

    @Test
    public void save_emptyList_replacesPreviouslySavedTasks() throws IOException {
        Path dataFile = tempDirectory.resolve("duke.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        storage.save(tasks);

        storage.save(new TaskList());

        assertTrue(Files.exists(dataFile));
        assertTrue(storage.load().isEmpty());
    }
}
