package squirtlebot.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import squirtlebot.TemporalPair;
import squirtlebot.exception.StorageException;
import squirtlebot.task.Deadline;
import squirtlebot.task.Event;
import squirtlebot.task.TaskList;
import squirtlebot.task.ToDo;

/**
 * Tests storage functionalities using {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void loadData_fileDoesNotExist_createsFileAndReturnsEmptyList() throws Exception {
        Path storagePath = tempDirectory.resolve("nested").resolve("Tasks.ser");
        Storage storage = new Storage(storagePath.toString());

        TaskList loadedTasks = storage.loadData();

        assertTrue(Files.exists(storagePath));
        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    public void loadData_emptyFile_returnsEmptyList() throws Exception {
        Path storagePath = tempDirectory.resolve("Tasks.ser");
        Files.createFile(storagePath);
        Storage storage = new Storage(storagePath.toString());

        assertTrue(storage.loadData().isEmpty());
    }

    @Test
    public void writeAndLoad_tasksPresent_returnsEquivalentTasks() throws Exception {
        Path storagePath = tempDirectory.resolve("Tasks.ser");
        Storage storage = new Storage(storagePath.toString());
        TaskList expectedTasks = createTaskList();

        storage.writeData(expectedTasks);

        assertEquals(expectedTasks, storage.loadData());
    }

    @Test
    public void resetData_tasksStored_clearsStorage() throws Exception {
        Path storagePath = tempDirectory.resolve("Tasks.ser");
        Storage storage = new Storage(storagePath.toString());
        storage.writeData(createTaskList());

        storage.resetData();

        assertTrue(Files.exists(storagePath));
        assertTrue(storage.loadData().isEmpty());
    }

    @Test
    public void disabledStorage_writeAndReset_doNotChangeFile() throws Exception {
        Path storagePath = tempDirectory.resolve("Tasks.ser");
        Storage storage = new Storage(storagePath.toString());
        storage.writeData(createTaskList());
        byte[] originalData = Files.readAllBytes(storagePath);
        storage.disable();

        storage.writeData(new TaskList());
        storage.resetData();

        assertTrue(Files.exists(storagePath));
        assertArrayEquals(originalData, Files.readAllBytes(storagePath));
    }

    @Test
    public void disabledStorage_fileDoesNotExist_doesNotCreateFile() throws IOException {
        Path storagePath = tempDirectory.resolve("Tasks.ser");
        Storage storage = new Storage(storagePath.toString());
        storage.disable();

        storage.writeData(createTaskList());
        storage.resetData();

        assertFalse(Files.exists(storagePath));
    }

    @Test
    public void loadData_corruptedFile_throwsStorageException() throws IOException {
        Path storagePath = tempDirectory.resolve("Tasks.ser");
        Files.writeString(storagePath, "not serialized task data");
        Storage storage = new Storage(storagePath.toString());

        assertThrows(StorageException.class, storage::loadData);
    }

    @Test
    public void loadData_serializedObjectIsNotTaskList_throwsStorageException() throws IOException {
        Path storagePath = tempDirectory.resolve("Tasks.ser");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                Files.newOutputStream(storagePath))) {
            outputStream.writeObject("not a task list");
        }
        Storage storage = new Storage(storagePath.toString());

        StorageException exception = assertThrows(StorageException.class, storage::loadData);

        assertEquals("Stored data is invalid :(", exception.getMessage());
    }

    private TaskList createTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read textbook"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 30)));

        ArrayList<TemporalPair> schedules = new ArrayList<>();
        schedules.add(new TemporalPair(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2)));
        tasks.add(new Event("project meeting", schedules));
        return tasks;
    }

}
