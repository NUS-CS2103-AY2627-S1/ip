package seedu.goat.storage;

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

import seedu.goat.GoatException;
import seedu.goat.task.Deadline;
import seedu.goat.task.Event;
import seedu.goat.task.Task;
import seedu.goat.task.Todo;

/**
 * Tests reading and writing the save file.
 * <p>
 * Every test works inside a temporary directory supplied by JUnit, so the tests never
 * touch the real save file and leave nothing behind.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    /**
     * Returns a store backed by a file inside the temporary directory.
     *
     * @param fileName name of the file within the temporary directory
     * @return the store under test
     */
    private Storage storageFor(String fileName) {
        return new Storage(tempDir.resolve(fileName).toString());
    }

    @Test
    public void load_fileDoesNotExist_emptyListReturned() throws GoatException {
        // A first run must look like an empty list, not a failure.
        assertTrue(storageFor("absent.txt").load().isEmpty());
    }

    @Test
    public void save_parentDirectoryDoesNotExist_directoryCreated() throws GoatException {
        Storage storage = new Storage(tempDir.resolve("nested/deeper/goat.txt").toString());
        storage.save(List.of(new Todo("read book")));
        assertTrue(Files.exists(tempDir.resolve("nested/deeper/goat.txt")));
    }

    @Test
    public void saveThenLoad_allThreeTaskTypes_roundTripsExactly() throws GoatException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Event event = new Event("meeting", LocalDate.of(2019, 10, 15),
                LocalDate.of(2019, 10, 16));
        deadline.markAsDone();

        Storage storage = storageFor("goat.txt");
        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals(todo.toFileString(), loaded.get(0).toFileString());
        assertEquals(deadline.toFileString(), loaded.get(1).toFileString());
        assertEquals(event.toFileString(), loaded.get(2).toFileString());
    }

    @Test
    public void saveThenLoad_doneFlag_preserved() throws GoatException {
        Todo done = new Todo("finished");
        done.markAsDone();

        Storage storage = storageFor("goat.txt");
        storage.save(List.of(done, new Todo("not finished")));
        List<Task> loaded = storage.load();

        assertEquals("[T][X] finished", loaded.get(0).toString());
        assertEquals("[T][ ] not finished", loaded.get(1).toString());
    }

    @Test
    public void save_replacesPreviousContents() throws GoatException {
        Storage storage = storageFor("goat.txt");
        storage.save(List.of(new Todo("first"), new Todo("second")));
        storage.save(List.of(new Todo("only")));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_malformedLines_badLinesSkippedAndCounted() throws IOException, GoatException {
        Path file = tempDir.resolve("goat.txt");
        Files.write(file, List.of(
                "T | 1 | read book",
                "X | 0 | unknown type letter",
                "D | 0 | missing the by field",
                "T | 7 | status is not 0 or 1",
                "T | 0 | ",
                "no separators at all",
                "E | 0 | only one date | 2019-10-15",
                "D | 0 | return book | 2019-12-02"));

        Storage storage = storageFor("goat.txt");
        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size(), "only the two well-formed lines should survive");
        assertEquals(6, storage.getSkippedLineCount());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", loaded.get(1).toString());
    }

    @Test
    public void load_unknownTypeLetter_notSilentlyTreatedAsAnotherType()
            throws IOException, GoatException {
        // Guards a real bug: the decoder once fell through to Event for any unknown
        // letter, turning a damaged line into the wrong kind of task.
        Files.write(tempDir.resolve("goat.txt"),
                List.of("X | 0 | mystery | 2019-10-15 | 2019-10-16"));

        Storage storage = storageFor("goat.txt");
        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_blankLines_ignoredWithoutCountingAsDamage()
            throws IOException, GoatException {
        Files.write(tempDir.resolve("goat.txt"),
                List.of("T | 0 | read book", "", "   ", "T | 0 | write report"));

        Storage storage = storageFor("goat.txt");
        assertEquals(2, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount(), "blank lines are not corruption");
    }

    @Test
    public void load_datesThatNoLongerParse_linesSkipped() throws IOException, GoatException {
        // Save files written before dates were understood hold free text here.
        Files.write(tempDir.resolve("goat.txt"), List.of(
                "T | 0 | still fine",
                "D | 0 | return book | Sunday",
                "E | 0 | old event | Mon 2pm | 4pm"));

        Storage storage = storageFor("goat.txt");
        assertEquals(1, storage.load().size());
        assertEquals(2, storage.getSkippedLineCount());
    }

    @Test
    public void getSkippedLineCount_resetBetweenLoads() throws IOException, GoatException {
        Path file = tempDir.resolve("goat.txt");
        Storage storage = storageFor("goat.txt");

        Files.write(file, List.of("junk"));
        storage.load();
        assertEquals(1, storage.getSkippedLineCount());

        Files.write(file, List.of("T | 0 | clean"));
        storage.load();
        assertEquals(0, storage.getSkippedLineCount(),
                "a clean load must not report the previous load's damage");
    }
    @Test
    public void load_extraFieldsAndReversedRanges_preservesOriginal() throws Exception {
        Path file = tempDir.resolve("goat.txt");
        String original = "T | 0 | task | extra\nE | 0 | trip | 2026-09-19 | 2026-09-18\n";
        Files.writeString(file, original);
        Storage storage = storageFor("goat.txt");
        assertTrue(storage.load().isEmpty());
        assertEquals(2, storage.getSkippedLineCount());
        assertThrows(GoatException.class, () -> storage.save(List.of(new Todo("new"))));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void save_parentIsFile_reportsFailureAndPreservesFile() throws Exception {
        Files.writeString(tempDir.resolve("parent"), "keep me");
        Storage storage = storageFor("parent/goat.txt");
        assertThrows(GoatException.class, () -> storage.save(List.of(new Todo("new"))));
        assertEquals("keep me", Files.readString(tempDir.resolve("parent")));
    }

    @Test
    public void load_invalidUtf8_disablesSaving() throws Exception {
        Path file = tempDir.resolve("goat.txt");
        byte[] original = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, original);
        Storage storage = storageFor("goat.txt");
        assertThrows(GoatException.class, storage::load);
        assertThrows(GoatException.class, () -> storage.save(List.of()));
        assertEquals(2, Files.size(file));
    }

    @Test
    public void load_legacyDuplicatesAndSingleDayEvents_preserved() throws Exception {
        Path file = tempDir.resolve("goat.txt");
        Files.writeString(file, "T | 0 | task\nT | 1 | task\nE | 0 | day | 2026-09-18 | 2026-09-18\n");
        Storage storage = storageFor("goat.txt");
        assertEquals(3, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
        storage.save(storage.load());
        assertEquals(3, storage.load().size());
    }
}
