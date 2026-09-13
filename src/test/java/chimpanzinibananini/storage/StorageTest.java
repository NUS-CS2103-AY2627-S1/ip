package chimpanzinibananini.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chimpanzinibananini.task.Deadline;
import chimpanzinibananini.task.Event;
import chimpanzinibananini.task.TaskList;
import chimpanzinibananini.task.Todo;

/** Tests saving and loading task data on disk. */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTasks_blankRequiredFields_throwsIOExceptionWithLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        for (String line : List.of("T | 0 |   ", "D | 0 | return book | ",
                "E | 0 | meeting |   | 4pm", "E | 0 | meeting | 2pm | ")) {
            Files.write(dataFile, List.of("T | 0 | valid", line));

            IOException exception = assertThrows(IOException.class, () -> new Storage(dataFile).loadTasks());

            assertEquals("invalid data on line 2: task fields cannot be blank", exception.getMessage());
        }
    }

    @Test
    void saveAndLoadTasks_eventWithEscapedTimes_preservesFieldsAndStatus() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Event event = new Event("meeting", "Monday | Tuesday", "after \\ review\r\nends");
        event.markAsDone();

        storage.saveTasks(new TaskList(List.of(event)));
        TaskList loaded = storage.loadTasks();

        assertEquals(List.of("E | 1 | meeting | Monday \\| Tuesday | after \\\\ review\\r\\nends"),
                Files.readAllLines(dataFile));
        assertEquals(1, loaded.size());
        assertEquals("[E][X] meeting (from: Monday | Tuesday to: after \\ review\r\nends)",
                loaded.get(0).toString());
    }

    @Test
    void saveTasks_filenameWithoutParent_roundTripsData() throws IOException {
        Path dataFile = Files.createTempFile(Path.of(""), "storage-test-", ".txt").getFileName();
        try {
            Storage storage = new Storage(dataFile);
            storage.saveTasks(new TaskList(List.of(new Todo("read book"))));

            assertEquals("[T][ ] read book", storage.loadTasks().get(0).toString());
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }

    @Test
    void loadTasks_invalidDeadlineDate_throwsIOExceptionWithLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        for (String date : List.of("2023-02-29", "2/12/2019 2400", "tomorrow")) {
            Files.write(dataFile, List.of("T | 0 | valid", "D | 0 | return book | " + date));

            IOException exception = assertThrows(IOException.class, () -> new Storage(dataFile).loadTasks());

            assertEquals("invalid data on line 2: "
                    + "Use yyyy-MM-dd or d/M/yyyy HHmm (for example, 2/12/2019 1800)", exception.getMessage());
        }
    }

    @Test
    void loadTasks_missingFile_returnsEmptyTaskList() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("tasks.txt");

        TaskList loaded = new Storage(dataFile).loadTasks();

        assertEquals(0, loaded.size());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void saveTasks_parentDirectoryMissing_createsDirectoryAndFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);

        storage.saveTasks(new TaskList(List.of(new Todo("read book"))));

        assertTrue(Files.isRegularFile(dataFile));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(dataFile));
    }

    @Test
    void saveAndLoadTasks_allTaskTypesAndStatuses_roundTripsData() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | write \\ review\nnotes");
        Deadline deadline = new Deadline(
                "return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        Event event = new Event("project meeting", "2pm", "4pm");

        storage.saveTasks(new TaskList(List.of(todo, deadline, event)));
        TaskList loaded = storage.loadTasks();

        assertEquals(3, loaded.size());
        assertEquals(todo.toString(), loaded.get(0).toString());
        assertEquals(deadline.toString(), loaded.get(1).toString());
        assertEquals(event.toString(), loaded.get(2).toString());
        assertEquals("X", loaded.get(1).getStatusIcon());
    }

    @Test
    void saveTasks_existingFile_replacesOldContents() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(new TaskList(List.of(new Todo("old task"))));

        storage.saveTasks(new TaskList(List.of(new Todo("new task"))));

        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(dataFile));
    }

    @Test
    void loadTasks_blankLines_ignoresBlankLines() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("", "T | 0 | read book", "   "));

        TaskList loaded = new Storage(dataFile).loadTasks();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
    }

    @Test
    void loadTasks_pathIsDirectory_throwsIOException() {
        assertThrows(IOException.class, () -> new Storage(temporaryDirectory).loadTasks());
    }

    @Test
    void loadTasks_unknownTaskType_throwsIOExceptionWithLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | valid", "Z | 0 | invalid"));

        IOException exception = assertThrows(
                IOException.class, () -> new Storage(dataFile).loadTasks());

        assertTrue(exception.getMessage().contains("line 2"));
    }

    @Test
    void loadTasks_invalidStatus_throwsIOException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 2 | read book");

        assertThrows(IOException.class, () -> new Storage(dataFile).loadTasks());
    }

    @Test
    void loadTasks_wrongFieldCount_throwsIOException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "D | 0 | return book");

        assertThrows(IOException.class, () -> new Storage(dataFile).loadTasks());
    }

    @Test
    void loadTasks_unfinishedEscapeSequence_throwsIOException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | read book\\");

        assertThrows(IOException.class, () -> new Storage(dataFile).loadTasks());
    }
}
