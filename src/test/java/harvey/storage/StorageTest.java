package harvey.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import harvey.HarveyException;
import harvey.task.Deadline;
import harvey.task.Event;
import harvey.task.Task;
import harvey.task.Todo;

/**
 * Tests {@link Storage} against real files in a temporary folder.
 * <p>
 * {@code @TempDir} gives each test its own empty directory, which JUnit deletes
 * afterwards. That keeps the tests independent of one another and stops them touching
 * the real {@code ./data/harvey.txt}, which belongs to whoever is running the app.
 * <p>
 * These are not unit tests in the strictest sense, since they touch the disk. They are
 * worth it: the whole point of Storage is what ends up in the file, and a fake file
 * system would be testing the fake.
 */
public class StorageTest {
    /** An empty folder created fresh for each test and removed afterwards. */
    @TempDir
    private Path tempDir;

    /** Returns a Storage writing into this test's temporary folder. */
    private Storage storage() {
        return new Storage(tempDir.toString(), "harvey.txt");
    }

    /** Writes the given lines straight to the save file, as a hand edit would. */
    private void writeFile(String... lines) throws IOException {
        Files.write(tempDir.resolve("harvey.txt"), List.of(lines));
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyListWithoutError() throws HarveyException {
        // The first run on any machine takes this path, so it must not be an error.
        assertTrue(storage().load().isEmpty());
    }

    @Test
    public void save_folderDoesNotExist_createsItAndWritesTheFile() throws Exception {
        // Nothing has created ./data yet on a fresh clone, and writing into a missing
        // folder fails, so save has to make it.
        Storage storage = new Storage(tempDir.resolve("nested").toString(), "harvey.txt");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        storage.save(tasks);

        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(tempDir.resolve("nested").resolve("harvey.txt")));
    }

