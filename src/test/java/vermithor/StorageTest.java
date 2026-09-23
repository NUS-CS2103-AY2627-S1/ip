package vermithor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies that tasks survive a save and load cycle. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveThenLoadRestoresTasks() throws VermithorException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt"));
        Task todo = new ToDo("read book");
        todo.markAsDone();
        List<Task> original = List.of(todo, new Deadline("submit report", LocalDate.of(2026, 9, 18)));

        storage.save(original);

        List<Task> restored = storage.load();
        assertEquals(original.toString(), restored.toString());
    }

    @Test
    void descriptionContainingDelimiterSurvivesRoundTrip() throws VermithorException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt"));
        List<Task> original = List.of(
                new ToDo("Buy milk | bread"),
                new Event("trip | getaway", "Mon | 2pm", "Tue | 4pm"));

        storage.save(original);

        List<Task> restored = storage.load();
        assertEquals(original.toString(), restored.toString());
    }

    @Test
    void descriptionContainingBackslashSurvivesRoundTrip() throws VermithorException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt"));
        List<Task> original = List.of(new ToDo("C:\\Users\\a"));

        storage.save(original);

        List<Task> restored = storage.load();
        assertEquals("C:\\Users\\a", restored.get(0).getDescription());
    }

    @Test
    void corruptedRecordIsSkippedWithoutLosingOtherTasks() throws Exception {
        Path file = temporaryDirectory.resolve("data/tasks.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "T|0|normal task\nD|0|Submit report|not-a-date\n");
        Storage storage = new Storage(file);

        List<Task> restored = storage.load();

        assertEquals(1, restored.size());
        assertEquals("normal task", restored.get(0).getDescription());
    }

    @Test
    void missingFileLoadsAsEmptyList() throws VermithorException {
        Storage storage = new Storage(temporaryDirectory.resolve("does-not-exist.txt"));

        List<Task> restored = storage.load();

        assertTrue(restored.isEmpty());
    }
}
