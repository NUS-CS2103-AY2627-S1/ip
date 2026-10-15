package duke.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;

/**
 * Tests loading and saving tasks with the storage helper.
 */
public class StorageTest {
    @TempDir
    public Path tempDir;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws DukeException {
        Storage storage = new Storage(tempDir.resolve("data/duke.txt").toString());

        assertEquals(0, storage.loadTasks().size());
    }

    @Test
    public void saveTasks_newFolder_writesExpectedFile() throws Exception {
        Path dataFile = tempDir.resolve("data/duke.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", LocalDate.parse("2019-10-15")));
        tasks.add(new Event("project meeting", LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16")));
        tasks.get(0).markAsDone();

        storage.saveTasks(tasks);

        assertEquals(
                "T | 1 | read book\n"
                        + "D | 0 | return book | 2019-10-15\n"
                        + "E | 0 | project meeting | 2019-10-15 | 2019-10-16\n",
                Files.readString(dataFile));
    }

    @Test
    public void loadTasks_validFile_returnsExpectedTasks() throws Exception {
        Path dataFile = tempDir.resolve("data/duke.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(
                dataFile,
                "T | 1 | read book\n"
                        + "D | 0 | return book | 2019-10-15\n"
                        + "E | 0 | project meeting | 2019-10-15 | 2019-10-16\n");
        Storage storage = new Storage(dataFile.toString());

        List<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)", tasks.get(2).toString());
    }

    @Test
    public void loadTasks_invalidDate_throwsException() throws Exception {
        Path dataFile = tempDir.resolve("data/duke.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "D | 0 | return book | Sunday\n");
        Storage storage = new Storage(dataFile.toString());

        DukeException exception = assertThrows(DukeException.class, storage::loadTasks);

        assertEquals("The saved deadline on line 1 has an invalid date.", exception.getMessage());
    }
}
