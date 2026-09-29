package tasktracker.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tasktracker.exception.TaskTrackerException;
import tasktracker.task.Fixed;
import tasktracker.task.Task;
import tasktracker.task.ToDo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws TaskTrackerException {
        Path nonExistentPath = tempDir.resolve("does-not-exist.txt");
        Storage storage = new Storage(nonExistentPath.toString());
        List<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_validFile_returnsCorrectTasks() throws Exception {
        File dataFile = tempDir.resolve("valid-tasks.txt").toFile();
        try (FileWriter writer = new FileWriter(dataFile)) {
            writer.write("T | 1 | read book\n");
            writer.write("F | 0 | project meeting | 2 hours\n");
        }

        Storage storage = new Storage(dataFile.getAbsolutePath());
        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).toString().contains("[X]"));

        assertEquals("project meeting", loadedTasks.get(1).getDescription());
        assertTrue(loadedTasks.get(1).toString().contains("[ ]"));
        assertTrue(loadedTasks.get(1) instanceof Fixed);
    }

    @Test
    public void saveAndReload_roundTrip_preservesTaskData() throws Exception {
        Path savePath = tempDir.resolve("saved-tasks.txt");
        Storage storage = new Storage(savePath.toString());

        ToDo todo = new ToDo("submit assignment");
        Fixed fixedTask = new Fixed("read notes", "45 mins");
        fixedTask.markAsDone();

        storage.save(List.of(todo, fixedTask));

        List<Task> reloaded = storage.load();
        assertEquals(2, reloaded.size());
        assertEquals("submit assignment", reloaded.get(0).getDescription());
        assertTrue(reloaded.get(0).toString().contains("[ ]"));

        assertEquals("read notes", reloaded.get(1).getDescription());
        assertTrue(reloaded.get(1).toString().contains("[X]"));
    }

    @Test
    public void load_corruptedLine_skipsAndRecordsWarning() throws Exception {
        File dataFile = tempDir.resolve("corrupted-line.txt").toFile();
        try (FileWriter writer = new FileWriter(dataFile)) {
            writer.write("NOT_A_VALID_FORMAT\n");
        }

        Storage storage = new Storage(dataFile.getAbsolutePath());
        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertFalse(storage.getLoadWarnings().isEmpty());
        assertTrue(storage.getLoadWarnings().get(0).contains("Line 1"));
    }

    @Test
    public void load_invalidStatus_skipsAndRecordsWarning() throws Exception {
        File dataFile = tempDir.resolve("corrupted-status.txt").toFile();
        try (FileWriter writer = new FileWriter(dataFile)) {
            writer.write("T | 99 | read book\n");
        }

        Storage storage = new Storage(dataFile.getAbsolutePath());
        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertFalse(storage.getLoadWarnings().isEmpty());
        assertTrue(storage.getLoadWarnings().get(0).contains("Line 1"));
    }

    @Test
    public void load_unknownType_skipsAndRecordsWarning() throws Exception {
        File dataFile = tempDir.resolve("unknown-type.txt").toFile();
        try (FileWriter writer = new FileWriter(dataFile)) {
            writer.write("X | 0 | unknown type\n");
        }

        Storage storage = new Storage(dataFile.getAbsolutePath());
        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertFalse(storage.getLoadWarnings().isEmpty());
        assertTrue(storage.getLoadWarnings().get(0).contains("Line 1"));
    }

    @Test
    public void load_fixedTaskMissingDuration_skipsAndRecordsWarning() throws Exception {
        File dataFile = tempDir.resolve("missing-duration.txt").toFile();
        try (FileWriter writer = new FileWriter(dataFile)) {
            writer.write("F | 0 | project\n");
        }

        Storage storage = new Storage(dataFile.getAbsolutePath());
        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertFalse(storage.getLoadWarnings().isEmpty());
        assertTrue(storage.getLoadWarnings().get(0).contains("Line 1"));
    }

    @Test
    public void load_mixedValidAndCorruptedLines_loadsValidAndRecordsWarnings() throws Exception {
        File dataFile = tempDir.resolve("mixed-tasks.txt").toFile();
        try (FileWriter writer = new FileWriter(dataFile)) {
            writer.write("T | 0 | buy milk\n");
            writer.write("bad line format\n");
            writer.write("T | 1 | clean room\n");
        }

        Storage storage = new Storage(dataFile.getAbsolutePath());
        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("buy milk", loadedTasks.get(0).getDescription());
        assertEquals("clean room", loadedTasks.get(1).getDescription());

        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("Line 2"));
    }
}
