package kiwi;

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
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.Task;
import kiwi.task.Todo;

/**
 * Unit tests for {@link Storage#save(ArrayList)} and {@link Storage#load()}.
 */
class StorageTest {

    @TempDir
    Path tempDir;

    private Path saveFile;
    private Storage storage;

    @BeforeEach
    void setUp() {
        saveFile = tempDir.resolve("kiwi.txt");
        storage = new Storage(saveFile.toString());
    }

    @Test
    void load_missingFile_returnsEmptyList() {
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypes_roundTripPreservesState() throws KiwiException {
        ArrayList<Task> tasks = new ArrayList<>();

        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);

        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));

        tasks.add(new Event("meeting",
                LocalDate.of(2019, 10, 4),
                LocalDate.of(2019, 10, 11)));

        storage.save(tasks);

        ArrayList<Task> loaded = new Storage(saveFile.toString()).load();
        assertEquals(3, loaded.size());

        Todo loadedTodo = (Todo) loaded.get(0);
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = (Deadline) loaded.get(1);
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("meeting", loaded.get(2).getDescription());
    }

    @Test
    void load_blankLinesAndCorruptedLines_skipsBadLinesAndLoadsValidOnes() throws IOException {
        Files.writeString(saveFile, """
                T | 0 | valid todo

                not a valid line
                D | 0 | return book | 2019-12-02
                """);

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
    }

    @Test
    void load_doneFlagRestoredForEachTaskType() throws IOException, KiwiException {
        Files.writeString(saveFile, """
                T | 1 | done todo
                D | 1 | done deadline | 2019-12-02
                E | 0 | open event | 2019-10-04 | 2019-10-11
                """);

        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.get(0).isDone());
        assertTrue(loaded.get(1).isDone());
        assertFalse(loaded.get(2).isDone());
    }

    @Test
    void save_emptyList_writesEmptyFile() throws KiwiException {
        storage.save(new ArrayList<>());

        ArrayList<Task> loaded = new Storage(saveFile.toString()).load();
        assertTrue(loaded.isEmpty());
        assertTrue(Files.exists(saveFile));
    }

    @Test
    void save_pathIsDirectory_throwsKiwiException() throws IOException {
        Path directory = tempDir.resolve("notAFile");
        Files.createDirectory(directory);
        Storage directoryStorage = new Storage(directory.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        KiwiException exception = assertThrows(KiwiException.class, () -> directoryStorage.save(tasks));
        assertTrue(exception.getMessage().contains("folder, not a file"));
    }

    @Test
    void load_pathIsDirectory_returnsEmptyListAndWarns() throws IOException {
        Path directory = tempDir.resolve("notAFile");
        Files.createDirectory(directory);
        Storage directoryStorage = new Storage(directory.toString());

        assertTrue(directoryStorage.load().isEmpty());
        assertEquals(1, directoryStorage.getLoadWarnings().size());
        assertTrue(directoryStorage.getLoadWarnings().get(0).contains("folder, not a file"));
    }

    @Test
    void load_duplicateLines_skipsLaterDuplicate() throws IOException {
        Files.writeString(saveFile, """
                T | 0 | read book
                T | 1 | read book
                D | 0 | return book | 2019-12-02
                """);

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertFalse(loaded.get(0).isDone());
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertTrue(storage.getLoadWarnings().get(0).contains("duplicated save line 2"));
    }

    @Test
    void load_eventEndBeforeStart_skipsCorruptedLine() throws IOException {
        Files.writeString(saveFile, """
                E | 0 | meeting | 2019-10-11 | 2019-10-04
                T | 0 | keep me
                """);

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("keep me", loaded.get(0).getDescription());
        assertTrue(storage.getLoadWarnings().get(0).contains("end date cannot be before"));
    }

    @Test
    void load_invalidCalendarDate_skipsCorruptedLine() throws IOException {
        Files.writeString(saveFile, "D | 0 | return book | 2019-02-30\n");

        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertTrue(storage.getLoadWarnings().get(0).contains("2019-02-30 is not a real calendar date"));
    }

    @Test
    void load_unreadableFile_returnsEmptyListAndWarns() throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
        Files.writeString(saveFile, "T | 0 | secret");
        Set<PosixFilePermission> original = Files.getPosixFilePermissions(saveFile);
        try {
            Files.setPosixFilePermissions(saveFile, Set.of());
            ArrayList<Task> loaded = storage.load();
            assertTrue(loaded.isEmpty());
            assertFalse(storage.getLoadWarnings().isEmpty());
        } finally {
            Files.setPosixFilePermissions(saveFile, original);
        }
    }

    @Test
    void save_unwritableDirectory_throwsKiwiException() throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
        Path lockedDir = tempDir.resolve("locked");
        Files.createDirectory(lockedDir);
        Set<PosixFilePermission> original = Files.getPosixFilePermissions(lockedDir);
        Storage lockedStorage = new Storage(lockedDir.resolve("kiwi.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        try {
            Files.setPosixFilePermissions(lockedDir, Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_EXECUTE));
            KiwiException exception = assertThrows(KiwiException.class, () -> lockedStorage.save(tasks));
            assertTrue(exception.getMessage().contains("Could not save tasks"));
        } finally {
            Files.setPosixFilePermissions(lockedDir, original);
        }
    }
}
