package ted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ted.task.Deadline;
import ted.task.Event;
import ted.task.Tag;
import ted.task.Task;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link Storage}, which writes the task list to disk and reads it back.
 * <p>
 * Every test writes inside a {@link TempDir} rather than the real save file, so
 * running the tests can never disturb the user's own tasks.
 */
public class StorageTest {
    /** A fixed date-time, so the tests do not depend on when they are run. */
    private static final LocalDateTime SECOND_OF_DECEMBER_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);

    /** Two hours before {@link #SECOND_OF_DECEMBER_6PM}. */
    private static final LocalDateTime SECOND_OF_DECEMBER_4PM = LocalDateTime.of(2019, 12, 2, 16, 0);

    /**
     * Verifies that loading a missing save file returns an empty task list.
     */
    @Test
    public void load_fileDoesNotExist_returnsEmptyList(@TempDir Path tempDir) throws TedException {
        // The first run on a new computer has no save file, which must not be an error.
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    /**
     * Verifies that saving creates the missing parent folder and data file.
     */
    @Test
    public void save_folderDoesNotExist_createsIt(@TempDir Path tempDir) throws TedException {
        Path dataFile = tempDir.resolve("data").resolve("ted.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.save(new TaskList(List.of(new Todo("borrow book"))));
        assertTrue(Files.exists(dataFile));
    }

    /**
     * Verifies that saving and loading preserve each task type and its saved state.
     */
    @Test
    public void saveThenLoad_everyTaskType_roundTripsUnchanged(@TempDir Path tempDir) throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        TaskList saved = new TaskList(List.of(
                todo,
                new Deadline("return book", SECOND_OF_DECEMBER_6PM),
                new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM)));

        storage.save(saved);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(0, storage.getSkippedLineCount());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        // Comparing the save format checks the description, the done flag and
        // the dates in one go.
        for (int i = 0; i < loadedTasks.size(); i++) {
            assertEquals(saved.asList().get(i).toSaveFormat(), loadedTasks.get(i).toSaveFormat());
        }
    }

    /**
     * Verifies that saving and loading preserve pipes and backslashes in descriptions.
     */
    @Test
    public void saveThenLoad_descriptionContainingSeparator_roundTripsUnchanged(@TempDir Path tempDir)
            throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        storage.save(new TaskList(List.of(new Todo("rock | roll \\ blues"))));

        List<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertEquals("[T][ ] rock | roll \\ blues", loadedTasks.get(0).toString());
    }

    /**
     * Verifies that saving an empty list removes previously saved tasks.
     */
    @Test
    public void save_emptyList_forgetsPreviousTasks(@TempDir Path tempDir) throws TedException {
        // Deleting the last task must be remembered, not silently undone by an
        // untouched save file.
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        storage.save(new TaskList(List.of(new Todo("borrow book"))));
        storage.save(new TaskList());
        assertTrue(storage.load().isEmpty());
    }

    /**
     * Verifies that loading ignores blank lines without counting them as corrupted.
     */
    @Test
    public void load_blankLines_ignoresThem(@TempDir Path tempDir) throws TedException, IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "T | 0 | borrow book\n\n   \n");

        Storage storage = new Storage(dataFile.toString());
        assertEquals(1, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    /**
     * Verifies that loading counts corrupted records and preserves valid tasks.
     */
    @Test
    public void load_corruptedLines_skipsThemAndKeepsTheRest(@TempDir Path tempDir)
            throws TedException, IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, String.join("\n",
                "T | 0 | borrow book",
                "X | 0 | unknown task type",
                "T | 2 | done flag is not 0 or 1",
                "D | 0 | not-a-date | return book",
                "D | 0 | missing the description",
                "gibberish",
                "E | 1 | 2019-12-02T16:00 | 2019-12-02T18:00 | meeting"));

        Storage storage = new Storage(dataFile.toString());
        List<Task> loadedTasks = storage.load();

        // The good lines survive; the five broken ones are counted, not fatal.
        assertEquals(2, loadedTasks.size());
        assertEquals(5, storage.getSkippedLineCount());
        assertEquals("[T][ ] borrow book", loadedTasks.get(0).toString());
        assertInstanceOf(Event.class, loadedTasks.get(1));
    }

    /**
     * Verifies that each load resets the count of skipped records.
     */
    @Test
    public void load_calledTwice_resetsTheSkippedCount(@TempDir Path tempDir)
            throws TedException, IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "gibberish\n");
        Storage storage = new Storage(dataFile.toString());
        storage.load();

        Files.writeString(dataFile, "T | 0 | borrow book\n");
        storage.load();
        assertEquals(0, storage.getSkippedLineCount());
    }

    /**
     * Verifies that loading accepts a valid record written directly to the save file.
     */
    @Test
    public void load_fileHandEditedByUser_isAccepted(@TempDir Path tempDir)
            throws TedException, IOException {
        // The save format is meant to be human-editable, so a line typed by hand
        // must load like one Ted wrote itself.
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "D | 1 | 2019-12-02T18:00 | return book\n");

        List<Task> loadedTasks = new Storage(dataFile.toString()).load();
        assertEquals(1, loadedTasks.size());
        assertEquals("D | 1 | 2019-12-02T18:00 | return book", loadedTasks.get(0).toSaveFormat());
    }

    /**
     * Verifies that saving and loading preserve tags and the rest of each task's saved state.
     */
    @Test
    public void saveThenLoad_taggedTasks_roundTripsUnchanged(@TempDir Path tempDir) throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        Todo todo = new Todo("read book");
        todo.addTags(List.of(new Tag("fun"), new Tag("school")));
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", SECOND_OF_DECEMBER_6PM);
        deadline.addTags(List.of(new Tag("library")));
        Event event = new Event("meeting", SECOND_OF_DECEMBER_4PM, SECOND_OF_DECEMBER_6PM);
        event.addTags(List.of(new Tag("cs2103")));
        TaskList saved = new TaskList(List.of(todo, deadline, event));

        storage.save(saved);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(0, storage.getSkippedLineCount());
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        // The save format includes the tags, so matching it proves they came back.
        for (int i = 0; i < loadedTasks.size(); i++) {
            assertEquals(saved.asList().get(i).toSaveFormat(), loadedTasks.get(i).toSaveFormat());
        }
    }

    /**
     * Verifies that loading preserves untagged records from before tagging was added.
     */
    @Test
    public void load_linesWrittenBeforeTags_readAsBefore(@TempDir Path tempDir)
            throws TedException, IOException {
        // None of these lines has a tags field, so each must load exactly as it
        // did before tags existed, even where the description looks like a tag.
        Path dataFile = tempDir.resolve("ted.txt");
        Files.write(dataFile, List.of(
                "T | 0 | #fun",
                "T | 0 | rock | roll",
                "D | 1 | 2019-12-02T18:00 | return book"));

        Storage storage = new Storage(dataFile.toString());
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(0, storage.getSkippedLineCount());
        assertEquals("T | 0 | #fun", loadedTasks.get(0).toSaveFormat());
        assertEquals("[T][ ] rock | roll", loadedTasks.get(1).toString());
        assertEquals("D | 1 | 2019-12-02T18:00 | return book", loadedTasks.get(2).toSaveFormat());
    }

    /**
     * Verifies that loading normalizes tag case and removes repeated tags.
     */
    @Test
    public void load_tagsInCapitalsOrRepeated_loadedOnceInLowerCase(@TempDir Path tempDir)
            throws TedException, IOException {
        // A hand-edited file need not match what Ted writes itself.
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "T | 0 | #Fun #fun #School | read book\n");

        List<Task> loadedTasks = new Storage(dataFile.toString()).load();
        assertEquals("T | 0 | #fun #school | read book", loadedTasks.get(0).toSaveFormat());
    }

    /**
     * Verifies that an invalid tag field remains part of a legacy task description.
     */
    @Test
    public void load_fieldThatIsNotValidTags_keptInDescription(@TempDir Path tempDir)
            throws TedException, IOException {
        // "#to-do" is not a tag, so the line is read like an older line whose
        // description holds an unescaped separator, rather than being skipped.
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "T | 0 | #to-do | read book\n");

        List<Task> loadedTasks = new Storage(dataFile.toString()).load();
        assertEquals("[T][ ] #to-do | read book", loadedTasks.get(0).toString());
    }

    /**
     * Verifies that a file with unreadable lines is copied aside before Ted
     * can overwrite those lines, and that the copy matches the original.
     */
    @Test
    public void load_unreadableLines_originalCopiedAside(@TempDir Path tempDir) throws TedException, IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        String original = "T | 0 | borrow book\ngibberish\n";
        Files.writeString(dataFile, original);

        Storage storage = new Storage(dataFile.toString());
        storage.load();

        Path backupFile = tempDir.resolve("ted.txt.bak");
        assertEquals(backupFile, storage.getBackupFile().orElseThrow());
        assertEquals(original, Files.readString(backupFile));
    }

    /**
     * Verifies that a file Ted can read in full is not copied.
     */
    @Test
    public void load_cleanFile_noCopyMade(@TempDir Path tempDir) throws TedException, IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "T | 0 | borrow book\n");

        Storage storage = new Storage(dataFile.toString());
        storage.load();

        assertTrue(storage.getBackupFile().isEmpty());
        assertFalse(Files.exists(tempDir.resolve("ted.txt.bak")));
    }

    /**
     * Verifies that a file that is not UTF-8 text is reported in plain words,
     * and copied aside before Ted starts over with an empty list.
     */
    @Test
    public void load_fileNotUtf8_exceptionThrownAndOriginalCopiedAside(@TempDir Path tempDir) throws IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        // 0xFF can never appear in UTF-8, e.g. when the file was saved as UTF-16.
        byte[] original = {(byte) 0xFF, (byte) 0xFE, 'T', 0};
        Files.write(dataFile, original);

        Storage storage = new Storage(dataFile.toString());
        TedException e = assertThrows(TedException.class, storage::load);

        assertTrue(e.getMessage().contains("not saved as UTF-8 text"));
        assertTrue(storage.getBackupFile().isPresent());
    }

    /**
     * Verifies that a folder where the data file should be is reported rather
     * than read.
     */
    @Test
    public void load_dataFileIsAFolder_exceptionThrown(@TempDir Path tempDir) throws IOException {
        Path dataFile = Files.createDirectory(tempDir.resolve("ted.txt"));
        TedException e = assertThrows(TedException.class, () -> new Storage(dataFile.toString()).load());
        assertTrue(e.getMessage().contains("is a folder, not a file"));
    }

    /**
     * Verifies that a file where the data folder should be is reported in plain words.
     */
    @Test
    public void save_fileWhereFolderShouldBe_exceptionThrown(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("data"), "not a folder");
        Storage storage = new Storage(tempDir.resolve("data").resolve("ted.txt").toString());

        TedException e = assertThrows(TedException.class, () -> storage.save(new TaskList()));
        assertTrue(e.getMessage().contains("is a file, but I need a folder there"));
    }

    /**
     * Verifies that a line holding nothing but a separator is skipped like any
     * other unreadable line, instead of stopping Ted from starting.
     */
    @Test
    public void load_separatorOnlyLine_skipped(@TempDir Path tempDir) throws TedException, IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, " | \nT | 0 | borrow book\n");

        Storage storage = new Storage(dataFile.toString());
        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    /**
     * Verifies that a save file Ted is not allowed to read is reported in
     * plain words. Skipped where the file system has no POSIX permissions,
     * e.g. on Windows.
     */
    @Test
    public void load_fileNotReadable_permissionDeniedReported(@TempDir Path tempDir) throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "T | 0 | borrow book\n");
        Files.setPosixFilePermissions(dataFile, PosixFilePermissions.fromString("-w-------"));
        // A user who can read anything, such as root, would not be denied at all.
        assumeTrue(!Files.isReadable(dataFile));

        Storage storage = new Storage(dataFile.toString());
        TedException e = assertThrows(TedException.class, storage::load);

        assertTrue(e.getMessage().contains("permission denied"));
        // The copy needs to read the file too, so none can be promised.
        assertTrue(storage.getBackupFile().isEmpty());
    }

    /**
     * Verifies that when the copy of an unreadable file cannot be made, the
     * tasks still load and no copy is promised.
     */
    @Test
    public void load_copyCannotBeMade_noCopyPromised(@TempDir Path tempDir) throws TedException, IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.writeString(dataFile, "T | 0 | borrow book\ngibberish\n");
        // A folder with something inside cannot be replaced by the copy.
        Path blockingFolder = Files.createDirectory(tempDir.resolve("ted.txt.bak"));
        Files.writeString(blockingFolder.resolve("keep.txt"), "in the way");

        Storage storage = new Storage(dataFile.toString());
        assertEquals(1, storage.load().size());
        assertTrue(storage.getBackupFile().isEmpty());
    }

    /**
     * Verifies that saving onto a folder is reported as a failed save, naming
     * the file, rather than crashing.
     */
    @Test
    public void save_dataFileIsAFolder_exceptionThrown(@TempDir Path tempDir) throws IOException {
        Path dataFile = Files.createDirectory(tempDir.resolve("ted.txt"));
        Storage storage = new Storage(dataFile.toString());

        TedException e = assertThrows(TedException.class, () -> storage.save(new TaskList()));
        assertTrue(e.getMessage().contains("I couldn't save your tasks to " + dataFile + ": "));
    }
}
