package sozius.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sozius.exception.SoziusException;
import sozius.task.DeadlineTask;
import sozius.task.DueDate;
import sozius.task.EventTask;
import sozius.task.Task;
import sozius.task.TodoTask;
import sozius.tasklist.TaskList;

public class StorageTest {

    @TempDir
    Path tempDir;

    private Path fileInTempDir(String name) {
        return tempDir.resolve(name);
    }

    @Test
    public void load_missingFile_createsFileAndReturnsEmpty() throws SoziusException {
        Path file = fileInTempDir("tasks.txt");
        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();
        assertEquals(0, loaded.size());
        assertEquals(0, storage.getSkippedLineCount());
        assertTrue(Files.exists(file));
    }

    @Test
    public void saveThenLoad_roundTrip_preservesTasks() throws SoziusException {
        Path file = fileInTempDir("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList tasks = new TaskList();
        TodoTask todo = new TodoTask("read book");
        tasks.add(todo);
        DeadlineTask deadline = new DeadlineTask("return book", DueDate.parse("2024-12-31 1800"));
        deadline.setDone(true);
        tasks.add(deadline);
        tasks.add(new EventTask("meeting", DueDate.parse("2024-01-01 1000"),
                DueDate.parse("2024-01-01 1200")));

        storage.save(tasks);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toFileString());
        assertEquals("D | 1 | return book | 2024-12-31 1800", loaded.get(1).toFileString());
        assertEquals("E | 0 | meeting | 2024-01-01 1000/2024-01-01 1200",
                loaded.get(2).toFileString());
        assertTrue(loaded.get(1).isDone());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void save_emptyList_createsEmptyFile() throws SoziusException, IOException {
        Path file = fileInTempDir("tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.save(new TaskList());
        assertTrue(Files.exists(file));
        assertEquals(0, Files.readAllLines(file).size());
    }

    @Test
    public void load_corruptedLines_skipsThemAndCounts() throws IOException, SoziusException {
        Path file = fileInTempDir("tasks.txt");
        Files.write(file, List.of(
                "T | 0 | read book",
                "X | 0 | unknown type",
                "T | 3 | invalid done flag",
                "T | 1 |",
                "D | 0 | return book | 2024-12-31",
                "E | 1 | meeting | 2024-01-01 1000/2024-01-01 1200",
                "",
                "D | 0 | bad date | not-a-date"));

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("return book", loaded.get(1).getDescription());
        assertEquals("meeting", loaded.get(2).getDescription());
        assertEquals(4, storage.getSkippedLineCount());
    }

    @Test
    public void load_eventWithBadTimeFormat_skipsLine() throws IOException, SoziusException {
        Path file = fileInTempDir("tasks.txt");
        Files.write(file, List.of(
                "E | 0 | meeting | 2024-01-01 1000", // missing the /end part
                "E | 0 | meeting | /2024-01-01 1200")); // blank start

        Storage storage = new Storage(file.toString());
        assertEquals(0, storage.load().size());
        assertEquals(2, storage.getSkippedLineCount());
    }

    @Test
    public void load_blankLines_ignoredWithoutCounting() throws IOException, SoziusException {
        Path file = fileInTempDir("tasks.txt");
        Files.write(file, List.of("", "   ", "T | 0 | read book", ""));

        Storage storage = new Storage(file.toString());
        assertEquals(1, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void save_createsMissingParentDirectories() throws SoziusException {
        Path file = tempDir.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));
        storage.save(tasks);
        assertTrue(Files.exists(file));
    }
}
