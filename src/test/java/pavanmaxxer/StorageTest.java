package pavanmaxxer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests task persistence and malformed storage handling.
 */
public class StorageTest {
    @TempDir
    Path testDirectory;

    @Test
    void save_multipleTasks_writesLinesInOriginalOrder() throws Exception {
        Path dataFile = testDirectory.resolve("pavanmaxxer.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline(
                "return book", LocalDate.parse("2026-08-31")));
        tasks.mark(0);

        storage.save(tasks);

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-08-31"),
                Files.readAllLines(dataFile));
    }

    @Test
    void saveAndLoad_multipleTaskTypes_preservesState() throws Exception {
        Path dataFile = testDirectory.resolve("data/pavanmaxxer.txt");
        Storage storage = new Storage(dataFile);
        TaskList original = new TaskList();
        original.add(new Todo("read book"));
        original.add(new Deadline(
                "return book", LocalDate.parse("2026-08-31")));
        original.add(new Event("meeting", "2pm", "4pm"));
        original.mark(0);

        storage.save(original);
        TaskList loaded = new TaskList(storage.load());

        assertEquals(original.asList().stream().map(Task::toString).toList(),
                loaded.asList().stream().map(Task::toString).toList());
    }

    @Test
    void load_missingFile_createsFileAndReturnsEmptyList() throws Exception {
        Path dataFile = testDirectory.resolve("data/pavanmaxxer.txt");
        Storage storage = new Storage(dataFile);

        assertTrue(storage.load().isEmpty());
        assertTrue(Files.exists(dataFile));
    }

    @Test
    void load_malformedLine_throwsApplicationException() throws Exception {
        Path dataFile = testDirectory.resolve("pavanmaxxer.txt");
        Files.writeString(dataFile, "T | 0 | \n");

        assertThrows(PavanmaxxerException.class, () ->
                new Storage(dataFile).load());
    }

    @Test
    void load_unsupportedTaskType_throwsApplicationException() throws Exception {
        Path dataFile = testDirectory.resolve("pavanmaxxer.txt");
        Files.writeString(dataFile, "X | 0 | read book\n");

        assertThrows(PavanmaxxerException.class, () ->
                new Storage(dataFile).load());
    }

    @Test
    void load_invalidDeadlineDate_throwsApplicationException() throws Exception {
        Path dataFile = testDirectory.resolve("pavanmaxxer.txt");
        Files.writeString(dataFile, "D | 0 | return book | not-a-date\n");

        assertThrows(PavanmaxxerException.class, () ->
                new Storage(dataFile).load());
    }

    @Test
    void load_eventWithEmptyEnd_throwsApplicationException() throws Exception {
        Path dataFile = testDirectory.resolve("pavanmaxxer.txt");
        Files.writeString(dataFile, "E | 0 | class | 2pm | \n");

        assertThrows(PavanmaxxerException.class, () ->
                new Storage(dataFile).load());
    }

    @Test
    void load_invalidCompletionState_throwsApplicationException()
            throws Exception {
        Path dataFile = testDirectory.resolve("pavanmaxxer.txt");
        Files.writeString(dataFile, "T | yes | read book\n");

        assertThrows(PavanmaxxerException.class, () ->
                new Storage(dataFile).load());
    }

    @Test
    void load_todoWithExtraField_throwsApplicationException() throws Exception {
        Path dataFile = testDirectory.resolve("pavanmaxxer.txt");
        Files.writeString(dataFile, "T | 0 | read | book\n");

        assertThrows(PavanmaxxerException.class, () ->
                new Storage(dataFile).load());
    }
}
