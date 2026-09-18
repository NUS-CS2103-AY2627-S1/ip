package hermes.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hermes.HermesException;

/**
 * Tests reading tasks from, and writing them to, the data file.
 *
 * <p>Every case works in a directory JUnit creates and removes, so the real
 * data/Hermes.txt is never read or overwritten.
 */
public class StorageTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 27, 15, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 27, 16, 30);
    private static final String SEPARATOR_MESSAGE = "Forgive me, but a task may not contain '|', "
            + "for I use that mark to divide the fields of thy scroll when I save it.";

    @TempDir
    Path tempDir;

    @Test
    public void load_missingFileInMissingDirectory_returnsNothingAndCreatesDirectory() {
        Path file = tempDir.resolve("nested").resolve("Hermes.txt");
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
        assertTrue(Files.isDirectory(file.getParent()));
        assertEquals(0, storage.getSkippedLines());
    }

    @Test
    public void save_eachTaskType_writesOneLinePerTask() throws HermesException, IOException {
        Path file = tempDir.resolve("Hermes.txt");

        new Storage(file.toString()).save(List.of(
                new ToDo(true, "read"),
                new Deadline("essay", START),
                new Event("meeting", START, END)));

        assertEquals(List.of(
                "T | 1 | read",
                "D | 0 | essay | 2026-08-27T15:00",
                "E | 0 | meeting | 2026-08-27T15:00 | 2026-08-27T16:30"),
                Files.readAllLines(file));
    }

    @Test
    public void saveThenLoad_eachTaskType_tasksReadBackUnchanged() throws HermesException {
        String path = tempDir.resolve("Hermes.txt").toString();
        List<Task> saved = List.of(
                new ToDo(true, "read"),
                new Deadline("essay", START),
                new Event("meeting", START, END));

        new Storage(path).save(saved);
        ArrayList<Task> loaded = new Storage(path).load();

        assertEquals(saved.size(), loaded.size());
        for (int i = 0; i < saved.size(); i++) {
            assertEquals(saved.get(i).getFileContent(), loaded.get(i).getFileContent());
            assertEquals(saved.get(i).toString(), loaded.get(i).toString());
        }
    }

    @Test
    public void load_unreadableLines_skippedAndCountedWhileReadableOnesLoad() throws IOException {
        Path file = tempDir.resolve("Hermes.txt");
        Files.write(file, List.of(
                "T | 0 | first good",
                "X | 0 | unknown type letter",
                "T | 0",
                "T | 0 | too | many",
                "T | 2 | completion flag is not 0 or 1",
                "D | 0 | date that cannot be read | tomorrow",
                "E | 0 |   | 2026-08-27T15:00 | 2026-08-27T16:30",
                "",
                "E | 1 | last good | 2026-08-27T15:00 | 2026-08-27T16:30"));
        Storage storage = new Storage(file.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals(7, storage.getSkippedLines());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first good", tasks.get(0).toString());
        assertEquals("[E][X] last good (from: 27/08/2026 1500 to: 27/08/2026 1630)",
                tasks.get(1).toString());
    }

    @Test
    public void load_calledAgainAfterFileRepaired_skippedCountStartsAfresh() throws IOException {
        Path file = tempDir.resolve("Hermes.txt");
        Files.write(file, List.of("rubbish", "T | 0 | good"));
        Storage storage = new Storage(file.toString());
        storage.load();

        Files.write(file, List.of("T | 0 | good"));
        storage.load();

        assertEquals(0, storage.getSkippedLines());
    }

    @Test
    public void load_fileCannotBeOpened_returnsNothingAndReportsIt() throws IOException {
        // A folder where the file should be cannot be opened on any system.
        Path file = Files.createDirectory(tempDir.resolve("Hermes.txt"));
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
        assertTrue(storage.isFileUnreadable());
    }

    @Test
    public void save_afterFileCouldNotBeOpened_leavesFileUntouched() throws IOException {
        Path file = tempDir.resolve("Hermes.txt");
        Files.write(file, List.of("T | 0 | kept safe"));
        // Some systems ignore this permission, so the test is skipped there.
        assumeTrue(file.toFile().setReadable(false) && !Files.isReadable(file));
        Storage storage = new Storage(file.toString());

        storage.load();

        assertThrows(HermesException.class, () -> storage.save(List.of(new ToDo("read"))));
        file.toFile().setReadable(true);
        assertEquals(List.of("T | 0 | kept safe"), Files.readAllLines(file));
    }

    @Test
    public void save_pathIsADirectory_throwsExceptionNamingThePath() {
        Storage storage = new Storage(tempDir.toString());

        HermesException exception = assertThrows(
                HermesException.class, () -> storage.save(List.of(new ToDo("read"))));

        assertEquals("Alas, I could not write to " + tempDir + ".\n"
                + "Thy change holds for now, but it shall be lost\n"
                + "when I depart.\n", exception.getMessage());
    }

    @Test
    public void rejectSeparator_fieldsWithoutSeparator_accepted() {
        assertDoesNotThrow(() -> Storage.rejectSeparator("read book", "27 Aug 2026 1500"));
    }

    @Test
    public void rejectSeparator_anyFieldHasSeparator_throwsException() {
        HermesException first = assertThrows(HermesException.class, () -> Storage.rejectSeparator("a | b"));
        HermesException later = assertThrows(
                HermesException.class, () -> Storage.rejectSeparator("fine", "also fine", "not|fine"));

        assertEquals(SEPARATOR_MESSAGE, first.getMessage());
        assertEquals(SEPARATOR_MESSAGE, later.getMessage());
    }
}
