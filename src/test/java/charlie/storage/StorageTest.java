package charlie.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charlie.exception.CharlieException;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.Todo;

public class StorageTest {
    @Test
    public void load_invalidCompletionStatus_exceptionThrown(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, "T | Finished | borrow book");
        Storage storage = new Storage(saveFile.toString());

        CharlieException exception = assertThrows(CharlieException.class, storage::load);

        assertEquals("Invalid completion status in saved task.", exception.getMessage());
    }

    @Test
    public void load_blankDescription_exceptionThrown(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, "T | Not done | ");
        Storage storage = new Storage(saveFile.toString());

        CharlieException exception = assertThrows(CharlieException.class, storage::load);

        assertEquals("Description cannot be empty.", exception.getMessage());
    }

    @Test
    public void load_eventEndsBeforeStart_exceptionThrown(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(
                saveFile,
                "E | Not done | meeting | 2026-09-20T11:00 | 2026-09-20T10:00");
        Storage storage = new Storage(saveFile.toString());

        CharlieException exception = assertThrows(CharlieException.class, storage::load);

        assertEquals("Event end must be after its start.", exception.getMessage());
    }

    @Test
    public void load_missingFile_returnsEmptyList(@TempDir Path temporaryDirectory) {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesTaskData(
            @TempDir Path temporaryDirectory) {
        Path saveFile = temporaryDirectory.resolve("nested").resolve("charlie.txt");
        Storage storage = new Storage(saveFile.toString());
        List<Task> tasks = List.of(
                new Todo("borrow book", true),
                new Deadline("submit report", false, LocalDate.of(2026, 9, 20)),
                new Event(
                        "project meeting",
                        true,
                        LocalDateTime.of(2026, 9, 21, 14, 0),
                        LocalDateTime.of(2026, 9, 21, 16, 0)));

        storage.save(tasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(tasks.stream().map(Task::saveFileFormat).toList(),
                loadedTasks.stream().map(Task::saveFileFormat).toList());
    }

    @Test
    public void load_blankLines_ignoresBlankLines(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, System.lineSeparator()
                + "T | Not done | borrow book" + System.lineSeparator()
                + "   " + System.lineSeparator());

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] borrow book", tasks.get(0).toString());
    }

    @Test
    public void load_unknownTaskType_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(temporaryDirectory, "X | Not done | mystery");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Unknown saved task type: X", exception.getMessage());
    }

    @Test
    public void load_wrongFieldCount_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(temporaryDirectory, "D | Not done | missing deadline");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Invalid number of fields in saved task.", exception.getMessage());
    }

    @Test
    public void load_invalidDeadline_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(
                temporaryDirectory, "D | Not done | impossible | 2026-02-30");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Deadline must be a valid date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void load_invalidEventDateTime_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(
                temporaryDirectory,
                "E | Not done | meeting | invalid | 2026-09-21T16:00");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Saved event contains an invalid date-time.", exception.getMessage());
    }

    @Test
    public void load_pathIsDirectory_exceptionThrown(@TempDir Path temporaryDirectory) {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(temporaryDirectory.toString()).load());

        assertEquals("Could not read the saved task file.", exception.getMessage());
    }

    @Test
    public void save_parentIsFile_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "not a directory");
        Storage storage = new Storage(blockedParent.resolve("charlie.txt").toString());

        CharlieException exception = assertThrows(
                CharlieException.class, () -> storage.save(List.of(new Todo("unsaved", false))));

        assertEquals("Could not save tasks.", exception.getMessage());
    }

    @Test
    public void save_loadFailed_preservesCorruptFile(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(temporaryDirectory, "D | Not done | missing deadline");
        String originalContent = Files.readString(saveFile);
        Storage storage = new Storage(saveFile.toString());
        assertThrows(CharlieException.class, storage::load);

        CharlieException exception = assertThrows(
                CharlieException.class, () -> storage.save(List.of(new Todo("new task", false))));

        assertEquals("Cannot save tasks because saved tasks could not be loaded.",
                exception.getMessage());
        assertEquals(originalContent, Files.readString(saveFile));
    }

    @Test
    public void save_duplicateLoadedTasks_preservesCorruptFile(@TempDir Path temporaryDirectory)
            throws IOException {
        String originalContent = "T | Not done | repeated" + System.lineSeparator()
                + "T | Done | repeated" + System.lineSeparator();
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, originalContent);
        Storage storage = new Storage(saveFile.toString());

        CharlieException loadingError = assertThrows(CharlieException.class, storage::load);
        CharlieException savingError = assertThrows(
                CharlieException.class, () -> storage.save(List.of(new Todo("new task", false))));

        assertEquals("This task already exists in the list.", loadingError.getMessage());
        assertEquals("Cannot save tasks because saved tasks could not be loaded.",
                savingError.getMessage());
        assertEquals(originalContent, Files.readString(saveFile));
    }

    @Test
    public void save_existingFile_replacesContentsWithoutTemporaryFile(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = writeSavedLine(temporaryDirectory, "T | Not done | old task");
        Storage storage = new Storage(saveFile.toString());

        storage.save(List.of(new Todo("new task", false)));

        assertEquals("T | Not done | new task" + System.lineSeparator(),
                Files.readString(saveFile));
        try (Stream<Path> directoryEntries = Files.list(temporaryDirectory)) {
            assertEquals(1, directoryEntries.count());
        }
    }

    private Path writeSavedLine(Path temporaryDirectory, String line) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, line + System.lineSeparator());
        return saveFile;
    }
}
