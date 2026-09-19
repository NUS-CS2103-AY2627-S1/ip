package ollie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void saveAndLoad_multipleTaskTypes_preservesTaskData() throws OllieException {
        Path filePath = tempDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read book");
        todo.mark();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Event event = new Event("meeting", LocalDate.of(2019, 12, 3),
                LocalDate.of(2019, 12, 5));

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), loadedDeadline.getDueDate());
        assertFalse(loadedDeadline.isDone());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("meeting", loadedEvent.getDescription());
        assertEquals(LocalDate.of(2019, 12, 3), loadedEvent.getStartDate());
        assertEquals(LocalDate.of(2019, 12, 5), loadedEvent.getEndDate());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws OllieException {
        Storage storage = new Storage(tempDirectory.resolve("missing").resolve("tasks.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_invalidTaskData_throwsOllieException() throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "D | 0 | return book | invalid-date");
        Storage storage = new Storage(filePath);

        assertThrows(OllieException.class, storage::load);
    }

    @Test
    public void load_invalidEventRange_throwsOllieException() throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "E | 0 | meeting | 2019-12-05 | 2019-12-03");
        Storage storage = new Storage(filePath);

        assertThrows(OllieException.class, storage::load);
    }

    @Test
    public void load_duplicateTasks_throwsOllieException() throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "T | 1 | Read  Book"));
        Storage storage = new Storage(filePath);

        OllieException exception = assertThrows(OllieException.class, storage::load);

        assertTrue(exception.getMessage().contains("duplicates"));
    }

    @Test
    public void load_filePathIsDirectory_throwsOllieException() throws IOException {
        Path directoryPath = Files.createDirectory(tempDirectory.resolve("tasks.txt"));
        Storage storage = new Storage(directoryPath);

        assertThrows(OllieException.class, storage::load);
    }

    @Test
    public void save_unsupportedTaskType_throwsOllieException() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt"));

        assertThrows(OllieException.class, () -> storage.save(List.of(new Task("generic task"))));
    }

    @Test
    public void save_invalidOrDuplicateTasks_throwsOllieException() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt"));

        assertThrows(OllieException.class, () -> storage.save(List.of(new Todo("read | book"))));
        assertThrows(OllieException.class, () -> storage.save(
                List.of(new Todo("read book"), new Todo("Read  Book"))));
    }

    @Test
    public void save_parentPathIsFile_throwsOllieException() throws IOException {
        Path parentFile = tempDirectory.resolve("not-a-folder");
        Files.writeString(parentFile, "content");
        Storage storage = new Storage(parentFile.resolve("tasks.txt"));

        assertThrows(OllieException.class, () -> storage.save(List.of(new Todo("read book"))));
    }
}
