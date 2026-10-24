package shannon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shannon.exception.StorageException;
import shannon.task.Deadline;
import shannon.task.Event;
import shannon.task.EventTime;
import shannon.task.Task;
import shannon.task.Todo;

/** Tests persistence and recovery using isolated temporary files. */
class StorageTest {
    @TempDir
    Path directory;

    @Test
    void load_missingFile_returnsEmptyListWithoutCreatingFile() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        assertTrue(storage.load().isEmpty());
        assertFalse(Files.exists(file));
        assertEquals(0, storage.getSkippedLineCount());
        assertEquals("", storage.getBackupFilePath());
        assertFalse(storage.isSaveBlocked());
    }

    @Test
    void save_allTaskTypesAndEscapedText_roundTripsAndTruncates() throws Exception {
        Storage storage = new Storage(directory.resolve("nested/tasks.txt").toString());
        Todo todo = new Todo("Read | notes\\draft\nnext\rline");
        todo.markDone();
        List<Task> tasks = List.of(todo, new Deadline("Report", LocalDate.of(2026, 8, 9)),
                new Event("Meeting", EventTime.parse("2026-08-09 9:00"),
                        EventTime.parse("2026-08-09 10:00")));
        storage.save(tasks);
        List<Task> loaded = storage.load();
        assertEquals(tasks.stream().map(Task::toFileFormat).toList(),
                loaded.stream().map(Task::toFileFormat).toList());
        assertEquals(todo.getDescription(), loaded.getFirst().getDescription());
        storage.save(List.of());
        assertEquals("", Files.readString(Path.of(storage.getFilePath())));
    }

    @Test
    void load_corruptLines_preservesValidTasksAndBacksUpOriginal() throws Exception {
        Path file = directory.resolve("tasks.txt");
        String original = String.join("\n", "T | 1 | Keep", "", "broken", "T | 2 | bad flag",
                "T | 0 | ", "X | 0 | unknown", "T | 0 | extra | field", "D | 0 | missing date",
                "D | 0 | invalid | 2026-02-30", "E | 0 | missing time | 2026-08-09",
                "E | 0 | reversed | 2026-08-10 | 2026-08-09");
        Files.writeString(file, original);
        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();
        assertEquals(List.of("T | 1 | Keep"), loaded.stream().map(Task::toFileFormat).toList());
        assertEquals(9, storage.getSkippedLineCount());
        assertEquals(original, Files.readString(Path.of(storage.getBackupFilePath())));
        assertFalse(storage.isSaveBlocked());
        storage.save(loaded);
        storage.load();
        assertEquals(0, storage.getSkippedLineCount());
        assertEquals("", storage.getBackupFilePath());
        assertEquals(original, Files.readString(directory.resolve("tasks.txt.bak")));
    }

    @Test
    void load_failedBackup_blocksDestructiveSaveUntilSuccessfulReload() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "damaged");
        Path backup = Files.createDirectory(directory.resolve("tasks.txt.bak"));
        Files.writeString(backup.resolve("keep.txt"), "prevents replacing a nonempty directory");
        Storage storage = new Storage(file.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.isSaveBlocked());
        assertEquals("", storage.getBackupFilePath());
        assertThrows(StorageException.class, () -> storage.save(List.of(new Todo("New task"))));
        assertEquals("damaged", Files.readString(file));
        Files.writeString(file, "T | 0 | repaired");
        assertEquals(1, storage.load().size());
        assertFalse(storage.isSaveBlocked());
    }

    @Test
    void load_invalidUtf8_backsUpBytesAndReportsError() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.write(file, new byte[] {(byte) 0xc3, (byte) 0x28});
        Storage storage = new Storage(file.toString());
        assertThrows(StorageException.class, storage::load);
        assertEquals(-1L, Files.mismatch(file, Path.of(storage.getBackupFilePath())));
    }

    @Test
    void load_directoryAndSaveBlockedParent_reportStorageErrors() throws Exception {
        Storage directoryStorage = new Storage(directory.toString());
        assertThrows(StorageException.class, directoryStorage::load);
        Path parentFile = directory.resolve("file");
        Files.writeString(parentFile, "keep");
        Storage storage = new Storage(parentFile.resolve("tasks.txt").toString());
        assertThrows(StorageException.class, () -> storage.save(List.of(new Todo("Read"))));
        assertEquals("keep", Files.readString(parentFile));
    }
}
