package esther.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import esther.exception.EstherException;
import esther.task.Deadline;
import esther.task.Event;
import esther.task.TaskList;
import esther.task.Todo;

/**
 * Tests task persistence using isolated temporary files.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList()
            throws IOException, EstherException {
        Path dataFile = tempDirectory.resolve("missing").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);

        TaskList loadedTasks = storage.load();

        assertEquals(0, loadedTasks.size());
    }

    @Test
    public void saveAndLoad_variedTasks_preservesTaskData()
            throws IOException, EstherException {
        Path dataFile = tempDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();

        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline(
                "return book", LocalDate.of(2026, 8, 30)));
        tasks.add(new Event("project meeting", "2pm", "4pm"));

        storage.save(tasks);

        List<String> expectedTaskData = List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-08-30",
                "E | 0 | project meeting | 2pm | 4pm");
        assertTrue(Files.exists(dataFile));
        assertEquals(
                expectedTaskData,
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));

        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(
                expectedTaskData.get(0),
                loadedTasks.get(0).toDataString());
        assertEquals(
                expectedTaskData.get(1),
                loadedTasks.get(1).toDataString());
        assertEquals(
                expectedTaskData.get(2),
                loadedTasks.get(2).toDataString());
    }

    @Test
    public void saveAndLoad_descriptionContainingDelimiter_preservesDescription()
            throws IOException, EstherException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read | review book"));

        storage.save(tasks);

        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals(
                "T | 0 | read \\| review book",
                loadedTasks.get(0).toDataString());
    }

    @Test
    public void load_fileWithBlankLines_ignoresBlankLines()
            throws IOException, EstherException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                System.lineSeparator()
                        + "T | 0 | read book"
                        + System.lineSeparator()
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals(
                "T | 0 | read book",
                loadedTasks.get(0).toDataString());
    }

    @Test
    public void load_invalidDeadlineDate_throwsException()
            throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "D | 0 | return book | 30-08-2026",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        assertThrows(EstherException.class, storage::load);
    }

    @Test
    public void load_invalidSecondLine_reportsLineNumber() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.write(
                dataFile,
                List.of(
                        "T | 0 | read book",
                        "D | 0 | return book | 30-08-2026"),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        EstherException exception = assertThrows(
                EstherException.class, storage::load);

        assertTrue(exception.getMessage().contains("line 2"));
    }
}
