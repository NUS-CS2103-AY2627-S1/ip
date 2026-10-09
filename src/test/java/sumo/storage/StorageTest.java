package sumo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sumo.task.Deadline;
import sumo.task.Event;
import sumo.task.Task;
import sumo.task.Todo;
import sumo.ui.Ui;

/** Tests durable task saving and tolerant loading in {@link Storage}. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies first-run loading and data-directory creation. */
    @Test
    public void load_fileDoesNotExist_emptyListReturnedAndDirectoryCreated() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("sumo.txt");
        Storage storage = new Storage(dataFile);

        List<Task> tasks = storage.load(new RecordingUi());

        assertTrue(tasks.isEmpty());
        assertTrue(Files.isDirectory(dataFile.getParent()));
        assertFalse(Files.exists(dataFile));
    }

    /** Verifies that every task type and status survives a save-load round trip. */
    @Test
    public void saveAndLoad_allTaskTypesAndStatuses_roundTripPreserved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("sumo.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 2, 3));
        Event event = new Event("camp",
                LocalDateTime.of(2026, 2, 3, 9, 0),
                LocalDateTime.of(2026, 2, 3, 17, 30));

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load(new RecordingUi());

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals(todo.toDataString(), loaded.get(0).toDataString());
        assertEquals(deadline.toDataString(), loaded.get(1).toDataString());
        assertEquals(event.toDataString(), loaded.get(2).toDataString());
        assertTrue(loaded.get(0).isDone());
    }

    /** Verifies that saving replaces obsolete file contents. */
    @Test
    public void save_existingFileReplacedWithCurrentTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("old")));

        storage.save(List.of(new Todo("new")));

        assertEquals(List.of("T | 0 | new"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /** Verifies that invalid records are reported without hiding valid records. */
    @Test
    public void load_blankAndInvalidRecords_validRecordsLoadedAndErrorsReported() throws IOException {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Files.write(dataFile, List.of(
                "T | 0 | first",
                "",
                "X | 0 | unknown",
                "D | done | report | 2026-02-03",
                "D | 0 | report | invalid-date",
                "E | 1 | camp | 2026-02-03 | 2026-02-04"), StandardCharsets.UTF_8);
        RecordingUi ui = new RecordingUi();

        List<Task> loaded = new Storage(dataFile).load(ui);

        assertEquals(List.of("first", "camp"),
                loaded.stream().map(Task::getDescription).toList());
        assertTrue(loaded.get(1).isDone());
        assertEquals(List.of(3, 4, 5), ui.invalidLineNumbers);
    }

    @Test
    public void load_invalidFieldsRangesAndDuplicates_validRecordsRemainReadOnly() throws IOException {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        String original = String.join("\n", "T | 0 | first", "T | 1 | FIRST", "T | 0 | ",
                "T | 0 | extra | field", "D | 0 | report | 2026-02-30", "D | 0 | report | 0000-01-01",
                "E | 0 | camp | 2026-02-03 | 2026-02-03", "E | 0 | camp | 2026-02-04 | 2026-02-03",
                "T | 0 | bad\u0000text", "T | 0 | second");
        Files.writeString(dataFile, original);
        Storage storage = new Storage(dataFile);
        RecordingUi ui = new RecordingUi();

        List<Task> loaded = storage.load(ui);

        assertEquals(List.of("first", "second"), loaded.stream().map(Task::getDescription).toList());
        assertEquals(List.of(2, 3, 4, 5, 6, 7, 8, 9), ui.invalidLineNumbers);
        assertThrows(IOException.class, () -> storage.save(loaded));
        assertEquals(original, Files.readString(dataFile));
    }

    @Test
    public void load_invalidUtf8_savingBlockedEvenAfterFileBecomesReadable() throws IOException {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Files.write(dataFile, new byte[] {(byte) 0xc3, 0x28});
        Storage storage = new Storage(dataFile);

        assertThrows(IOException.class, () -> storage.load(new RecordingUi()));
        Files.writeString(dataFile, "T | 0 | recovered");
        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("new"))));
        assertEquals("T | 0 | recovered", Files.readString(dataFile));
        assertEquals("recovered", storage.load(new RecordingUi()).get(0).getDescription());
        storage.save(List.of(new Todo("recovered")));
    }

    @Test
    public void loadAndSave_directoryAtDataPath_directoryPreserved() throws IOException {
        Path dataFile = Files.createDirectory(temporaryDirectory.resolve("sumo.txt"));
        Storage storage = new Storage(dataFile);

        assertThrows(IOException.class, () -> storage.load(new RecordingUi()));
        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("new"))));
        assertThrows(IOException.class, () -> new Storage(dataFile).save(List.of(new Todo("new"))));
        assertTrue(Files.isDirectory(dataFile));
    }

    @Test
    public void loadAndSave_parentIsAFile_originalPreserved() throws IOException {
        Path parent = temporaryDirectory.resolve("data");
        Files.writeString(parent, "keep this file");
        Storage storage = new Storage(parent.resolve("sumo.txt"));

        assertThrows(IOException.class, () -> storage.load(new RecordingUi()));
        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("new"))));
        assertEquals("keep this file", Files.readString(parent));
    }

    @Test
    public void save_fileChangedOutsideSession_externalDataPreserved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("original")));
        storage.load(new RecordingUi());
        Files.writeString(dataFile, "T | 0 | external edit");

        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("stale"))));
        assertEquals("T | 0 | external edit", Files.readString(dataFile));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(dataFile), files.toList());
        }
    }

    @Test
    public void save_fileDeletedOutsideSession_doesNotRecreateStaleData() throws IOException {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("original")));
        Files.delete(dataFile);

        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("stale"))));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void save_fileCreatedByAnotherSession_doesNotOverwriteNewData() throws IOException {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Storage firstSession = new Storage(dataFile);
        firstSession.load(new RecordingUi());
        new Storage(dataFile).save(List.of(new Todo("other session")));

        assertThrows(IOException.class, () -> firstSession.save(List.of(new Todo("stale"))));
        assertEquals(List.of("T | 0 | other session"), Files.readAllLines(dataFile));
    }

    /** Records invalid line numbers without coupling storage tests to console output. */
    private static class RecordingUi extends Ui {
        private final List<Integer> invalidLineNumbers = new ArrayList<>();

        /** Records the line number of an invalid stored task. */
        @Override
        public void showInvalidTaskError(int lineNumber, String message) {
            invalidLineNumbers.add(lineNumber);
        }
    }
}
