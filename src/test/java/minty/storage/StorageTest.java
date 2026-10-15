package minty.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import minty.exception.MintyException;
import minty.task.Deadline;
import minty.task.Event;
import minty.task.Task;
import minty.task.Todo;

/**
 * Tests saving and loading task data without touching the project's real data file.
 */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_legacyDuplicates_preservesEveryTaskAndAllowsSaving() throws IOException, MintyException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | same\nT | 1 | same\nT | 0 | same\nD | 0 | work | 2026-09-20");
        Storage storage = new Storage(file);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(4, tasks.size());
        assertEquals("T | 0 | same", tasks.get(0).toDataString());
        assertEquals("T | 1 | same", tasks.get(1).toDataString());
        assertEquals("T | 0 | same", tasks.get(2).toDataString());
        storage.saveTasks(tasks);
        assertEquals(tasks.stream().map(Task::toDataString).toList(),
                new Storage(file).loadTasks().stream().map(Task::toDataString).toList());
    }

    @Test
    public void saveTasks_readOnlyFile_reportsErrorAndPreservesData() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | keep");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                Files.getFileStore(file).supportsFileAttributeView("posix"));
        Set<PosixFilePermission> originalPermissions = Files.getPosixFilePermissions(file);
        try {
            Files.setPosixFilePermissions(file, Set.of(PosixFilePermission.OWNER_READ));
            org.junit.jupiter.api.Assumptions.assumeFalse(Files.isWritable(file));
            assertThrows(IOException.class, () -> new Storage(file).saveTasks(List.of(new Todo("new"))));
            assertEquals("T | 0 | keep", Files.readString(file));
        } finally {
            Files.setPosixFilePermissions(file, originalPermissions);
        }
    }

    @Test
    public void loadTasks_permissionDenied_blocksSaving() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | keep");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                Files.getFileStore(file).supportsFileAttributeView("posix"));
        Set<PosixFilePermission> originalPermissions = Files.getPosixFilePermissions(file);
        Storage storage = new Storage(file);
        try {
            Files.setPosixFilePermissions(file, Set.of());
            org.junit.jupiter.api.Assumptions.assumeFalse(Files.isReadable(file));
            assertThrows(IOException.class, storage::loadTasks);
        } finally {
            Files.setPosixFilePermissions(file, originalPermissions);
        }
        assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("new"))));
        assertEquals("T | 0 | keep", Files.readString(file));
    }

    @Test
    public void loadTasks_badData_blocksLaterWritesAndPreservesOriginal() throws IOException {
        for (String original : new String[] {"bad data", "T | 2 | invalid status"}) {
            Path file = temporaryDirectory.resolve("tasks.txt");
            Files.writeString(file, original);
            Storage storage = new Storage(file);
            assertThrows(MintyException.class, storage::loadTasks);
            assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("new"))));
            assertEquals(original, Files.readString(file));
        }
    }

    @Test
    public void saveTasks_replacesExistingFileAndCleansTemporaryFile() throws IOException, MintyException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("first")));
        storage.saveTasks(List.of(new Todo("second")));
        assertEquals("T | 0 | second", storage.loadTasks().getFirst().toDataString());
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    public void loadTasks_invalidUtf8_blocksSaving() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        byte[] bytes = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, bytes);
        Storage storage = new Storage(file);
        assertThrows(IOException.class, storage::loadTasks);
        assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("new"))));
        assertEquals(2, Files.size(file));
    }

    @Test
    public void saveTasks_directoryTarget_preservesContents() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(file);
        Path child = file.resolve("keep.txt");
        Files.writeString(child, "keep");
        assertThrows(IOException.class, () -> new Storage(file).saveTasks(List.of(new Todo("new"))));
        assertEquals("keep", Files.readString(child));
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws IOException, MintyException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesTaskData()
            throws IOException, MintyException {
        Path file = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("read | review \\ notes");
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 9, 10));
        deadline.markAsDone();
        Event event = new Event("conference", LocalDate.of(2026, 9, 11),
                LocalDate.of(2026, 9, 12));
        ArrayList<Task> originalTasks = new ArrayList<>(List.of(todo, deadline, event));

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(originalTasks.stream().map(Task::toDataString).toList(),
                loadedTasks.stream().map(Task::toDataString).toList());
    }

    @Test
    public void loadTasks_blankLines_ignoresBlankLines() throws IOException, MintyException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("", "T | 0 | read book", "   "));

        ArrayList<Task> tasks = new Storage(file).loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void loadTasks_unknownTaskType_throwsMintyExceptionWithLineNumber()
            throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("T | 0 | valid", "X | 0 | invalid"));

        MintyException exception = assertThrows(MintyException.class, () -> new Storage(file).loadTasks());

        assertEquals("I couldn't read the saved task on line 2: unknown task type 'X'.",
                exception.getMessage());
    }

    @Test
    public void loadTasks_invalidStatus_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 2 | read book");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }

    @Test
    public void loadTasks_invalidDate_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | submit | 10-09-2026");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }

    @Test
    public void loadTasks_eventEndBeforeStart_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "E | 0 | conference | 2026-09-12 | 2026-09-10");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }

    @Test
    public void loadTasks_unsupportedEscapeSequence_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | invalid\\qdescription");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }
}
