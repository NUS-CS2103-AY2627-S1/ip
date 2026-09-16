package serangooner.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import serangooner.SerangoonerException;
import serangooner.task.Deadline;
import serangooner.task.Event;
import serangooner.task.Task;
import serangooner.task.Todo;

public class StorageTest {
    @Test
    public void load_missingFile_returnsEmptyResult(@TempDir Path directory) {
        Storage.LoadResult result = new Storage(directory.resolve("absent.txt")).load();
        assertEquals(List.of(), result.tasks());
        assertEquals(0, result.skippedLineCount());
        assertEquals("", result.errorMessage());
    }

    @Test
    public void saveThenLoad_everyTaskType_readsBackTheSameList(@TempDir Path directory) {
        Storage storage = new Storage(directory.resolve("serangooner.txt"));
        Task todo = new Todo("read book");
        Task deadline = new Deadline("submit ip", "2026-09-01");
        Task event = new Event("orbital demo", "2026-09-05 1400", "2026-09-05 1600");
        deadline.markDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load().tasks();

        assertEquals(3, loaded.size());
        assertEquals(todo.toString(), loaded.get(0).toString());
        assertEquals(deadline.toString(), loaded.get(1).toString());
        assertEquals(event.toString(), loaded.get(2).toString());
        assertTrue(loaded.get(1).isDone());
        assertFalse(loaded.get(0).isDone());
    }

    @Test
    public void save_missingParentDirectory_createsIt(@TempDir Path directory) {
        Path file = directory.resolve("data").resolve("serangooner.txt");
        new Storage(file).save(List.of(new Todo("read book")));
        assertTrue(Files.exists(file));
    }

    @Test
    public void save_emptyList_clearsTheFile(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.write(file, List.of("T | 0 | read book"));

        new Storage(file).save(List.of());

        assertEquals(List.of(), Files.readAllLines(file));
    }

    @Test
    public void load_unreadableLines_skipsThemAndCountsThem(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.write(file, List.of(
                "T | 0 | read book",
                "X | 0 | unknown type code",
                "T | 7 | bad done flag",
                "T | 0",
                "D | 0 | no date given",
                "D | 0 | unparseable date | not-a-date",
                "E | 0 | ends first | 2026-09-05 | 2026-09-04",
                "bogus"));

        Storage.LoadResult result = new Storage(file).load();

        assertEquals(1, result.tasks().size());
        assertEquals(7, result.skippedLineCount());
        assertEquals("", result.errorMessage());
    }

    @Test
    public void load_blankLines_ignoresThemWithoutCounting(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.write(file, List.of("", "T | 0 | read book", "   ", ""));

        Storage.LoadResult result = new Storage(file).load();

        assertEquals(1, result.tasks().size());
        assertEquals(0, result.skippedLineCount());
    }

    @Test
    public void load_fileThatCannotBeRead_reportsReasonInsteadOfThrowing(@TempDir Path directory)
            throws IOException {
        // A directory sitting where the save file should be cannot be read as lines.
        Path file = directory.resolve("serangooner.txt");
        Files.createDirectory(file);

        Storage.LoadResult result = new Storage(file).load();

        assertEquals(List.of(), result.tasks());
        assertEquals(0, result.skippedLineCount());
        assertTrue(result.errorMessage().contains("couldn't read"));
    }

    @Test
    public void save_fileThatCannotBeWritten_exceptionThrown(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.createDirectory(file);

        Storage storage = new Storage(file);
        assertThrows(SerangoonerException.class, () -> storage.save(List.of(new Todo("read book"))));
    }

