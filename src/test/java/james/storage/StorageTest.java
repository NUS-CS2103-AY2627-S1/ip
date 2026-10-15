package james.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import james.task.Deadline;
import james.task.Event;
import james.task.Task;
import james.task.TaskList;
import james.task.ToDo;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    @Test
    public void save_writerLockHeld_preservesFileAndAllowsRetry() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        String original = "T | 0 | keep\n";
        Files.writeString(file, original);
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList(storage.load());
        tasks.addTask(new ToDo("new task"));
        try (FileChannel channel = FileChannel.open(tempDir.resolve("tasks.txt.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock lock = channel.lock()) {
            assertTrue(lock.isValid());
            assertFalse(storage.save(tasks));
            assertTrue(storage.wasSaveLocked());
            assertEquals(original, Files.readString(file));
        }
        assertTrue(storage.save(tasks));
        assertFalse(storage.wasSaveLocked());
        assertEquals(original + "T | 0 | new task\n", Files.readString(file));
    }

    @Test
    public void load_controlCharacters_preservesLegacyPipesAndBlocksSaving() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        String original = "T | 0 | safe | legacy\nT | 0 | unsafe" + (char) 27 + "[2Jrecord\n";
        Files.writeString(file, original);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("safe | legacy", tasks.get(0).getDescription());
        assertTrue(storage.getLoadWarning().startsWith("Warning: Skipping invalid saved task at line 2.\n"));
        assertFalse(storage.getLoadWarning().contains(String.valueOf((char) 27)));
        assertTrue(storage.hasLoadErrors());
        assertFalse(storage.save(new TaskList(tasks)));
        assertEquals(original, Files.readString(file));
    }

    /**
     * Tests that load returns an empty ArrayList when the storage file does not exist.
     */
    @Test
    public void load_nonExistentFile_returnsEmptyList() {
        Path nonExistentPath = tempDir.resolve("non_existent_data.txt");
        Storage storage = new Storage(nonExistentPath.toString());

        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    /**
     * Tests that load correctly parses tasks of each type from an existing valid storage file.
     */
    @Test
    public void load_validFileWithTasks_returnsParsedTaskList() throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        List<String> lines = List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-06-06",
                "E | 0 | orientation | 2026-08-06 | 2026-08-08"
        );
        Files.write(filePath, lines);

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertInstanceOf(ToDo.class, tasks.get(0));
        assertEquals("read book", tasks.get(0).getDescription());

        assertInstanceOf(Deadline.class, tasks.get(1));
        Deadline deadline = (Deadline) tasks.get(1);
        assertEquals("return book", deadline.getDescription());
        assertTrue(deadline.isDone());
        assertEquals(LocalDate.parse("2026-06-06"), deadline.getBy());

        assertInstanceOf(Event.class, tasks.get(2));
        Event event = (Event) tasks.get(2);
        assertEquals("orientation", event.getDescription());
        assertEquals(LocalDate.parse("2026-08-06"), event.getFrom());
        assertEquals(LocalDate.parse("2026-08-08"), event.getTo());
    }

    /**
     * Tests that corrupted or malformed lines are skipped gracefully while valid lines are loaded.
     */
    @Test
    public void load_fileWithCorruptedLines_skipsCorruptedLines() throws IOException {
        Path filePath = tempDir.resolve("corrupted_tasks.txt");
        List<String> lines = List.of(
                "T | 0 | valid todo",
                "INVALID | LINE | FORMAT",
                "D | 0 | bad deadline | not-a-date",
                "T | 1 | another valid todo"
        );
        Files.write(filePath, lines);

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("valid todo", tasks.get(0).getDescription());
        assertEquals("another valid todo", tasks.get(1).getDescription());
    }

    /**
     * Tests that save serializes all tasks in the task list into the storage file in the expected format.
     */
    @Test
    public void save_taskListWithTasks_writesAllTasksToFile() throws IOException {
        Path filePath = tempDir.resolve("saved_tasks.txt");
        Storage storage = new Storage(filePath.toString());

        TaskList taskList = new TaskList();
        ToDo todo = new ToDo("buy milk");
        Deadline deadline = new Deadline("submit essay", LocalDate.parse("2026-12-15"));
        deadline.markDone();
        taskList.addTask(todo);
        taskList.addTask(deadline);

        storage.save(taskList);

        assertTrue(Files.exists(filePath));
        List<String> savedLines = Files.readAllLines(filePath);
        assertEquals(2, savedLines.size());
        assertEquals("T | 0 | buy milk", savedLines.get(0));
        assertEquals("D | 1 | submit essay | 2026-12-15", savedLines.get(1));
    }

    /**
     * Tests that save creates missing parent directories when saving to a nested path.
     */
    @Test
    public void save_nestedNonExistentDirectory_createsDirectoriesAndSaves() throws IOException {
        Path nestedPath = tempDir.resolve("nested").resolve("subfolder").resolve("tasks.txt");
        Storage storage = new Storage(nestedPath.toString());

        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("nested task"));

        storage.save(taskList);

        assertTrue(Files.exists(nestedPath));
        List<String> savedLines = Files.readAllLines(nestedPath);
        assertEquals(1, savedLines.size());
        assertEquals("T | 0 | nested task", savedLines.get(0));
    }
    @Test
    public void save_corruptedLoad_preservesOriginalFile() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        String contents = "T | 0 | keep\nE | 0 | bad | 2026-09-16 | 2026-09-16\n" +
                "T | 1 | keep\nD | 0 | extra | field | 2026-09-16\n";
        Files.writeString(file, contents);
        Storage storage = new Storage(file.toString());
        assertEquals(1, storage.load().size());
        assertFalse(storage.save(new TaskList()));
        assertEquals(contents, Files.readString(file));
    }

    @Test
    public void save_unreadableUtf8_preservesOriginalBytes() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        byte[] contents = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, contents);
        Storage storage = new Storage(file.toString());
        storage.load();
        assertFalse(storage.save(new TaskList()));
        assertEquals(2, Files.size(file));
    }

    @Test
    public void save_directoryAtStoragePath_returnsFalse() {
        Storage storage = new Storage(tempDir.toString());
        assertTrue(storage.load().isEmpty());
        assertFalse(storage.save(new TaskList()));
        assertTrue(Files.isDirectory(tempDir));
    }

    @Test
    public void load_corruptedFile_reportsLineNumbersAndClearsWarningsAfterRepair() throws IOException {
        Path file = tempDir.resolve("warnings.txt");
        Files.writeString(file, "\nbroken record\nT | 0 | valid\ninvalid\n");
        Storage storage = new Storage(file.toString());
        assertEquals(1, storage.load().size());
        assertEquals("Warning: Skipping invalid saved task at line 2.\n" +
                "Warning: Skipping invalid saved task at line 4.\n" +
                "Some saved tasks could not be loaded. Changes are disabled.\n" +
                "Repair the saved file or restore read access, then restart James.", storage.getLoadWarning());
        Files.writeString(file, "T | 0 | repaired\n");
        assertEquals(1, storage.load().size());
        assertEquals("", storage.getLoadWarning());
        assertFalse(storage.hasLoadErrors());
    }

}
