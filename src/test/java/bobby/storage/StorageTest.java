package bobby.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bobby.BobbyException;
import bobby.task.Deadline;
import bobby.task.Event;
import bobby.task.Task;
import bobby.task.Todo;

/**
 * Tests saved-data loading behavior for missing and malformed task files.
 */
class StorageTest {
    private static final Path DATA_FILE = Path.of("data", "bobby.txt");

    @TempDir
    private Path temporaryFolder;

    private Path backupFile;
    private boolean hasOriginalDataFile;

    @BeforeEach
    void backUpDataFile() throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        hasOriginalDataFile = Files.exists(DATA_FILE);
        if (hasOriginalDataFile) {
            backupFile = temporaryFolder.resolve("bobby-backup.txt");
            Files.copy(DATA_FILE, backupFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @AfterEach
    void restoreDataFile() throws IOException {
        if (hasOriginalDataFile) {
            Files.copy(backupFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.deleteIfExists(DATA_FILE);
        }
    }

    @Test
    void loadTasks_missingFile_returnsEmptyList() throws BobbyException, IOException {
        Files.deleteIfExists(DATA_FILE);

        assertTrue(Storage.loadTasks().isEmpty());
    }

    @Test
    void loadTasks_duplicateTasks_throwsBobbyException() throws IOException {
        writeData(
                "T | 0 | read book",
                "T | 1 | read book");

        assertThrows(BobbyException.class, Storage::loadTasks);
    }

    @Test
    void loadTasks_validRows_returnsRestoredTasksWithStatusAndTags() throws BobbyException, IOException {
        writeData(
                "T | 1 | read book | #fun #school",
                "D | 0 | submit report | 2019-12-06T00:00 | #urgent",
                "E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00");

        ArrayList<Task> tasks = Storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book #fun #school", tasks.get(0).toString());
        assertEquals("[D][ ] submit report (by: Dec 6 2019) #urgent", tasks.get(1).toString());
        assertEquals("[E][ ] meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)",
                tasks.get(2).toString());
    }

    @Test
    void loadTasks_invalidTypeStatusFieldCountOrTag_throwsBobbyException() throws IOException {
        writeData("X | 0 | read book");
        assertThrows(BobbyException.class, Storage::loadTasks);

        writeData("T | 2 | read book");
        assertThrows(BobbyException.class, Storage::loadTasks);

        writeData("T | 0 | read book | #fun | extra");
        assertThrows(BobbyException.class, Storage::loadTasks);

        writeData("D | 0 | submit report");
        assertThrows(BobbyException.class, Storage::loadTasks);

        writeData("E | 0 | meeting | 2019-12-02T14:00");
        assertThrows(BobbyException.class, Storage::loadTasks);

        writeData("T | 0 | read book | #bad!");
        assertThrows(BobbyException.class, Storage::loadTasks);
    }

    @Test
    void loadTasks_invalidDescription_throwsBobbyException() throws IOException {
        writeData("T | 0 | ");

        assertThrows(BobbyException.class, Storage::loadTasks);
    }

    @Test
    void loadTasks_invalidEventRange_throwsBobbyException() throws IOException {
        writeData("E | 0 | meeting | 2019-12-02T16:00 | 2019-12-02T14:00");

        assertThrows(BobbyException.class, Storage::loadTasks);
    }

    @Test
    void saveTasks_validTasks_writesStorageFile() throws BobbyException, IOException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 6, 0, 0));
        Event event = new Event("meeting", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));
        todo.markAsDone();
        deadline.addTag("#urgent");

        Storage.saveTasks(List.of(todo, deadline, event));

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | submit report | 2019-12-06T00:00 | #urgent",
                "E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00"),
                Files.readAllLines(DATA_FILE));
    }

    private void writeData(String... lines) throws IOException {
        Files.write(DATA_FILE, List.of(lines));
    }
}
