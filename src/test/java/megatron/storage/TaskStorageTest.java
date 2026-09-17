package megatron.storage;

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
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.StorageException;
import megatron.exception.TaskNotFoundException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.task.Todo;

/**
 * Tests persistent task storage without using the production data file.
 */
class TaskStorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyTaskList() throws StorageException {
        TaskStorage storage = new TaskStorage(tempDirectory.resolve("missing.csv").toString());

        assertEquals(0, storage.load().size());
    }

    @Test
    void load_emptyFile_returnsEmptyTaskList() throws IOException, StorageException {
        Path file = tempDirectory.resolve("empty.csv");
        Files.createFile(file);
        TaskStorage storage = new TaskStorage(file.toString());

        assertEquals(0, storage.load().size());
    }

    @Test
    void load_completedEvent_restoresDoneStatus() throws IOException, StorageException, TaskNotFoundException {
        Path file = tempDirectory.resolve("completed-event.csv");
        Files.write(file, List.of(
                "type,done,description,extra",
                "E,1,completed event,2026-08-07T15:00|2026-08-07T16:30"));
        TaskStorage storage = new TaskStorage(file.toString());

        Task loadedTask = storage.load().getTask(1);

        assertInstanceOf(Event.class, loadedTask);
        assertTrue(loadedTask.isDone());
        assertEquals("completed event", loadedTask.getDescription());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesDataAndEscapesCsv()
            throws IOException, StorageException, TaskNotFoundException {
        Path file = tempDirectory.resolve("nested").resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(file.toString());
        Todo todo = new Todo("buy, \"fresh\" milk");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 6, 14, 0));
        Event event = new Event("review", LocalDateTime.of(2026, 8, 7, 15, 0),
                LocalDateTime.of(2026, 8, 7, 16, 30));

        storage.save(new TaskList(List.of(todo, deadline, event)));

        assertEquals(List.of(
                "type,done,description,extra",
                "\"T\",1,\"buy, \"\"fresh\"\" milk\",\"\"",
                "\"D\",0,\"submit report\",\"2026-08-06T14:00\"",
                "\"E\",0,\"review\",\"2026-08-07T15:00|2026-08-07T16:30\""),
                Files.readAllLines(file));

        TaskList loadedTasks = storage.load();
        Task loadedTodo = loadedTasks.getTask(1);
        Task loadedDeadline = loadedTasks.getTask(2);
        Task loadedEvent = loadedTasks.getTask(3);
        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTodo);
        assertEquals("buy, \"fresh\" milk", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        assertInstanceOf(Deadline.class, loadedDeadline);
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals("2026-08-06T14:00", loadedDeadline.getExtra());
        assertFalse(loadedDeadline.isDone());
        assertInstanceOf(Event.class, loadedEvent);
        assertEquals("review", loadedEvent.getDescription());
        assertEquals("2026-08-07T15:00|2026-08-07T16:30", loadedEvent.getExtra());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    void save_emptyTaskList_writesHeaderAndLoadsAsEmpty() throws IOException, StorageException {
        Path file = tempDirectory.resolve("empty-tasks.csv");
        TaskStorage storage = new TaskStorage(file.toString());

        storage.save(new TaskList());

        assertEquals(List.of("type,done,description,extra"), Files.readAllLines(file));
        assertEquals(0, storage.load().size());
    }

    @Test
    void load_malformedRows_skipsInvalidRowsAndLoadsValidRows()
            throws IOException, StorageException, TaskNotFoundException {
        Path file = tempDirectory.resolve("tasks.csv");
        Files.write(file, List.of(
                "type,done,description,extra",
                "T,0,valid todo,",
                "D,1,valid deadline,2026-08-06T14:00",
                "E,0,valid event,2026-08-07T15:00|2026-08-07T16:30",
                "invalid,row",
                "X,0,unknown type,",
                "T,2,invalid status,",
                "T,0,,",
                "T,0,unexpected extra,value",
                "D,0,invalid deadline,not-a-date",
                "D,0,missing deadline,",
                "E,0,missing end,2026-08-07T15:00",
                "E,0,invalid start,invalid|2026-08-07T16:30",
                "E,0,end before start,2026-08-07T16:30|2026-08-07T15:00",
                "E,0,end equal to start,2026-08-07T15:00|2026-08-07T15:00"));
        TaskStorage storage = new TaskStorage(file.toString());

        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("valid todo", loadedTasks.getTask(1).getDescription());
        assertEquals("valid deadline", loadedTasks.getTask(2).getDescription());
        assertTrue(loadedTasks.getTask(2).isDone());
        assertEquals("valid event", loadedTasks.getTask(3).getDescription());
    }

    @Test
    void load_malformedCsvQuotes_skipsMalformedRowsAndLoadsValidRows()
            throws IOException, StorageException, TaskNotFoundException {
        Path file = tempDirectory.resolve("tasks.csv");
        Files.write(file, List.of(
                "type,done,description,extra",
                "T,0,valid todo,",
                "\"T,0,unclosed quote,",
                "T\"0,misplaced quote,",
                "\"T\"trailing,0,characters after quote,"));
        TaskStorage storage = new TaskStorage(file.toString());

        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("valid todo", loadedTasks.getTask(1).getDescription());
    }

    @Test
    void load_directoryPath_throwsStorageException() throws IOException {
        Path directory = Files.createDirectory(tempDirectory.resolve("data"));
        TaskStorage storage = new TaskStorage(directory.toString());

        assertThrows(StorageException.class, storage::load);
    }

    @Test
    void save_directoryPath_throwsStorageException() throws IOException {
        Path directory = Files.createDirectory(tempDirectory.resolve("data"));
        TaskStorage storage = new TaskStorage(directory.toString());

        StorageException exception = assertThrows(
                StorageException.class, () -> storage.save(new TaskList()));

        assertEquals("Could not save tasks. Check that the data file is writable.", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void save_serializationFailure_preservesExistingFileAndRemovesTemporaryFile() throws IOException {
        Path file = tempDirectory.resolve("tasks.csv");
        Files.writeString(file, "existing task data");
        Task failingTask = new Task("unwritable task") {
            @Override
            public String getDescription() {
                throw new IllegalStateException("simulated serialization failure");
            }
        };
        TaskStorage storage = new TaskStorage(file.toString());

        StorageException exception = assertThrows(
                StorageException.class, () -> storage.save(new TaskList(List.of(failingTask))));

        assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertEquals("existing task data", Files.readString(file));
        try (Stream<Path> storedFiles = Files.list(tempDirectory)) {
            assertEquals(List.of(file), storedFiles.toList());
        }
    }
}
