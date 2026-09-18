package waffles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests recovery and round-trip persistence of task records. */
class StorageTest {
    /** Temporary folder used to isolate filesystem tests. */
    @TempDir
    Path temporaryFolder;

    /** Verifies descriptions with record delimiters survive a save and load cycle. */
    @Test
    void saveTasks_escapedDescription_roundTripsWithoutDataLoss() {
        Storage storage = storage();
        Todo todo = new Todo("cook | eat \\\\ repeat");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 18));

        storage.saveTasks(List.of(todo, deadline));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals(todo.getDescription(), loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals(deadline.toString(), loadedTasks.get(1).toString());
    }

    /** Verifies malformed records are skipped while valid records remain available. */
    @Test
    void loadTasks_malformedRecords_recoversValidRecords() throws Exception {
        Storage storage = storage();
        Path file = temporaryFolder.resolve("waffles.txt");
        Files.writeString(file, "T|0|valid task\nD|0|broken|2026-02-30\nnot a record\n");

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
    }

    /** Creates isolated storage for one persistence test. */
    private Storage storage() {
        return new Storage(temporaryFolder.resolve("waffles.txt").toString());
    }
}
