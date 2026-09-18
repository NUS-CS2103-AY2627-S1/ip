package rem.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rem.task.Deadline;
import rem.task.Event;
import rem.task.Task;
import rem.task.Todo;

/**
 * Tests persistence and validation of Rem's task data.
 */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_emptyListReturned() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypesAndStatuses_roundTripPreserved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.setNote("Borrow it from Alice | return Friday");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 29, 18, 0));
        Event event = new Event("conference", LocalDateTime.of(2026, 8, 30, 9, 0),
                LocalDateTime.of(2026, 8, 31, 17, 0));

        storage.saveTasks(List.of(todo, deadline, event));
        assertEquals(List.of(
                "T | 1 | read book | N:Qm9ycm93IGl0IGZyb20gQWxpY2UgfCByZXR1cm4gRnJpZGF5",
                "D | 0 | submit report | 2026-08-29 1800",
                "E | 0 | conference | 2026-08-30 0900 | 2026-08-31 1700"),
                Files.readAllLines(dataFile));
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertEquals("Borrow it from Alice | return Friday", loadedTasks.get(0).getNote());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals(deadline.getBy(), loadedDeadline.getBy());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(event.getFrom(), loadedEvent.getFrom());
        assertEquals(event.getTo(), loadedEvent.getTo());
    }

    @Test
    public void saveTasks_existingFile_previousContentsReplaced() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.saveTasks(List.of(new Todo("old"), new Todo("extra")));

        storage.saveTasks(List.of(new Todo("new")));

        assertEquals(List.of("T | 0 | new"), Files.readAllLines(dataFile));
    }

    @Test
    public void saveTasks_emptyList_existingContentsRemoved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.saveTasks(List.of(new Todo("old")));

        storage.saveTasks(List.of());

        assertTrue(Files.readAllLines(dataFile).isEmpty());
    }

    @Test
    public void loadTasks_blankLines_blankLinesIgnored() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT | 0 | read book\n\n");
        Storage storage = new Storage(dataFile.toString());

        assertEquals(1, storage.loadTasks().size());
    }

    @Test
    public void loadTasks_malformedLines_exceptionIdentifiesLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | valid\nD | maybe | broken\n");
        Storage storage = new Storage(dataFile.toString());

        IOException exception = assertThrows(IOException.class, storage::loadTasks);
        assertEquals("Invalid task data on line 2.", exception.getMessage());
    }

    @Test
    public void loadTasks_unknownTypeOrInvalidDate_exceptionThrown() throws IOException {
        Path unknownTypeFile = temporaryDirectory.resolve("unknown.txt");
        Files.writeString(unknownTypeFile, "X | 0 | task\n");
        Path invalidDateFile = temporaryDirectory.resolve("invalid-date.txt");
        Files.writeString(invalidDateFile, "D | 0 | task | tomorrow\n");
        Path reversedEventFile = temporaryDirectory.resolve("reversed-event.txt");
        Files.writeString(reversedEventFile,
                "E | 0 | event | 2026-08-30 0900 | 2026-08-29 1700\n");

        assertThrows(IOException.class, () -> new Storage(unknownTypeFile.toString()).loadTasks());
        assertThrows(IOException.class, () -> new Storage(invalidDateFile.toString()).loadTasks());
        assertThrows(IOException.class, () -> new Storage(reversedEventFile.toString()).loadTasks());
    }

    @Test
    public void loadTasks_structurallyInvalidRecords_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        for (String line : List.of(
                "T",
                "T2 | 0",
                "T | 0",
                "T | 0 | ",
                "T | 0 | task | extra | excess",
                "D | 0 | task | ",
                "E | 0 | task | invalid | 2026-10-02")) {
            Files.writeString(dataFile, line);

            IOException exception = assertThrows(IOException.class, () ->
                    new Storage(dataFile.toString()).loadTasks(), line);

            assertEquals("Invalid task data on line 1.", exception.getMessage(), line);
        }
    }

    @Test
    public void loadTasks_oldFormat_tasksHaveNoNotes() throws IOException {
        Path dataFile = temporaryDirectory.resolve("old.txt");
        Files.writeString(dataFile, "T | 0 | old task\n");

        Task task = new Storage(dataFile.toString()).loadTasks().get(0);

        assertFalse(task.hasNote());
    }

    @Test
    public void loadTasks_malformedNote_exceptionThrown() throws IOException {
        Path invalidBase64File = temporaryDirectory.resolve("invalid-base64.txt");
        Files.writeString(invalidBase64File, "T | 0 | task | N:not-base64!\n");
        Path tooLongFile = temporaryDirectory.resolve("too-long.txt");
        String encodedNote = Base64.getEncoder().encodeToString(
                "a".repeat(201).getBytes(StandardCharsets.UTF_8));
        Files.writeString(tooLongFile, "T | 0 | task | N:" + encodedNote + "\n");

        assertThrows(IOException.class, () ->
                new Storage(invalidBase64File.toString()).loadTasks());
        assertThrows(IOException.class, () -> new Storage(tooLongFile.toString()).loadTasks());
    }

    @Test
    public void loadTasks_nonCanonicalOrInvalidUtf8Base64_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String invalidUtf8 = Base64.getEncoder().encodeToString(new byte[]{(byte) 0xc3, 0x28});
        for (String line : List.of(
                "T2 | 0 | YQ",
                "T2 | 0 | " + invalidUtf8,
                "T | 0 | task | N:YQ",
                "T | 0 | task | N:" + invalidUtf8)) {
            Files.writeString(dataFile, line);

            assertThrows(IOException.class, () ->
                    new Storage(dataFile.toString()).loadTasks(), line);
        }
    }

    @Test
    public void loadTasks_invalidOptionalNotes_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        for (String note : List.of("", " note", "note ", "two\nlines", "two\rlines",
                "two\u2028lines", "two\u2029lines")) {
            String encodedNote = Base64.getEncoder().encodeToString(
                    note.getBytes(StandardCharsets.UTF_8));
            Files.writeString(dataFile, "T | 0 | task | N:" + encodedNote);

            assertThrows(IOException.class, () ->
                    new Storage(dataFile.toString()).loadTasks(), note);
        }
        Files.writeString(dataFile, "T | 0 | task | not-a-note");
        assertThrows(IOException.class, () -> new Storage(dataFile.toString()).loadTasks());
    }

    @Test
    public void saveTasks_unsupportedTaskSubtype_assertionErrorThrown() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        assertThrows(AssertionError.class, () -> storage.saveTasks(List.of(new Task("unsupported"))));
    }
    @Test
    public void saveAndLoadTasks_separatorInDescriptions_roundTripPreserved() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        List<Task> tasks = List.of(new Todo("A | B"),
                new Deadline("C | D", LocalDateTime.of(2026, 10, 1, 0, 0)),
                new Event("E | F", LocalDateTime.of(2026, 10, 1, 0, 0),
                        LocalDateTime.of(2026, 10, 2, 0, 0)));
        tasks.get(0).setNote("N: | literal");
        storage.saveTasks(tasks);
        assertEquals(tasks.stream().map(Task::toString).toList(),
                storage.loadTasks().stream().map(Task::toString).toList());
    }

    @Test
    public void loadTasks_invalidRecords_blockSavingWithoutChangingFile() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        for (String line : List.of("D | 0 | task | 2026-02-30", "D | 0 | task | 2026-10-01 2400",
                "E | 0 | task | 2026-10-01 | 2026-10-01", "T2 | 0 | !invalid",
                "T2 | 0 | /w==", "T | 0 | bad\u0000text", "T | 0 | task | N:YQpi")) {
            Files.writeString(file, line);
            Storage storage = new Storage(file.toString());
            assertThrows(IOException.class, storage::loadTasks, line);
            assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("new"))), line);
            assertEquals(line, Files.readString(file));
        }
    }

    @Test
    public void loadTasks_failureThenSuccessfulReload_savingEnabledAgain() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        Files.writeString(file, "invalid");
        assertThrows(IOException.class, storage::loadTasks);

        Files.writeString(file, "T | 0 | repaired\n");
        assertEquals("repaired", storage.loadTasks().get(0).getDescription());

        storage.saveTasks(List.of(new Todo("new")));
        assertEquals(List.of("T | 0 | new"), Files.readAllLines(file));
    }

    @Test
    public void startFresh_malformedData_backsUpOriginalAndEnablesSaving() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | keep me\nD | maybe | broken\n";
        Files.writeString(file, original);
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::loadTasks);
        assertTrue(storage.canStartFresh());

        Path backup = storage.startFresh();

        assertTrue(backup.getFileName().toString()
                .matches("tasks-corrupt-\\d{8}-\\d{6}(?:-\\d+)?\\.txt"));
        assertEquals(original, Files.readString(backup));
        assertEquals("", Files.readString(file));
        assertFalse(storage.canStartFresh());
        storage.saveTasks(List.of(new Todo("new")));
        assertEquals(List.of("T | 0 | new"), Files.readAllLines(file));
    }

    @Test
    public void loadTasks_dataPathIsDirectory_actionableExceptionThrown() throws IOException {
        Path dataDirectory = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        Storage storage = new Storage(dataDirectory.toString());

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertEquals("The saved task path is a directory. Choose a regular file and restart RemBot.",
                exception.getMessage());
        assertFalse(storage.canStartFresh());
        assertThrows(IOException.class, storage::startFresh);
    }

    @Test
    public void saveTasks_destinationIsNonemptyDirectory_temporaryFileCleanedUp() throws IOException {
        Path dataDirectory = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        Path sentinel = dataDirectory.resolve("keep.txt");
        Files.writeString(sentinel, "keep");

        assertThrows(IOException.class, () ->
                new Storage(dataDirectory.toString()).saveTasks(List.of(new Todo("new"))));

        try (Stream<Path> files = Files.list(dataDirectory)) {
            assertEquals(List.of(sentinel), files.toList());
        }
        assertEquals("keep", Files.readString(sentinel));
    }

    @Test
    public void loadTasks_existingDuplicates_kept() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | same\nT | 1 | same\n");
        assertEquals(2, new Storage(file.toString()).loadTasks().size());
    }

}
