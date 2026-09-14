package baron.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import baron.exception.BaronException;
import baron.task.Deadline;
import baron.task.Event;
import baron.task.Task;
import baron.task.Todo;

class TaskPersistenceTest {
    private static final Path SAVE_FILE = Path.of("./data/tasks.json");

    private boolean hasSaveFile;
    private byte[] originalSaveFile;

    @BeforeEach
    void backupSaveFile() throws IOException {
        hasSaveFile = Files.exists(SAVE_FILE);
        if (hasSaveFile) {
            originalSaveFile = Files.readAllBytes(SAVE_FILE);
        }
    }

    @AfterEach
    void restoreSaveFile() throws IOException {
        if (hasSaveFile) {
            Files.write(SAVE_FILE, originalSaveFile);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    void saveThenLoad_mixedTasks_returnsEquivalentTasks() throws BaronException {
        ArrayList<Task> tasks = new ArrayList<>(List.of(
                new Todo("read book"),
                new Deadline("submit report", "2025-08-20 14:30"),
                new Event("team meeting", "2025-08-21 10:00", "2025-08-21 11:00")));
        tasks.get(0).markAsDone();

        TaskPersistence.save(tasks);

        assertEquals(tasks.toString(), TaskPersistence.load().toString());
    }

    @Test
    void saveThenLoad_emptyList_returnsEmptyList() throws BaronException {
        TaskPersistence.save(new ArrayList<>());

        assertEquals(List.of(), TaskPersistence.load());
    }

    @Test
    void saveThenLoad_descriptionContainingPipe_preservesDescription() throws BaronException {
        ArrayList<Task> tasks = new ArrayList<>(List.of(new Todo("Plan | review | submit")));

        TaskPersistence.save(tasks);

        assertEquals(tasks.toString(), TaskPersistence.load().toString());
    }

    @Test
    void load_whenSaveFileDoesNotExist_returnsEmptyList() throws IOException, BaronException {
        Files.deleteIfExists(SAVE_FILE);

        assertEquals(List.of(), TaskPersistence.load());
    }

    @Test
    void saveThenLoad_whenSaveFileDoesNotExist_createsAndLoadsTasks() throws IOException, BaronException {
        Files.deleteIfExists(SAVE_FILE);
        ArrayList<Task> tasks = new ArrayList<>(List.of(new Todo("read book")));

        TaskPersistence.save(tasks);

        assertEquals(tasks.toString(), TaskPersistence.load().toString());
    }

    @Test
    void load_malformedTask_throwsBaronException() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE, "not valid json");

        assertThrows(BaronException.class, TaskPersistence::load);
    }
}
