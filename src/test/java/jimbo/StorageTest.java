package jimbo;

import jimbo.task.Deadline;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Tests generated using ChatGPT 5.6-Luna

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_fileDoesNotExist_createsEmptyFileAndReturnsEmptyList() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());

        List<Task> tasks = storage.load();

        assertTrue(Files.exists(saveFile));
        assertTrue(tasks.isEmpty());
    }

    @Test
    void save_emptyTaskList_createsEmptyFile() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());

        storage.save(List.of());

        assertTrue(Files.exists(saveFile));
        assertEquals("", Files.readString(saveFile));
    }

    @Test
    void save_tasks_writesSerialisedTasksToFile() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        List<Task> tasks = List.of(new Todo("read book"),
                new Deadline("return book", "2026-06-06 1800"));

        storage.save(tasks);

        String expected = "T|0|read book|\n"
                + "D|0|return book|2026-06-06 1800|\n";
        assertEquals(expected, Files.readString(saveFile));
    }

    @Test
    void save_thenLoad_returnsEquivalentTasks() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        List<Task> originalTasks = new ArrayList<>(List.of(
                new Todo("read book"),
                new Deadline("return book", "2026-06-06 1800"),
                new Event("project meeting", "2026-06-06 1400", "2026-06-06 1600")));
        originalTasks.get(0).mark();

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(originalTasks.size(), loadedTasks.size());
        for (int i = 0; i < originalTasks.size(); i++) {
            assertEquals(originalTasks.get(i).toString(), loadedTasks.get(i).toString());
            assertEquals(originalTasks.get(i).serialise(), loadedTasks.get(i).serialise());
        }
    }

    @Test
    void save_existingFile_replacesPreviousContents() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());

        storage.save(List.of(new Todo("old task")));
        storage.save(List.of(new Todo("new task")));

        assertFalse(Files.readString(saveFile).contains("old task"));
        assertTrue(Files.readString(saveFile).contains("new task"));
    }

    @Test
    void load_malformedTaskData_throwsJimboException() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "invalid task data\n");
        Storage storage = new Storage(saveFile.toString());

        assertThrows(JimboException.class, storage::load);
    }
}
