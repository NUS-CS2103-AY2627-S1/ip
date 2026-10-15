package labubu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests save-file persistence and malformed save-file handling. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void returnsEmptyListWhenSaveFileIsMissing() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void savesAndLoadsAllTaskTypesAndStatuses() throws Exception {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList(List.of(
                new ToDo("read book"),
                new Deadline("return book", LocalDateTime.of(2026, 7, 6, 18, 30)),
                new Event("meeting", LocalDateTime.of(2026, 7, 6, 14, 0),
                        LocalDateTime.of(2026, 7, 6, 15, 0))));
        tasks.getTask(0).setStatus(Task.Status.COMPLETED);
        tasks.getTask(1).setStatus(Task.Status.IN_PROGRESS);

        Storage storage = new Storage(savePath.toString());
        storage.saveTasks(tasks);
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals(Task.Status.COMPLETED, loadedTasks.get(0).getStatus());
        assertEquals(Task.Status.IN_PROGRESS, loadedTasks.get(1).getStatus());
        assertEquals("E", loadedTasks.get(2).getMarker());
    }

    @Test
    public void deletesSaveFileWhenMarkerIsInvalid() throws Exception {
        Path savePath = writeSaveFile("X|INCOMPLETE|unknown");
        Storage storage = new Storage(savePath.toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                storage::loadTasks);

        assertEquals("Unknown task marker. Resetting save file.", exception.getMessage());
        assertTrue(Files.notExists(savePath));
    }

    @Test
    public void deletesSaveFileWhenDateIsInvalid() throws Exception {
        Path savePath = writeSaveFile("D|INCOMPLETE|return book|not-a-date");
        Storage storage = new Storage(savePath.toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                storage::loadTasks);

        assertEquals("Invalid date/time format. Resetting save file.", exception.getMessage());
        assertTrue(Files.notExists(savePath));
    }

    @Test
    public void rejectsInvalidStatus() throws Exception {
        Path savePath = writeSaveFile("T|NOT_A_STATUS|task");
        Storage storage = new Storage(savePath.toString());

        assertThrows(IllegalArgumentException.class, storage::loadTasks);
    }

    @Test
    public void deletesSaveFileWhenRecordHasMissingFields() throws Exception {
        Path savePath = writeSaveFile("E|INCOMPLETE|meeting|2026-07-06T14:00");
        Storage storage = new Storage(savePath.toString());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                storage::loadTasks);

        assertEquals("Malformed save file. Resetting save file.", exception.getMessage());
        assertTrue(Files.notExists(savePath));
    }

    private Path writeSaveFile(String content) throws Exception {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(savePath, content, StandardCharsets.UTF_8);
        return savePath;
    }
}
