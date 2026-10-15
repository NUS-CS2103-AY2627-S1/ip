package alexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence of tasks performed by {@link Storage}. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    private Path dataFile;
    private Storage storage;

    @BeforeEach
    void setUp() {
        dataFile = temporaryDirectory.resolve("data").resolve("alexa.txt");
        storage = new Storage(dataFile);
    }

    @Test
    void load_dataFileMissing_returnsEmptyList() throws IOException {
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void save_nestedDataDirectoryMissing_createsDirectoryAndWritesTasks() throws IOException {
        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(dataFile));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(dataFile));
    }

    @Test
    void saveThenLoad_mixedTasksAndDoneStatus_restoresEveryTask() throws IOException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event event = new Event("project meeting", LocalDate.of(2019, 10, 16), LocalDate.of(2019, 10, 17));
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] return book (by: Oct 15 2019)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)",
                loadedTasks.get(2).toString());
    }

    @Test
    void load_unknownTaskType_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "X | 0 | unsupported task");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Unknown task type: X", exception.getMessage());
    }

    @Test
    void load_invalidTaskStatus_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "T | complete | read book");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task status: T | complete | read book", exception.getMessage());
    }

    @Test
    void load_invalidDate_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "D | 0 | return book | tomorrow");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task date: D | 0 | return book | tomorrow", exception.getMessage());
    }

    @Test
    void load_missingRequiredField_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "E | 0 | project meeting | 2019-10-16");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task data: E | 0 | project meeting | 2019-10-16", exception.getMessage());
    }

    @Test
    void load_existingDuplicateTasks_preservesEveryStoredTask() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.write(dataFile, List.of("T | 0 | read book", "T | 0 | read book"));

        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[T][ ] read book", loadedTasks.get(1).toString());
    }

    @Test
    void load_eventWithEndDateNotLaterThanStartDate_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "E | 0 | meeting | 2026-10-16 | 2026-10-15");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid event date range: E | 0 | meeting | 2026-10-16 | 2026-10-15",
                exception.getMessage());
    }

    @Test
    void load_todoWithUnexpectedField_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "T | 0 | read book | unexpected");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task data: T | 0 | read book | unexpected", exception.getMessage());
    }

    @Test
    void load_deadlineWithMissingDate_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "D | 0 | return book");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task data: D | 0 | return book", exception.getMessage());
    }

    @Test
    void load_eventWithEqualDates_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "E | 0 | meeting | 2026-10-15 | 2026-10-15");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid event date range: E | 0 | meeting | 2026-10-15 | 2026-10-15",
                exception.getMessage());
    }

    @Test
    void load_malformedLine_throwsIOException() throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "T | 0");

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task data: T | 0", exception.getMessage());
    }
}
