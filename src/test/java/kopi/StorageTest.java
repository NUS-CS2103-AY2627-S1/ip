package kopi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void load_missingFile_emptyListReturned() throws KopiException {
        Storage storage = new Storage(tempDirectory.resolve("data").resolve("kopi.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_validTasks_tasksPreserved() throws KopiException {
        Storage storage = new Storage(tempDirectory.resolve("nested").resolve("kopi.txt"));
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.parse("2026-09-06"));
        deadline.markAsDone();
        Event event = new Event(
                "project meeting",
                LocalDate.parse("2026-09-07"),
                LocalDate.parse("2026-09-08"));

        storage.save(List.of(todo, deadline, event));
        List<String> storedTasks = storage.load().stream().map(Task::toDataString).toList();

        assertEquals(
                List.of(
                        "T | 0 | read book",
                        "D | 1 | return book | 2026-09-06",
                        "E | 0 | project meeting | 2026-09-07 | 2026-09-08"),
                storedTasks);
    }

    @Test
    void load_corruptedFile_exceptionThrown() throws IOException {
        Path filePath = tempDirectory.resolve("kopi.txt");
        Files.writeString(filePath, "D | 0 | return book | someday");
        Storage storage = new Storage(filePath);

        KopiException exception = assertThrows(KopiException.class, storage::load);

        assertEquals("The data file contains an invalid date.", exception.getMessage());
    }

    @Test
    void load_duplicateTasks_exceptionThrown() throws IOException {
        Path filePath = tempDirectory.resolve("kopi.txt");
        Files.writeString(filePath, "T | 0 | read book\nT | 1 | READ BOOK\n");
        Storage storage = new Storage(filePath);

        KopiException exception = assertThrows(KopiException.class, storage::load);

        assertEquals("The data file contains duplicate tasks.", exception.getMessage());
    }

    @Test
    void load_invalidEventRange_exceptionThrown() throws IOException {
        Path filePath = tempDirectory.resolve("kopi.txt");
        Files.writeString(filePath, "E | 0 | meeting | 2026-09-09 | 2026-09-08");
        Storage storage = new Storage(filePath);

        KopiException exception = assertThrows(KopiException.class, storage::load);

        assertEquals("The data file contains an invalid event range.", exception.getMessage());
    }

    @Test
    void load_blankDescription_exceptionThrown() throws IOException {
        Path filePath = tempDirectory.resolve("kopi.txt");
        Files.writeString(filePath, "T | 0 | ");
        Storage storage = new Storage(filePath);

        KopiException exception = assertThrows(KopiException.class, storage::load);

        assertEquals("The data file contains an invalid todo.", exception.getMessage());
    }
}