    @Test
    public void save_fileWithExistingTasks_replacesThemInsteadOfAppending(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Storage storage = new Storage(file);
        storage.save(List.of(new Todo("read book"), new Todo("water plants")));

        storage.save(List.of(new Todo("call mum")));

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("call mum"));
    }

    @Test
    public void saveThenLoad_everyTaskTypeDone_restoresTheDoneState(@TempDir Path directory) {
        Storage storage = new Storage(directory.resolve("serangooner.txt"));
        Task todo = new Todo("read book");
        Task deadline = new Deadline("submit ip", "2026-09-01");
        Task event = new Event("orbital demo", "2026-09-05 1400", "2026-09-05 1600");
        todo.markDone();
        deadline.markDone();
        event.markDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load().tasks();

        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0).isDone());
        assertTrue(loaded.get(1).isDone());
        assertTrue(loaded.get(2).isDone());
    }

    @Test
    public void load_lineWithTooManyFields_skipsIt(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.write(file, List.of("T | 0 | read book | and one field too many"));

        Storage.LoadResult result = new Storage(file).load();

        assertEquals(List.of(), result.tasks());
        assertEquals(1, result.skippedLineCount());
    }

    @Test
    public void load_lineOfDelimitersOnly_skipsItInsteadOfThrowing(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.write(file, List.of(" | ", "T | 0 | read book", " |  | "));

        Storage.LoadResult result = new Storage(file).load();

        assertEquals(1, result.tasks().size());
        assertEquals(2, result.skippedLineCount());
    }

    @Test
    public void load_everyLineReadable_makesNoBackup(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.write(file, List.of("T | 0 | read book"));

        Storage.LoadResult result = new Storage(file).load();

        assertEquals("", result.backupMessage());
        assertFalse(Files.exists(directory.resolve("serangooner.txt.bak")));
    }

    @Test
    public void load_unreadableLines_backsUpTheOriginalBeforeItIsSavedOver(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("serangooner.txt");
        List<String> original = List.of("T | 0 | read book", "T | 7 | bad done flag");
        Files.write(file, original);
        Storage storage = new Storage(file);

        Storage.LoadResult result = storage.load();
        storage.save(result.tasks());

        Path backup = directory.resolve("serangooner.txt.bak");
        assertEquals(original, Files.readAllLines(backup));
        assertTrue(result.backupMessage().contains(backup.toString()));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(file));
    }

    @Test
    public void load_fileNotValidText_backsItUpByteForByte(@TempDir Path directory)
            throws IOException {
        // Bytes that are not valid UTF-8 make the whole file unreadable as lines.
        Path file = directory.resolve("serangooner.txt");
        byte[] original = {(byte) 0xC3, (byte) 0x28, '\n'};
        Files.write(file, original);

        Storage.LoadResult result = new Storage(file).load();

        assertTrue(result.errorMessage().contains("couldn't read"));
        assertArrayEquals(original, Files.readAllBytes(directory.resolve("serangooner.txt.bak")));
    }

    @Test
    public void save_afterBackupFailed_exceptionThrownAndFileLeftUntouched(@TempDir Path directory)
            throws IOException {
        Path file = directory.resolve("serangooner.txt");
        List<String> original = List.of("T | 7 | bad done flag");
        Files.write(file, original);
        // A non-empty directory where the backup should go cannot be replaced by a copy.
        Path backup = Files.createDirectory(directory.resolve("serangooner.txt.bak"));
        Files.write(backup.resolve("blocker.txt"), List.of("in the way"));
        Storage storage = new Storage(file);

        Storage.LoadResult result = storage.load();

        assertTrue(result.backupMessage().contains("couldn't back it up"));
        assertThrows(SerangoonerException.class, () -> storage.save(List.of(new Todo("read book"))));
        assertEquals(original, Files.readAllLines(file));
    }

    @Test
    public void load_emptyFile_returnsEmptyResult(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("serangooner.txt");
        Files.write(file, List.of());

        Storage.LoadResult result = new Storage(file).load();

        assertEquals(List.of(), result.tasks());
        assertEquals(0, result.skippedLineCount());
        assertEquals("", result.errorMessage());
    }

    @Test
    public void saveThenLoad_manyTasks_keepsThemInTheOrderTheyWereWritten(
            @TempDir Path directory) {
        Storage storage = new Storage(directory.resolve("serangooner.txt"));
        storage.save(List.of(new Todo("first"), new Todo("second"), new Todo("third")));

        List<Task> loaded = storage.load().tasks();

        assertEquals("[T][ ] first", loaded.get(0).toString());
        assertEquals("[T][ ] second", loaded.get(1).toString());
        assertEquals("[T][ ] third", loaded.get(2).toString());
    }

    @Test
    public void saveThenLoad_chineseDescription_readsItBackUnchanged(@TempDir Path directory) {
        Storage storage = new Storage(directory.resolve("serangooner.txt"));
        storage.save(List.of(new Todo("买菜"), new Deadline("交报告", "2026-09-01")));

        List<Task> loaded = storage.load().tasks();

        assertEquals("[T][ ] 买菜", loaded.get(0).toString());
        assertEquals("[D][ ] 交报告 (by: 01 Sep 2026)", loaded.get(1).toString());
    }

    @Test
    public void load_windowsLineEndings_readsEveryTaskWithoutStrayCharacters(@TempDir Path directory)
            throws IOException {
        // A save file copied from Windows ends each line with a carriage return as well.
        Path file = directory.resolve("serangooner.txt");
        Files.writeString(file, "T | 0 | read book\r\nD | 1 | submit ip | 2026-09-01\r\n");

        Storage.LoadResult result = new Storage(file).load();

        assertEquals(0, result.skippedLineCount());
        assertEquals("[T][ ] read book", result.tasks().get(0).toString());
        assertEquals("[D][✓] submit ip (by: 01 Sep 2026)", result.tasks().get(1).toString());
    }
}
