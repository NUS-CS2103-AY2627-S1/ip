package peter.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import peter.PeterException;
import peter.task.Deadline;
import peter.task.Event;
import peter.task.Task;
import peter.task.Todo;

/**
 * Unit tests for {@link Storage}, which owns the save-file format.
 *
 * <p>This class is worth testing carefully because a mistake here loses or
 * corrupts the user's data, and because {@code load} contains the most
 * defensive branching in the app: every malformed line must produce a clear
 * error naming the offending line number instead of crashing.
 *
 * <p>{@link TempDir} gives each test its own throwaway directory, so the tests
 * exercise real file I/O without touching the user's actual data file and
 * without interfering with one another.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    private Path dataFile;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        dataFile = tempDir.resolve("tasks.txt");
        storage = new Storage(dataFile.toString());
    }

    // =====================================================================
    // load(): valid data
    // =====================================================================

    @Test
    public void load_missingFile_emptyListReturned() throws PeterException {
        // First run of the app: no data file exists yet, which is not an error.
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_emptyFile_emptyListReturned() throws PeterException, IOException {
        Files.writeString(dataFile, "");
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_todoNotDone_taskLoaded() throws PeterException, IOException {
        writeDataFile("T | 0 | read book");

        List<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void load_todoDone_taskLoadedAsDone() throws PeterException, IOException {
        // The "1" status flag is the only thing distinguishing these two cases,
        // so both directions need a test.
        writeDataFile("T | 1 | read book");
        assertTrue(storage.load().get(0).isDone());
    }

    @Test
    public void load_deadline_taskLoaded() throws PeterException, IOException {
        writeDataFile("D | 0 | return book | 2019-12-02T18:00");
        assertEquals("D | 0 | return book | 2019-12-02T18:00",
                storage.load().get(0).toDataString());
    }

    @Test
    public void load_event_taskLoaded() throws PeterException, IOException {
        writeDataFile("E | 1 | camp | 2019-12-02T18:00 | 2019-12-05T09:30");
        assertEquals("E | 1 | camp | 2019-12-02T18:00 | 2019-12-05T09:30",
                storage.load().get(0).toDataString());
    }

    @Test
    public void load_multipleTasks_orderPreserved() throws PeterException, IOException {
        // Task numbers shown to the user are positions in this list, so the
        // order on disk must be preserved exactly.
        writeDataFile("T | 0 | first",
                "D | 0 | second | 2019-12-02T00:00",
                "E | 0 | third | 2019-12-02T00:00 | 2019-12-05T00:00");

        List<Task> tasks = storage.load();
        assertEquals(3, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
        assertEquals("third", tasks.get(2).getDescription());
    }

    @Test
    public void load_blankLines_linesSkipped() throws PeterException, IOException {
        // A trailing newline or a stray blank line is tolerated rather than
        // being reported as corrupt data.
        writeDataFile("T | 0 | first", "", "   ", "T | 0 | second");
        assertEquals(2, storage.load().size());
    }

    @Test
    public void load_legacyDateOnlyDeadline_taskLoadedAtMidnight()
            throws PeterException, IOException {
        // Earlier versions saved a bare date. Such a file must still open, so
        // upgrading the app does not strand the user's existing tasks.
        writeDataFile("D | 0 | return book | 2019-12-02");
        assertEquals("D | 0 | return book | 2019-12-02T00:00",
                storage.load().get(0).toDataString());
    }

    @Test
    public void load_fileStartingWithByteOrderMark_markIgnored()
            throws PeterException, IOException {
        // A data file edited in some Windows editors gains a leading BOM,
        // which would otherwise make the first line's type unrecognizable.
        writeDataFile("﻿T | 0 | read book");
        assertEquals("read book", storage.load().get(0).getDescription());
    }

    @Test
    public void load_descriptionContainingBarWithoutSpaces_taskLoaded()
            throws PeterException, IOException {
        // Only " | " splits fields, so a bare "|" survives a round trip.
        writeDataFile("T | 0 | read|book");
        assertEquals("read|book", storage.load().get(0).getDescription());
    }

    // =====================================================================
    // load(): corrupt data
    // =====================================================================

    @Test
    public void load_unknownTaskType_exceptionThrown() throws IOException {
        writeDataFile("X | 0 | mystery");
        assertEquals(invalidDataMessage(1), assertThrows(PeterException.class, () ->
                storage.load()).getMessage());
    }

    @Test
    public void load_invalidStatusFlag_exceptionThrown() throws IOException {
        // Only "0" and "1" are valid; anything else is corrupt.
        writeDataFile("T | 2 | read book");
        assertEquals(invalidDataMessage(1), assertThrows(PeterException.class, () ->
                storage.load()).getMessage());
    }

    @Test
    public void load_tooFewFields_exceptionThrown() throws IOException {
        writeDataFile("T | 0");
        assertThrows(PeterException.class, () -> storage.load());
    }

    @Test
    public void load_todoWithTooManyFields_exceptionThrown() throws IOException {
        // A todo has no schedule, so an extra field means the line is wrong.
        writeDataFile("T | 0 | read book | 2019-12-02T00:00");
        assertThrows(PeterException.class, () -> storage.load());
    }

    @Test
    public void load_deadlineMissingDate_exceptionThrown() throws IOException {
        writeDataFile("D | 0 | return book");
        assertThrows(PeterException.class, () -> storage.load());
    }

    @Test
    public void load_eventMissingEndDate_exceptionThrown() throws IOException {
        writeDataFile("E | 0 | camp | 2019-12-02T00:00");
        assertThrows(PeterException.class, () -> storage.load());
    }

    @Test
    public void load_blankDescription_exceptionThrown() throws IOException {
        // A present-but-empty field is rejected, since a task with no
        // description cannot be displayed usefully.
        writeDataFile("T | 0 | ");
        assertThrows(PeterException.class, () -> storage.load());
    }

    @Test
    public void load_unparsableDate_exceptionThrown() throws IOException {
        // The date parse failure is converted into the same user-facing
        // message rather than leaking a DateTimeParseException.
        writeDataFile("D | 0 | return book | not-a-date");
        assertEquals(invalidDataMessage(1), assertThrows(PeterException.class, () ->
                storage.load()).getMessage());
    }

    @Test
    public void load_impossibleDate_exceptionThrown() throws IOException {
        writeDataFile("D | 0 | return book | 2019-02-30T18:00");
        assertThrows(PeterException.class, () -> storage.load());
    }

    @Test
    public void load_userInputDateFormat_exceptionThrown() throws IOException {
        // The save file uses ISO format only; the user-facing format is not
        // accepted here.
        writeDataFile("D | 0 | return book | 2/12/2019 1800");
        assertThrows(PeterException.class, () -> storage.load());
    }

    @Test
    public void load_corruptLineAfterValidLines_correctLineNumberReported()
            throws IOException {
        // The line number must point at the real offending line so the user
        // can find and fix it, which means counting valid lines too.
        writeDataFile("T | 0 | first", "T | 0 | second", "X | 0 | broken");
        assertEquals(invalidDataMessage(3), assertThrows(PeterException.class, () ->
                storage.load()).getMessage());
    }

    @Test
    public void load_corruptLineAfterBlankLine_blankLineCounted() throws IOException {
        // Blank lines are skipped but still occupy a line number, so the
        // reported number matches what the user sees in an editor.
        writeDataFile("", "X | 0 | broken");
        assertEquals(invalidDataMessage(2), assertThrows(PeterException.class, () ->
                storage.load()).getMessage());
    }

    // =====================================================================
    // save()
    // =====================================================================

    @Test
    public void save_emptyList_emptyFileWritten() throws PeterException, IOException {
        // Deleting the last task must clear the file, not leave the old
        // contents behind.
        writeDataFile("T | 0 | read book");
        storage.save(new ArrayList<>());
        assertTrue(Files.readAllLines(dataFile).isEmpty());
    }

    @Test
    public void save_tasks_linesWrittenInOrder() throws PeterException, IOException {
        storage.save(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0))));

        assertEquals(List.of("T | 0 | read book", "D | 0 | return book | 2019-12-02T18:00"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void save_missingParentDirectory_directoryCreated() throws PeterException {
        // On a first run the ./data folder does not exist yet, so saving has
        // to create it rather than fail.
        Path nestedFile = tempDir.resolve("data").resolve("tasks.txt");
        new Storage(nestedFile.toString()).save(List.of(new Todo("read book")));
        assertTrue(Files.exists(nestedFile));
    }

    @Test
    public void save_existingFile_contentsReplaced() throws PeterException, IOException {
        // Saving rewrites the whole file, so a shorter list must not leave
        // stale lines from the previous save.
        storage.save(List.of(new Todo("first"), new Todo("second")));
        storage.save(List.of(new Todo("only")));

        assertEquals(List.of("T | 0 | only"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void save_afterSaving_temporaryFileRemoved() throws PeterException {
        // Saving writes a ".tmp" file and moves it into place. If the move
        // leaves the temporary file behind, the data folder slowly fills with
        // junk, so this checks the cleanup.
        storage.save(List.of(new Todo("read book")));
        assertFalse(Files.exists(tempDir.resolve("tasks.txt.tmp")));
    }

    @Test
    public void save_nonAsciiDescription_encodingPreserved() throws PeterException, IOException {
        // The file is written and read as UTF-8, so accented text must survive.
        storage.save(List.of(new Todo("café résumé")));
        assertEquals("café résumé",
                storage.load().get(0).getDescription());
    }

    // =====================================================================
    // save() then load(): the round trip that matters most
    // =====================================================================

    @Test
    public void saveThenLoad_allTaskTypes_tasksRecovered() throws PeterException {
        // The real contract: whatever is saved must come back identical on the
        // next launch, including completion status and times.
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        Event event = new Event("camp",
                LocalDateTime.of(2019, 12, 2, 18, 0), LocalDateTime.of(2019, 12, 5, 9, 30));

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals(todo.toDataString(), loaded.get(0).toDataString());
        assertEquals(deadline.toDataString(), loaded.get(1).toDataString());
        assertEquals(event.toDataString(), loaded.get(2).toDataString());
    }

    @Test
    public void saveThenLoad_doneStatusPreserved() throws PeterException {
        Todo done = new Todo("done task");
        done.markAsDone();
        storage.save(List.of(done, new Todo("pending task")));

        List<Task> loaded = storage.load();
        assertTrue(loaded.get(0).isDone());
        assertFalse(loaded.get(1).isDone());
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    /** Writes the given lines to the data file, replacing any existing content. */
    private void writeDataFile(String... lines) throws IOException {
        Files.write(dataFile, List.of(lines), StandardCharsets.UTF_8);
    }

    /** Builds the message Storage reports for a corrupt line. */
    private static String invalidDataMessage(int lineNumber) {
        return "Oh dear! The task data file is invalid at line " + lineNumber
                + ". Please fix or remove it before restarting me!";
    }
}
