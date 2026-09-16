package eve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import eve.task.Deadline;
import eve.task.Event;
import eve.task.Task;
import eve.task.ToDo;

/** Tests {@link Storage}'s load/save round trip and how it handles a missing or corrupted file. */
public class StorageTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("data").resolve("eve.txt").toString());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws EveException {
        assertEquals(List.of(), newStorage().load());
    }

    @Test
    public void saveThenLoad_toDo_roundTripsDescriptionAndDoneStatus() throws EveException {
        Storage storage = newStorage();
        ToDo task = new ToDo("read book");
        task.markAsDone();

        storage.save(List.of(task));
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_deadline_roundTripsDueDate() throws EveException {
        Storage storage = newStorage();
        Deadline task = new Deadline("return book", LocalDate.of(2019, 6, 6));

        storage.save(List.of(task));
        List<Task> loaded = storage.load();

        assertEquals("[D][ ] return book (by: Jun 6 2019)", loaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_event_roundTripsStartAndEndDates() throws EveException {
        Storage storage = newStorage();
        Event task = new Event("trip", LocalDate.of(2019, 10, 4), LocalDate.of(2019, 10, 11));

        storage.save(List.of(task));
        List<Task> loaded = storage.load();

        assertEquals("[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)", loaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_multipleTasks_preservesOrder() throws EveException {
        Storage storage = newStorage();
        storage.save(List.of(new ToDo("first"), new ToDo("second"), new ToDo("third")));

        List<Task> loaded = storage.load();

        assertEquals(List.of("[T][ ] first", "[T][ ] second", "[T][ ] third"),
                loaded.stream().map(Task::toString).toList());
    }

    @Test
    public void save_createsParentDirectoryIfMissing() throws EveException {
        Storage storage = newStorage();

        storage.save(List.of(new ToDo("read book")));

        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_lineWithBadStatusField_skipsItButKeepsOthers() throws IOException, EveException {
        Path file = tempDir.resolve("eve.txt");
        Files.createDirectories(tempDir);
        Files.write(file, List.of("T | X | bad status", "T | 0 | good todo"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(List.of("[T][ ] good todo"), loaded.stream().map(Task::toString).toList());
    }

    @Test
    public void load_lineWithUnknownTypeLetter_skipsItButKeepsOthers() throws IOException, EveException {
        Path file = tempDir.resolve("eve.txt");
        Files.createDirectories(tempDir);
        Files.write(file, List.of("X | 0 | mystery type", "T | 0 | good todo"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(List.of("[T][ ] good todo"), loaded.stream().map(Task::toString).toList());
    }

    @Test
    public void load_lineWithTooFewFields_skipsItButKeepsOthers() throws IOException, EveException {
        Path file = tempDir.resolve("eve.txt");
        Files.createDirectories(tempDir);
        Files.write(file, List.of("NOT A VALID LINE", "T | 0 | good todo"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(List.of("[T][ ] good todo"), loaded.stream().map(Task::toString).toList());
    }

    @Test
    public void load_deadlineWithUnparseableDate_skipsItButKeepsOthers() throws IOException, EveException {
        Path file = tempDir.resolve("eve.txt");
        Files.createDirectories(tempDir);
        Files.write(file, List.of("D | 0 | bad date | June 6th", "T | 0 | good todo"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(List.of("[T][ ] good todo"), loaded.stream().map(Task::toString).toList());
    }

    @Test
    public void load_fileIsActuallyADirectory_throwsEveException() throws IOException {
        // Files.readAllLines() on a directory fails with an IOException, which load() should
        // convert to a friendly EveException rather than letting it propagate raw.
        Path directoryAsFile = tempDir.resolve("eve.txt");
        Files.createDirectory(directoryAsFile);
        Storage storage = new Storage(directoryAsFile.toString());

        EveException exception = assertThrows(EveException.class, storage::load);
        assertFalse(exception.getMessage().isEmpty());
    }
}
