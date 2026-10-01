package victoria.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import victoria.model.TaskList;

class TaskFileTest {
    private static final Path DATA_FILE = Path.of("data", "victoria.txt");

    private byte[] originalFile;

    @BeforeEach
    void isolateDataFile() throws IOException {
        originalFile = Files.exists(DATA_FILE) ? Files.readAllBytes(DATA_FILE) : null;
        Files.deleteIfExists(DATA_FILE);
    }

    @AfterEach
    void restoreDataFile() throws IOException {
        if (originalFile == null) {
            Files.deleteIfExists(DATA_FILE);
        } else {
            Files.write(DATA_FILE, originalFile);
        }
    }

    @Test
    void loadInto_nullList_reportsError() {
        TaskFile.LoadResult result = TaskFile.loadInto(null);

        assertEquals(TaskFile.LoadStatus.ERROR, result.status());
        assertEquals(0, result.loadedTasks());
    }

    @Test
    void saveAndLoad_tasksRoundTripWithStatusAndDates() throws Exception {
        TaskList source = new TaskList(10);
        source.add("Buy milk");
        source.add(new victoria.model.Deadline("Submit report", "2026-08-23"));
        source.add(new victoria.model.Event("Meeting", "2026-08-23", "2026-08-24"));
        source.markDone(1);

        TaskFile.save(source);

        TaskList restored = new TaskList(10);
        TaskFile.LoadResult result = TaskFile.loadInto(restored);

        assertEquals(TaskFile.LoadStatus.LOADED, result.status());
        assertEquals(3, result.loadedTasks());
        assertEquals("Buy milk", restored.getTask(1).getDescription());
        assertTrue(restored.getTask(1).isDone());
        assertEquals("2026-08-23", ((victoria.model.Deadline) restored.getTask(2)).getBy());
        assertEquals("2026-08-24", ((victoria.model.Event) restored.getTask(3)).getTo());
    }

    @Test
    void loadInto_missingEmptyAndInvalidFiles_reportsAppropriateStatus() throws IOException {
        TaskList tasks = new TaskList(5);

        assertEquals(TaskFile.LoadStatus.NO_FILE, TaskFile.loadInto(tasks).status());

        Files.createDirectories(DATA_FILE.getParent());
        Files.writeString(DATA_FILE, "");
        assertEquals(TaskFile.LoadStatus.EMPTY, TaskFile.loadInto(tasks).status());

        Files.writeString(DATA_FILE, "invalid\nT|2|not-base64\nD|0|dGFzaw==|bad-date\n");
        assertEquals(TaskFile.LoadStatus.NO_VALID_RECORDS, TaskFile.loadInto(tasks).status());
        assertEquals(0, tasks.size());
    }

    @Test
    void loadInto_moreRecordsThanCapacity_loadsOnlyAvailableTasks() throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        Files.writeString(DATA_FILE, "T|0|Zmlyc3Q=\nT|1|c2Vjb25k\n");
        TaskList tasks = new TaskList(1);

        TaskFile.LoadResult result = TaskFile.loadInto(tasks);

        assertEquals(TaskFile.LoadStatus.LOADED, result.status());
        assertEquals(1, result.loadedTasks());
        assertEquals("first", tasks.getTask(1).getDescription());
    }
}
