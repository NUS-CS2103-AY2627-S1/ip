package miaow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import miaow.task.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void getFilePath_returnsConfiguredFilePath() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        assertEquals(filePath.toString(), storage.getFilePath());
    }

    @Test
    void fileExists_returnsFalseWhenFileDoesNotExist() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        assertFalse(storage.fileExists());
    }

    @Test
    void loadTasks_returnsEmptyListWhenFileDoesNotExist() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveTasks_writesTasksToFile() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Finish homework"));

        storage.saveTasks(tasks);

        assertTrue(storage.fileExists());
        assertEquals(
                "T | 0 | Finish homework",
                Files.readAllLines(filePath).get(0));
    }

    @Test
    void saveAndLoadTasks_preservesTaskInformation() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();

        Task unfinishedTask = new Task("Finish homework");

        Task finishedTask = new Task("Submit assignment");
        finishedTask.mark();

        tasks.add(unfinishedTask);
        tasks.add(finishedTask);

        storage.saveTasks(tasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals("Finish homework", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isDone());

        assertEquals("Submit assignment", loadedTasks.get(1).getDescription());
        assertTrue(loadedTasks.get(1).isDone());
    }

    @Test
    void formatTaskForFile_returnsTaskFileFormat() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        Task task = new Task("Read a book");

        assertEquals("T | 0 | Read a book", storage.formatTaskForFile(task));
    }
}
