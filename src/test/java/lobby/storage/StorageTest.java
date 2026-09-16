package lobby.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lobby.task.Deadline;
import lobby.task.Event;
import lobby.task.Task;
import lobby.task.Todo;

/**
 * Tests saving, loading, and recovery behavior in {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that loading a missing save file succeeds with no tasks.
     */
    @Test
    public void load_missingFile_returnsEmptySuccessfulResult() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/lobby.txt").toString());

        Storage.LoadResult result = storage.load();

        assertAll(() ->
                assertTrue(result.tasks().isEmpty()), () ->
                assertEquals(0, result.skippedLines()), () ->
                assertFalse(result.readFailed()));
    }

    /**
     * Verifies that saving and reloading preserves every task type and creates directories.
     *
     * @throws IOException if the temporary test file cannot be written or read
     */
    @Test
    public void saveAndLoad_allTaskTypes_preservesTasksAndCreatesParentDirectory()
            throws IOException {
        Path saveFile = temporaryDirectory.resolve("nested/data/lobby.txt");
        Storage storage = new Storage(saveFile.toString());
        Todo todo = new Todo("read | book \\ notes");
        todo.markAsDone();
        List<Task> tasks = List.of(
                todo,
                new Deadline("submit report", LocalDate.of(2026, 9, 30)),
                new Event("project meeting", "Monday | 2pm", "Monday \\ 3pm"));

        storage.save(tasks);
        Storage.LoadResult result = storage.load();

        assertAll(() ->
                assertTrue(Files.exists(saveFile)), () ->
                assertFalse(result.readFailed()), () ->
                assertEquals(0, result.skippedLines()), () ->
                assertEquals(
                        tasks.stream().map(Task::toDataString).toList(),
                        result.tasks().stream().map(Task::toDataString).toList()));
    }

    /**
     * Verifies that loading skips malformed records while preserving valid tasks.
     *
     * @throws IOException if the temporary test file cannot be written or read
     */
    @Test
    public void load_validAndMalformedLines_recoversValidTasksAndCountsSkippedLines()
            throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Files.write(saveFile, List.of(
                "T | 0 | valid task",
                "",
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "D | 0 | missing date | ",
                "E | 1 | missing end time | 2pm"), StandardCharsets.UTF_8);
        Storage storage = new Storage(saveFile.toString());

        Storage.LoadResult result = storage.load();

        assertAll(() ->
                assertFalse(result.readFailed()), () ->
                assertEquals(4, result.skippedLines()), () ->
                assertEquals(1, result.tasks().size()), () ->
                assertEquals("T | 0 | valid task", result.tasks().get(0).toDataString()));
    }

    /**
     * Verifies that every required description or time field rejects blank content.
     *
     * @throws IOException if the temporary test file cannot be written or read
     */
    @Test
    public void load_blankFieldsInEveryPosition_skipsInvalidTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Files.write(saveFile, List.of(
                "T | 0 | ",
                "D | 0 | \t | 2026-09-30",
                "D | 0 | report | ",
                "E | 0 | | Monday | Tuesday",
                "E | 0 | meeting | \t | Tuesday",
                "E | 0 | meeting | Monday | ",
                "T | 1 | valid task"), StandardCharsets.UTF_8);
        Storage storage = new Storage(saveFile.toString());

        Storage.LoadResult result = storage.load();

        assertAll(() ->
                assertFalse(result.readFailed()), () ->
                assertEquals(6, result.skippedLines()), () ->
                assertEquals(1, result.tasks().size()), () ->
                assertEquals("T | 1 | valid task", result.tasks().get(0).toDataString()));
    }

    /**
     * Verifies that attempting to load a directory is reported as a read failure.
     */
    @Test
    public void load_pathIsDirectory_returnsReadFailure() {
        Storage storage = new Storage(temporaryDirectory.toString());

        Storage.LoadResult result = storage.load();

        assertAll(() ->
                assertTrue(result.readFailed()), () ->
                assertTrue(result.tasks().isEmpty()), () ->
                assertEquals(0, result.skippedLines()));
    }

    @Test
    public void load_eachTaskLayoutAndStatus_preservesStoredFields() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        List<String> records = List.of(
                "T | 0 | incomplete todo", "T | 1 | completed todo",
                "D | 0 | incomplete deadline | 2026-09-30", "D | 1 | completed deadline | 2024-02-29",
                "E | 0 | incomplete event | Monday | Tuesday", "E | 1 | completed event | 2pm | 3pm");
        Files.write(saveFile, records);

        Storage.LoadResult result = new Storage(saveFile.toString()).load();

        assertFalse(result.readFailed());
        assertEquals(0, result.skippedLines());
        assertEquals(records, result.tasks().stream().map(Task::toDataString).toList());
    }

    @Test
    public void load_missingExtraOrBlankFields_skipsOnlyInvalidRecords() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        List<String> invalidRecords = List.of(
                "T", "T |", "T | 2 | invalid status", "X | 0 | unknown type",
                "T | 0", "D | 0 | missing date", "E | 0 | missing end | start",
                "T | 0 | todo | extra", "D | 0 | deadline | 2026-09-30 | extra",
                "E | 0 | event | start | end | extra",
                "T | 0 | ", "D | 0 | | 2026-09-30", "D | 0 | deadline | ",
                "E | 0 | | start | end", "E | 0 | event | | end", "E | 0 | event | start | ");
        List<String> records = new ArrayList<>(invalidRecords);
        records.add("T | 1 | valid task after malformed records");
        Files.write(saveFile, records);

        Storage.LoadResult result = new Storage(saveFile.toString()).load();

        assertFalse(result.readFailed());
        assertEquals(invalidRecords.size(), result.skippedLines());
        assertEquals(1, result.tasks().size());
        assertEquals("T | 1 | valid task after malformed records", result.tasks().get(0).toDataString());
    }

    @Test
    public void load_invalidDeadlineDates_recoversSurroundingValidTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Files.write(saveFile, List.of(
                "T | 0 | before invalid dates",
                "D | 0 | impossible date | 2025-02-29",
                "D | 1 | wrong format | 30-09-2026",
                "D | 1 | valid leap day | 2024-02-29"));

        Storage.LoadResult result = new Storage(saveFile.toString()).load();

        assertFalse(result.readFailed());
        assertEquals(2, result.skippedLines());
        assertEquals(List.of("T | 0 | before invalid dates", "D | 1 | valid leap day | 2024-02-29"),
                result.tasks().stream().map(Task::toDataString).toList());
    }

    @Test
    public void save_corruptFile_preservesOriginalOnceAcrossSaves() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        byte[] original = "T | 0 | valid\r\ninvalid record\r\n".getBytes(StandardCharsets.UTF_8);
        Files.write(saveFile, original);
        Storage storage = new Storage(saveFile.toString());
        Storage.LoadResult loaded = storage.load();
        storage.save(loaded.tasks());
        storage.save(List.of(new Todo("replacement")));
        try (var files = Files.list(temporaryDirectory)) {
            List<Path> backups = files.filter(path -> path.toString().endsWith(".bak")).toList();
            assertEquals(1, backups.size());
            assertArrayEquals(original, Files.readAllBytes(backups.get(0)));
        }
        assertFalse(storage.needsRecoveryBackup());
        assertEquals("replacement", storage.load().tasks().get(0).getDescription());
    }

    @Test
    public void save_invalidUtf8_preservesUnreadableBytes() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        byte[] original = {(byte) 0xc3, (byte) 0x28};
        Files.write(saveFile, original);
        Storage storage = new Storage(saveFile.toString());
        assertTrue(storage.load().readFailed());
        storage.save(List.of(new Todo("new task")));
        try (var files = Files.list(temporaryDirectory)) {
            Path backup = files.filter(path -> path.toString().endsWith(".bak")).findFirst().orElseThrow();
            assertArrayEquals(original, Files.readAllBytes(backup));
        }
    }

    @Test
    public void save_originalUnavailable_blocksUntilPreservationSucceeds() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Files.writeString(saveFile, "invalid record");
        Storage storage = new Storage(saveFile.toString());
        storage.load();
        Files.delete(saveFile);
        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("unsaved"))));
        assertTrue(storage.needsRecoveryBackup());
        assertFalse(Files.exists(saveFile));
        Files.writeString(saveFile, "invalid record");
        storage.save(List.of(new Todo("saved")));
        assertFalse(storage.needsRecoveryBackup());
    }

    @Test
    public void save_parentlessPath_savesInWorkingDirectory() throws IOException {
        Path saveFile = Path.of("lobby-test-" + java.util.UUID.randomUUID() + ".txt");
        try {
            Storage storage = new Storage(saveFile.toString());
            storage.save(List.of(new Todo("relative path")));
            assertEquals("relative path", storage.load().tasks().get(0).getDescription());
        } finally {
            Files.deleteIfExists(saveFile);
        }
    }
}
