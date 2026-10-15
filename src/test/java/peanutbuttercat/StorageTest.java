package peanutbuttercat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests loading tasks from and saving tasks to storage. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void save_multipleTasks_writesAllTaskRecordsInOrder() throws IOException, StorageException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDateTime.of(2026, 6, 6, 18, 0))));

        storage.save(tasks);

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 0 | return book | 2026-06-06T18:00"), Files.readAllLines(saveFile));
    }

    @Test
    void save_completedTasks_appendsCompletionDates() throws IOException, StorageException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        Todo todo = new Todo("read book");
        todo.markAsDone(LocalDate.of(2026, 9, 9));
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 9, 10, 18, 0));
        deadline.markAsDone(LocalDate.of(2026, 9, 8));
        Event event = new Event("meeting",
                LocalDateTime.of(2026, 9, 11, 10, 0),
                LocalDateTime.of(2026, 9, 11, 11, 0));
        event.markAsDone(LocalDate.of(2026, 9, 7));

        storage.save(new TaskList(List.of(todo, deadline, event)));

        assertEquals(List.of(
                "T | 1 | read book | 2026-09-09",
                "D | 1 | return book | 2026-09-10T18:00 | 2026-09-08",
                "E | 1 | meeting | 2026-09-11T10:00 | 2026-09-11T11:00 | 2026-09-07"),
                Files.readAllLines(saveFile));
    }

    @Test
    void load_newAndLegacyCompletedTasks_restoresAvailableCompletionDates() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Files.createDirectories(saveFile.getParent());
        Files.write(saveFile, List.of(
                "T | 1 | recent task | 2026-09-09",
                "T | 1 | legacy task"));

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertEquals(LocalDate.of(2026, 9, 9), tasks.get(0).getCompletionDate());
        assertTrue(tasks.get(1).isDone());
        assertNull(tasks.get(1).getCompletionDate());
    }

    @Test
    void load_invalidCompletionMetadata_preservesBaseTaskAndWarns() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Files.createDirectories(saveFile.getParent());
        Files.write(saveFile, List.of("T | 1 | completed task | not-a-date"));
        ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
        PrintStream originalError = System.err;
        Storage storage = new Storage(saveFile.toString());
        List<Task> tasks;

        try {
            System.setErr(new PrintStream(errorOutput));
            tasks = storage.load();
        } finally {
            System.setErr(originalError);
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertNull(tasks.get(0).getCompletionDate());
        assertTrue(errorOutput.toString().contains("Ignoring invalid completion metadata at line 1."));
        assertEquals("Heads up: some saved task data could not be read. "
                + "I recovered what I could, so please check your task list.", storage.getLoadWarning());
    }

    @Test
    void load_missingFile_returnsEmptyListWithoutWarning() {
        Path saveFile = temporaryDirectory.resolve("missing").resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());

        List<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
        assertNull(storage.getLoadWarning());
    }

    @Test
    void load_duplicateAndInvalidEventRecords_skipsRecordsAndWarns() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Files.createDirectories(saveFile.getParent());
        Files.write(saveFile, List.of(
                "T | 0 | read book",
                "T | 1 | READ BOOK | 2026-09-09",
                "E | 0 | invalid meeting | 2026-09-11T11:00 | 2026-09-11T10:00"));
        Storage storage = new Storage(saveFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("Heads up: some saved task data could not be read. "
                + "I recovered what I could, so please check your task list.", storage.getLoadWarning());
    }

    @Test
    void save_parentPathIsAFile_throwsUserFriendlyException() throws IOException {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "blocking file");
        Storage storage = new Storage(blockingFile.resolve("tasks.txt").toString());

        StorageException exception = assertThrows(StorageException.class, () ->
                storage.save(new TaskList(List.of(new Todo("read book")))));

        assertEquals("I couldn't save that change. Please check that PeanutButterCat can write "
                + "to its data folder, then try again.", exception.getMessage());
    }
}
