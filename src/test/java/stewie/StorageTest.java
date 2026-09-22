package stewie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import stewie.model.Deadline;
import stewie.model.Task;
import stewie.model.TaskList;
import stewie.model.ToDo;
import stewie.storage.Storage;
import stewie.storage.StorageException;

/**
 * Verifies storage recovery and preservation of both disk and memory on failed writes.
 */
public class StorageTest {
    @TempDir
    private Path directory;

    /**
     * Verifies missing directories are created and every task type survives a restart in another locale.
     */
    @Test
    public void saveTasks_roundTripsAcrossLocales() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRANCE);
            Path file = directory.resolve("data/stewie.txt");
            TaskList tasks = new TaskList(new Storage(file));
            tasks.addToDo("read book");
            tasks.addDeadline("report", "2026-01-01");
            tasks.addEvent("trip", "2026-01-01", "2026-01-02");
            tasks.markAsDone(1);
            TaskList restored = new TaskList(new Storage(file));
            assertArrayEquals(tasks.produceTaskList(), restored.produceTaskList());
            assertEquals("", restored.getLoadWarning());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    /**
     * Verifies damaged records do not hide later valid records or allow overwriting the original file.
     */
    @Test
    public void loadTasks_preservesDamagedFile() throws IOException {
        Path file = directory.resolve("stewie.txt");
        String content = "T | 0 | first\nD | 0 | invalid | 2026-02-30\nT | 1 | last\n";
        Files.writeString(file, content);
        TaskList tasks = new TaskList(new Storage(file));
        assertEquals(2, tasks.produceTaskList().length);
        assertTrue(tasks.getLoadWarning().contains("lines 2"));
        assertThrows(StorageException.class, () -> tasks.addToDo("new"));
        assertEquals(content, Files.readString(file));
    }

    /**
     * Verifies external edits block every kind of mutation without altering memory or the edited file.
     */
    @Test
    public void saveTasks_protectsExternalChanges() throws IOException {
        Path file = directory.resolve("stewie.txt");
        TaskList tasks = new TaskList(new Storage(file));
        tasks.addToDo("first");
        tasks.addToDo("second");
        tasks.markAsDone(1);
        String[] before = tasks.produceTaskList();
        String external = "T | 0 | external edit\n";
        Files.writeString(file, external);
        assertThrows(StorageException.class, () -> tasks.addToDo("new"));
        assertThrows(StorageException.class, () -> tasks.markAsDone(0));
        assertThrows(StorageException.class, () -> tasks.markAsUndone(1));
        assertThrows(StorageException.class, () -> tasks.deleteTask(0));
        assertThrows(StorageException.class, () -> tasks.updateTask(0, "revised"));
        assertArrayEquals(before, tasks.produceTaskList());
        assertEquals(external, Files.readString(file));
    }

    /**
     * Verifies denied saves can be retried after permissions are restored, without losing prior data.
     */
    @Test
    public void saveTasks_recoversAfterDeniedWrite() throws IOException {
        Path file = directory.resolve("stewie.txt");
        TaskList tasks = new TaskList(new Storage(file));
        tasks.addToDo("first");
        byte[] before = Files.readAllBytes(file);
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView("posix"), "Requires POSIX permissions");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("r--r--r--"));
        try {
            assumeTrue(!Files.isWritable(file), "Requires a user subject to file permissions");
            assertThrows(StorageException.class, () -> tasks.deleteTask(0));
            assertEquals(1, tasks.produceTaskList().length);
            assertArrayEquals(before, Files.readAllBytes(file));
        } finally {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
        }
        tasks.addToDo("second");
        assertEquals(2, new TaskList(new Storage(file)).produceTaskList().length);
    }

    /**
     * Verifies a failed temporary write leaves existing content intact and can be retried.
     */
    @Test
    public void saveTasks_preservesFileWhenDirectoryIsUnwritable() throws IOException {
        Path data = Files.createDirectory(directory.resolve("data"));
        Path file = data.resolve("stewie.txt");
        TaskList tasks = new TaskList(new Storage(file));
        tasks.addToDo("first");
        byte[] before = Files.readAllBytes(file);
        assumeTrue(Files.getFileStore(data).supportsFileAttributeView("posix"), "Requires POSIX permissions");
        Files.setPosixFilePermissions(data, PosixFilePermissions.fromString("r-x------"));
        try {
            assumeTrue(!Files.isWritable(data), "Requires a user subject to directory permissions");
            assertThrows(StorageException.class, () -> tasks.addToDo("second"));
            assertArrayEquals(before, Files.readAllBytes(file));
            assertEquals(1, tasks.produceTaskList().length);
        } finally {
            Files.setPosixFilePermissions(data, PosixFilePermissions.fromString("rwx------"));
        }
        tasks.addToDo("second");
        try (var files = Files.list(data)) {
            assertEquals(1, files.count());
        }
    }

    /**
     * Verifies invalid bytes cannot be overwritten as apparently empty data.
     */
    @Test
    public void loadTasks_protectsUnreadableSources() throws IOException {
        Path file = directory.resolve("stewie.txt");
        byte[] invalidBytes = {(byte) 0xff};
        Files.write(file, invalidBytes);
        TaskList tasks = new TaskList(new Storage(file));
        assertFalse(tasks.getLoadWarning().isEmpty());
        assertThrows(StorageException.class, () -> tasks.addToDo("new"));
        assertArrayEquals(invalidBytes, Files.readAllBytes(file));
    }

    /**
     * Verifies symbolic links cannot redirect reads or writes to another task file.
     */
    @Test
    public void loadTasks_protectsSymbolicLinks() throws IOException {
        assumeTrue(Files.getFileStore(directory).supportsFileAttributeView("posix"),
                "Symbolic-link checks require a POSIX test environment");
        Path file = directory.resolve("stewie.txt");
        Files.writeString(file, "T | 0 | original");
        Path link = directory.resolve("link.txt");
        Files.createSymbolicLink(link, file);
        Storage storage = new Storage(link);
        storage.loadFromDisk();
        assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>()));
        assertTrue(Files.isSymbolicLink(link));
        assertEquals("T | 0 | original", Files.readString(file));
    }

    /**
     * Verifies duplicate records, invalid statuses, field counts, and event ranges are diagnosed individually.
     */
    @Test
    public void loadTasks_validatesEveryRecord() throws IOException {
        Path file = directory.resolve("stewie.txt");
        Files.writeString(file, "T | 0 | first\nT | 1 | FIRST\nT | yes | status\nT | 0 | extra | field\n"
                + "E | 0 | trip | 2026-01-02 | 2026-01-01\nD | 0 | report | 2026-01-03\n");
        Storage storage = new Storage(file);
        ArrayList<Task> tasks = storage.loadFromDisk();
        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).hasSameDetails(new ToDo("first")));
        assertTrue(tasks.get(1).hasSameDetails(new Deadline("report", "2026-01-03")));
        assertTrue(storage.getLoadWarning().contains("lines 2, 3, 4, 5"));
    }
    /**
     * Verifies GUI revisions change only after successful writes and survive rejected actions.
     */
    @Test
    public void taskRevision_changesOnlyAfterSuccessfulSave() throws IOException {
        Path file = directory.resolve("stewie.txt");
        TaskList tasks = new TaskList(new Storage(file));
        assertEquals(0, tasks.getRevision());
        tasks.addToDo("first");
        assertEquals(1, tasks.getRevision());
        tasks.markAsDone(0);
        assertEquals(2, tasks.getRevision());
        assertThrows(IllegalArgumentException.class, () -> tasks.addToDo("first"));
        assertEquals(2, tasks.getRevision());
        Files.writeString(file, "T | 0 | external");
        assertThrows(StorageException.class, () -> tasks.deleteTask(0));
        assertEquals(2, tasks.getRevision());
    }
}
