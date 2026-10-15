package storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import task.DeadlineTask;
import task.EventTask;
import task.Task;
import task.TaskList;

/**
 * Tests persistence behavior of Storage: loading, saving and round-tripping tasks.
 */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void load_preservesExistingDuplicateEntries() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T|0|buy milk\nT|1|buy milk");

        assertEquals(2, new Storage(taskFile.toString()).load().size());
    }

    @Test
    public void load_missingFileReturnsEmptyTaskList() throws Exception {
        Path taskFile = temporaryDirectory.resolve("missing.txt");

        assertTrue(new Storage(taskFile.toString()).load().isEmpty());
    }

    @Test
    public void load_skipsMalformedLines() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T|0|buy milk\nnot a valid signature\nT|1|buy bread");

        TaskList tasks = new Storage(taskFile.toString()).load();

        assertEquals(2, tasks.size());
    }

    @Test
    public void load_parsesAllTaskTypes() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile,
                "T|0|buy milk\n"
                        + "D|1|submit report|09/09/2026 10:00\n"
                        + "E|0|team meeting|09/09/2026 10:00|09/09/2026 11:00");

        TaskList tasks = new Storage(taskFile.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals(new Task("buy milk"), tasks.get(0));
        assertEquals(
                new DeadlineTask("submit report", LocalDateTime.of(2026, 9, 9, 10, 0)),
                tasks.get(1));
        assertEquals(
                new EventTask(
                        "team meeting",
                        LocalDateTime.of(2026, 9, 9, 10, 0),
                        LocalDateTime.of(2026, 9, 9, 11, 0)),
                tasks.get(2));
    }

    @Test
    public void save_writesSignaturesThatCanBeReloaded() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));
        tasks.add(new DeadlineTask("submit report", true, LocalDateTime.of(2026, 9, 9, 10, 0)));

        storage.save(tasks);
        TaskList reloaded = storage.load();

        assertEquals(tasks.toSignature(), reloaded.toSignature());
    }

    @Test
    public void save_overwritesExistingFileContent() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T|0|old task");
        Storage storage = new Storage(taskFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Task("new task"));

        storage.save(tasks);

        assertEquals("T|0|new task", Files.readString(taskFile));
    }
}