    @Test
    public void save_allThreeTaskTypes_writesOneLinePerTaskInOrder() throws Exception {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return book", java.time.LocalDate.of(2019, 10, 15)));
        tasks.add(new Event("project meeting", java.time.LocalDateTime.of(2019, 10, 15, 14, 0),
                java.time.LocalDateTime.of(2019, 10, 15, 16, 0)));
        storage().save(tasks);

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-10-15",
                "E | 0 | project meeting | 2019-10-15 1400 | 2019-10-15 1600"),
                Files.readAllLines(tempDir.resolve("harvey.txt")));
    }

    @Test
    public void saveThenLoad_allThreeTaskTypes_tasksComeBackUnchanged() throws Exception {
        // The property that matters most: whatever is saved must reload identically,
        // including the done flag and every date field.
        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", java.time.LocalDate.of(2019, 10, 15)));
        original.add(new Event("project meeting", java.time.LocalDateTime.of(2019, 10, 15, 14, 0),
                java.time.LocalDateTime.of(2019, 10, 15, 16, 0)));

        Storage storage = storage();
        storage.save(original);
        ArrayList<Task> reloaded = storage.load();

        assertEquals(original.size(), reloaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), reloaded.get(i).toString());
        }
        assertEquals(0, storage.getSkippedLines());
    }

    @Test
    public void save_emptyList_clearsTheFile() throws Exception {
        Storage storage = storage();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        storage.save(tasks);
        // Deleting the last task must leave an empty file, not the old contents.
        storage.save(new ArrayList<>());

        assertTrue(Files.readAllLines(tempDir.resolve("harvey.txt")).isEmpty());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_blankLines_ignoredWithoutCountingAsDamage() throws Exception {
        // A trailing newline is normal, not a sign the file was edited badly.
        writeFile("T | 0 | read book", "", "   ");
        Storage storage = storage();

        assertEquals(1, storage.load().size());
        assertEquals(0, storage.getSkippedLines());
    }

    @Test
    public void load_lineWithTooFewFields_skippedAndCounted() throws Exception {
        writeFile("T | 0 | keep me", "D | 0 | missing its date");
        Storage storage = storage();

        assertEquals(1, storage.load().size());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_unknownTypeLetter_skippedRatherThanTreatedAsTodo() throws Exception {
        // Guessing here would turn damaged data into a task that looks legitimate,
        // which is worse than losing the line.
        writeFile("X | 0 | bogus type");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_emptyDescription_skipped() throws Exception {
        // The description is checked before any task is built, so no half-formed Todo
        // is created and thrown away.
        writeFile("T | 0 | ");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_deadlineWithTooManyFields_skipped() throws Exception {
        // Each task type has its own expected field count, so a deadline carrying an
        // extra field is damaged even though it has more than the shared three.
        writeFile("D | 0 | return book | 2019-10-15 | extra");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_doneFlagNeitherZeroNorOne_skipped() throws Exception {
        writeFile("T | yes | read book");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_unparseableDate_skipped() throws Exception {
        // Storage and the command parser must agree on what a date looks like.
        writeFile("D | 0 | return book | 15-Oct-2019");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_severalDamagedLines_goodLinesStillLoad() throws Exception {
        // One bad line must not cost the user the rest of the file.
        writeFile("T | 0 | first",
                "nonsense",
                "X | 0 | bad type",
                "E | 1 | meeting | 2019-10-15 1400 | 2019-10-15 1600");
        Storage storage = storage();
        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals(2, storage.getSkippedLines());
        assertEquals("[T][ ] first", loaded.get(0).toString());
        assertEquals("[E][X] meeting (from: Oct 15 2019 2:00PM to: Oct 15 2019 4:00PM)",
                loaded.get(1).toString());
    }

    @Test
    public void getSkippedLines_secondLoadOfCleanFile_countResetToZero() throws Exception {
        writeFile("nonsense");
        Storage storage = storage();
        storage.load();
        assertEquals(1, storage.getSkippedLines());

        // The count describes the most recent load only; a stale count would make
        // Harvey complain about a file that is now fine.
        writeFile("T | 0 | read book");
        storage.load();
        assertEquals(0, storage.getSkippedLines());
    }

    @Test
    public void load_eventWithUnreadableTimes_lineIsSkipped() throws Exception {
        // Events used to be saved with their times as free text. Such a line can no longer
        // be understood, so it is treated as damaged rather than loaded as a broken event.
        writeFile("E | 0 | meeting | Mon 2pm | 4pm");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_eventEndingBeforeItStarts_lineIsSkipped() throws Exception {
        // The file can be edited by hand, so an impossible event has to be caught on the
        // way in rather than reaching the clash check.
        writeFile("E | 0 | meeting | 2019-10-15 1600 | 2019-10-15 1400");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void load_damagedLine_originalBackedUpBeforeItCanBeOverwritten() throws Exception {
        writeFile("T | 0 | keep me", "D | 0 | missing its date");
        Storage storage = storage();
        storage.load();

        // The next save rewrites the file without the damaged line, so the backup is the
        // only place that line survives.
        Path backup = storage.getBackupPath();
        assertEquals(List.of("T | 0 | keep me", "D | 0 | missing its date"), Files.readAllLines(backup));
        assertTrue(storage.isOverwriteSafe());
    }

    @Test
    public void load_intactFile_noBackupMade() throws Exception {
        writeFile("T | 0 | read book");
        Storage storage = storage();
        storage.load();

        assertNull(storage.getBackupPath());
        // Only the save file itself should be in the folder. try-with-resources closes the
        // directory listing, which holds an open handle until it is closed.
        try (var files = Files.list(tempDir)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void load_invalidBytesOnOneLine_onlyThatLineSkipped() throws Exception {
        // 0xFF can never appear in UTF-8 text. Before, it made the whole file unreadable.
        byte[] good = "T | 0 | first\n".getBytes();
        byte[] bad = {(byte) 0xFF, '\n'};
        byte[] alsoGood = "T | 1 | second\n".getBytes();
        Files.write(tempDir.resolve("harvey.txt"), concat(good, bad, alsoGood));
        Storage storage = storage();

        assertEquals(2, storage.load().size());
        assertEquals(1, storage.getSkippedLines());
        assertNotNull(storage.getBackupPath());
    }

    @Test
    public void load_saveFileIsAFolder_exceptionThrown() throws Exception {
        Files.createDirectory(tempDir.resolve("harvey.txt"));
        Storage storage = storage();

        HarveyException e = assertThrows(HarveyException.class, storage::load);
        assertTrue(e.getMessage().contains("is a folder"), e.getMessage());
    }

    @Test
    public void save_folderPathIsAFile_exceptionGivesTheReason() throws Exception {
        Files.writeString(tempDir.resolve("nested"), "not a folder");
        Storage storage = new Storage(tempDir.resolve("nested").toString(), "harvey.txt");

        HarveyException e = assertThrows(HarveyException.class, () -> storage.save(new ArrayList<>()));
        assertTrue(e.getMessage().contains("because"), e.getMessage());
    }

    /** Joins byte arrays end to end, for writing a file with chosen bytes in it. */
    private static byte[] concat(byte[]... parts) {
        int length = 0;
        for (byte[] part : parts) {
            length += part.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }

    @Test
    public void load_emptyDescriptionBetweenOtherFields_skipped() throws Exception {
        // Unlike "T | 0 | ", this line still has enough fields, so it reaches the check
        // for an empty description rather than the check for too few fields.
        writeFile("D | 0 |  | 2026-10-01");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLines());
    }

    @Test
    public void describe_eachKindOfFileProblem_givesMatchingReason() {
        assertEquals("permission was denied", Storage.describe(new AccessDeniedException("f")));
        assertEquals("a file is in the way where the folder should be",
                Storage.describe(new FileAlreadyExistsException("data")));
        assertEquals("the file or its folder does not exist", Storage.describe(new NoSuchFileException("f")));
        assertEquals("the system said: Is a directory",
                Storage.describe(new FileSystemException("f", null, "Is a directory")));
    }

    @Test
    public void describe_problemWithoutReason_fallsBackToMessage() {
        assertEquals("the system said: disk full", Storage.describe(new IOException("disk full")));
        // A FileSystemException with no reason must not produce "the system said: null".
        assertFalse(Storage.describe(new FileSystemException("f")).endsWith("null"));
    }

    @Test
    public void loadThenSave_unreadableFile_loadFailsAndSaveRefused() throws Exception {
        writeFile("T | 0 | precious");
        Path file = tempDir.resolve("harvey.txt");
        makeUnreadable(file);
        Storage storage = storage();

        try {
            HarveyException loadError = assertThrows(HarveyException.class, storage::load);
            assertTrue(loadError.getMessage().contains("permission was denied"), loadError.getMessage());

            // The copy fails too, since it has to read the file. Saving must then be refused,
            // or the task Harvey could not read would be erased.
            assertNull(storage.getBackupPath());
            assertFalse(storage.isOverwriteSafe());
            HarveyException saveError = assertThrows(HarveyException.class, () ->
                    storage.save(new ArrayList<>()));
            assertTrue(saveError.getMessage().contains("won't save"), saveError.getMessage());
        } finally {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-r--r--"));
        }
        assertEquals(List.of("T | 0 | precious"), Files.readAllLines(file));
    }

    /**
     * Removes every permission from a file, skipping the test where that cannot work.
     * <p>
     * Windows has no POSIX permissions, and an administrator (root) can read any file
     * regardless of them. In either case the test is skipped rather than failed, because
     * the behavior it checks cannot be set up there. CI runs it on Linux and macOS.
     */
    private static void makeUnreadable(Path file) throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"),
                "needs POSIX file permissions");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("---------"));
        assumeFalse(Files.isReadable(file), "running as a user who can read any file");
    }
}
