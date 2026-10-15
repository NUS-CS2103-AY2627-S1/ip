package lily.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import lily.storage.Storage.LoadResult;
import lily.storage.Storage.LoadStatus;
import lily.task.Deadline;
import lily.task.Event;
import lily.task.Task;
import lily.task.ToDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Storage}, which saves and loads Lily's task list to and from disk.
 *
 * <p>{@link Storage#load()} is one of the highest-value methods in the codebase to
 * cover: it is deliberately built to never let a damaged save file crash the app (a
 * missing file yields an empty list, individual corrupted lines are skipped rather than
 * aborting the whole load, and a wholly unreadable file is backed up rather than
 * refused), and that robustness behaviour is exactly the kind of thing that is easy to
 * silently break during a later refactor without a test catching it. {@link
 * Storage#save(List)} is tested alongside it since the two are only meaningful
 * together — a save/load round trip is the real contract this class has to uphold.
 *
 * <p>Each test uses a fresh {@code @TempDir} so tests never touch the real {@code
 * data/lily.txt} file and cannot interfere with one another.
 */
public class StorageTest {

    // ----- escapeField: pure text transformation, testable directly -----

    @Test
    public void escapeField_plainText_returnedUnchanged() {
        assertEquals("read book", Storage.escapeField("read book"));
    }

    @Test
    public void escapeField_containsPipe_pipeIsEscaped() {
        assertEquals("a \\| b", Storage.escapeField("a | b"));
    }

    @Test
    public void escapeField_containsBackslash_backslashIsEscaped() {
        assertEquals("a \\\\ b", Storage.escapeField("a \\ b"));
    }

    @Test
    public void escapeField_containsBothPipeAndBackslash_bothAreEscaped() {
        // Backslashes must be escaped first; otherwise the backslash introduced to
        // escape the pipe would itself look like part of an (incorrect) escape
        // sequence when the field is later read back.
        assertEquals("a \\\\\\| b", Storage.escapeField("a \\| b"));
    }

    @Test
    public void escapeField_null_returnsEmptyStringRatherThanThrowing() {
        assertEquals("", Storage.escapeField(null));
    }

    // ----- save/load round trip -----

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = storageIn(newTempFile("does-not-exist.txt"));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void loadWithReport_dataDirectoryMissing_distinguishedFromMissingFile(@TempDir Path tempDir) {
        Storage missingDirectoryStorage = storageIn(tempDir.resolve("missing/lily.txt"));
        Storage missingFileStorage = storageIn(tempDir.resolve("lily.txt"));

        assertEquals(LoadStatus.DATA_DIRECTORY_MISSING,
                missingDirectoryStorage.loadWithReport().status());
        assertEquals(LoadStatus.SAVE_FILE_MISSING,
                missingFileStorage.loadWithReport().status());
    }

    @Test
    public void saveThenLoad_singleToDo_roundTripsCorrectly(@TempDir Path tempDir) throws IOException {
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        storage.save(List.of(new ToDo("read book")));

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toFileString());
    }

    @Test
    public void saveThenLoad_deadlineAndEvent_roundTripsCorrectly(@TempDir Path tempDir) throws IOException {
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        Task deadline = new Deadline("return book", LocalDateTime.of(2019, 10, 15, 0, 0));
        Task event = new Event("project meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0), LocalDateTime.of(2019, 8, 6, 16, 0));
        storage.save(List.of(deadline, event));

        List<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("D | 0 | return book | 2019-10-15T00:00 | false", loaded.get(0).toFileString());
        assertEquals("E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00 | true | true",
                loaded.get(1).toFileString());
    }

    @Test
    public void saveThenLoad_explicitMidnight_preservesDisplayIntent(@TempDir Path tempDir) throws IOException {
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        storage.save(List.of(new Deadline("midnight deployment", LocalDateTime.of(2019, 10, 15, 0, 0), true)));

        assertEquals("[D][ ] midnight deployment (by: Oct 15 2019, 12:00AM)", storage.load().get(0).toString());
    }

    @Test
    public void saveThenLoad_markedTask_doneStatusPreserved(@TempDir Path tempDir) throws IOException {
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        Task task = new ToDo("read book");
        task.markAsDone();
        storage.save(List.of(task));

        Task loaded = storage.load().get(0);
        assertEquals("T | 1 | read book", loaded.toFileString());
    }

    @Test
    public void saveThenLoad_descriptionWithPipeCharacter_roundTripsWithoutCorruptingFields(
            @TempDir Path tempDir) throws IOException {
        // A pipe in the description is indistinguishable from a field delimiter unless
        // it is escaped on save and unescaped on load. A single save/load cycle should
        // recover exactly one task (not get split into extra "fields" and rejected as
        // corrupted), and saving that recovered task again should reproduce the exact
        // same on-disk record byte-for-byte, proving the escape/unescape pair is
        // stable under repeated round trips rather than drifting.
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        Task original = new ToDo("buy milk | eggs");
        storage.save(List.of(original));

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals(original.toFileString(), loaded.get(0).toFileString());
    }

    @Test
    public void saveThenLoad_emptyTaskList_producesEmptyFileAndEmptyLoad(@TempDir Path tempDir)
            throws IOException {
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        storage.save(List.of());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_missingParentDirectory_directoryIsCreatedAutomatically(@TempDir Path tempDir)
            throws IOException {
        Path nestedFile = tempDir.resolve("nested/sub/lily.txt");
        Storage storage = storageIn(nestedFile);

        storage.save(List.of(new ToDo("read book")));

        assertTrue(Files.exists(nestedFile));
    }

    @Test
    public void save_secondSaveReplacesRatherThanAppends(@TempDir Path tempDir) throws IOException {
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        storage.save(List.of(new ToDo("first save")));
        storage.save(List.of(new ToDo("second save")));

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("T | 0 | second save", loaded.get(0).toFileString());
    }

    @Test
    public void save_nullTaskList_exceptionThrown(@TempDir Path tempDir) {
        Storage storage = storageIn(tempDir.resolve("lily.txt"));
        assertThrows(IOException.class, () -> storage.save(null));
    }

    @Test
    public void save_parentPathIsFile_reportsDirectoryCreationFailure(@TempDir Path tempDir)
            throws IOException {
        Path parentFile = tempDir.resolve("not-a-directory");
        Files.writeString(parentFile, "existing file");
        Storage storage = storageIn(parentFile.resolve("lily.txt"));

        IOException thrown = assertThrows(IOException.class,
                () -> storage.save(List.of(new ToDo("read book"))));

        assertTrue(thrown.getMessage().contains("exists but is not a directory"));
    }

    // ----- load: tolerance for a damaged save file -----

    @Test
    public void load_oneCorruptedLineAmongValidOnes_corruptedLineSkippedRestLoaded(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of(
                "T | 1 | read book",
                "this line is not a valid record",
                "T | 0 | join sports club"
        ), StandardCharsets.UTF_8);
        Storage storage = storageIn(file);

        List<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("T | 1 | read book", loaded.get(0).toFileString());
        assertEquals("T | 0 | join sports club", loaded.get(1).toFileString());
    }

    @Test
    public void loadWithReport_malformedRecord_returnsWarningsAndPartialStatus(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of("T | 0 | valid", "not a task"), StandardCharsets.UTF_8);

        LoadResult result = storageIn(file).loadWithReport();

        assertEquals(LoadStatus.MALFORMED_RECORDS_SKIPPED, result.status());
        assertEquals(1, result.tasks().size());
        assertTrue(result.warnings().stream().anyMatch(message -> message.contains("line 2")));
    }

    @Test
    public void load_malformedEscapes_recordsSkippedWithoutChangingText(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of(
                "T | 0 | valid",
                "T | 0 | unknown\\qescape",
                "T | 0 | dangling\\",
                "T | 0 | unescaped|pipe"
        ), StandardCharsets.UTF_8);

        LoadResult result = storageIn(file).loadWithReport();

        assertEquals(1, result.tasks().size());
        assertEquals("valid", result.tasks().get(0).getDescription());
        assertEquals(LoadStatus.MALFORMED_RECORDS_SKIPPED, result.status());
        assertTrue(result.warnings().stream()
                .anyMatch(message -> message.contains("unsupported escape")));
        assertTrue(result.warnings().stream()
                .anyMatch(message -> message.contains("incomplete escape")));
        assertTrue(result.warnings().stream()
                .anyMatch(message -> message.contains("unescaped '|'")));
    }

    @Test
    public void load_invalidUtf8_fileBackedUpAndWarningReturned(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});
        Clock clock = Clock.fixed(Instant.parse("2026-09-17T03:04:05Z"), ZoneOffset.UTC);
        Storage storage = new Storage(file.toString(), clock);

        LoadResult result = storage.loadWithReport();
        Path backup = tempDir.resolve("lily.txt.corrupted-20260917-030405");

        assertEquals(LoadStatus.INVALID_UTF8_BACKED_UP, result.status());
        assertFalse(Files.exists(file));
        assertTrue(Files.exists(backup));
        assertTrue(result.warnings().stream().anyMatch(message -> message.contains(backup.toString())));
    }

    @Test
    public void load_invalidUtf8WhenBackupNameExists_usesNumericSuffix(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Path firstBackup = tempDir.resolve("lily.txt.corrupted-20260917-030405");
        Files.writeString(firstBackup, "older backup");
        Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});
        Clock clock = Clock.fixed(Instant.parse("2026-09-17T03:04:05Z"), ZoneOffset.UTC);

        new Storage(file.toString(), clock).loadWithReport();

        assertEquals("older backup", Files.readString(firstBackup));
        assertTrue(Files.exists(tempDir.resolve("lily.txt.corrupted-20260917-030405-1")));
    }

    @Test
    public void load_unknownTaskTypeLetter_lineSkipped(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of("X | 0 | mystery task"), StandardCharsets.UTF_8);
        Storage storage = storageIn(file);

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_invalidDoneFlag_lineSkipped(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of("T | 2 | read book"), StandardCharsets.UTF_8);
        Storage storage = storageIn(file);

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_oldPlainTextDateFormat_lineSkippedRatherThanCrashing(@TempDir Path tempDir)
            throws IOException {
        // Simulates a save file written before DateTimeParser existed, whose date
        // fields were plain strings like "June 6th" rather than ISO-style text.
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of(
                "D | 0 | return book | June 6th",
                "T | 1 | read book"
        ), StandardCharsets.UTF_8);
        Storage storage = storageIn(file);

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("T | 1 | read book", loaded.get(0).toFileString());
    }

    @Test
    public void load_eventEndingBeforeItStarts_lineSkippedRatherThanBreakingEventInvariant(
            @TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of(
                "E | 0 | impossible event | 2019-08-06T16:00 | 2019-08-06T14:00",
                "T | 0 | still valid"
        ), StandardCharsets.UTF_8);
        Storage storage = storageIn(file);

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | 0 | still valid", loaded.get(0).toFileString());
    }

    @Test
    public void load_blankLinesInFile_ignoredWithoutBeingCountedAsCorrupted(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("lily.txt");
        Files.write(file, List.of(
                "T | 1 | read book",
                "",
                "   ",
                "T | 0 | join sports club"
        ), StandardCharsets.UTF_8);
        Storage storage = storageIn(file);

        assertEquals(2, storage.load().size());
    }

    @Test
    public void load_pathIsADirectoryNotAFile_returnsEmptyListRatherThanThrowing(@TempDir Path tempDir) {
        // tempDir itself already exists as a directory, so pointing Storage directly at
        // it exercises the "not a regular file" guard rather than "file is missing".
        Storage storage = storageIn(tempDir);
        LoadResult result = storage.loadWithReport();
        assertTrue(result.tasks().isEmpty());
        assertEquals(LoadStatus.SAVE_PATH_NOT_FILE, result.status());
        assertFalse(result.warnings().isEmpty());
    }

    // ----- helpers -----

    private static Storage storageIn(Path filePath) {
        return new Storage(filePath.toString());
    }

    private static Path newTempFile(String fileName) {
        try {
            Path dir = Files.createTempDirectory("lily-storage-test");
            return dir.resolve(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
