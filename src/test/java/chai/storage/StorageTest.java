package chai.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chai.ChaiException;
import chai.task.Deadline;
import chai.task.Event;
import chai.task.Task;
import chai.task.Todo;

/** Tests task persistence and invalid saved data handling. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() throws ChaiException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing").resolve("chai.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesTaskData() throws ChaiException, IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("chai.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("buy tea | milk \\ sugar");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 10, 1));
        Event event = new Event(
                "conference", LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 3));
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()),
                loadedTasks.stream().map(Task::toString).toList());
        assertTrue(loadedTasks.get(1).isDone());
        assertTrue(Files.readString(dataFile).contains("\\|"));
    }

    @Test
    public void load_invalidStatus_reportsLineAndCause() throws IOException {
        ChaiException exception = loadInvalidLine("T | 2 | read book");

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("completion status"));
    }

    @Test
    public void load_blankDescription_reportsLineAndCause() throws IOException {
        ChaiException exception = loadInvalidLine("T | 0 |   ");

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("description cannot be blank"));
    }

    @Test
    public void load_invalidDate_reportsLineAndCause() throws IOException {
        ChaiException exception = loadInvalidLine("D | 0 | submit report | 2026-02-30");

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    public void load_directoryInsteadOfFile_exceptionThrown() {
        Storage storage = new Storage(temporaryDirectory);

        assertThrows(ChaiException.class, storage::load);
    }

    @Test
    public void save_parentPathIsFile_exceptionThrown() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "blocking file");
        Storage storage = new Storage(parentFile.resolve("chai.txt"));

        assertThrows(ChaiException.class, () -> storage.save(List.of(new Todo("read book"))));
    }

    /** Writes one invalid storage line and returns the resulting load exception. */
    private ChaiException loadInvalidLine(String line) throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid").resolve("chai.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, line);
        Storage storage = new Storage(dataFile);

        return assertThrows(ChaiException.class, storage::load);
    }
}
