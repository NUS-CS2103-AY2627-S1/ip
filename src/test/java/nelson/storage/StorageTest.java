package nelson.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nelson.exception.NelsonException;
import nelson.task.Deadline;
import nelson.task.Event;
import nelson.task.TaskList;
import nelson.task.Todo;

/** Tests task persistence and recovery from unavailable or malformed data. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        TaskList tasks = storage.load();

        assertEquals(0, tasks.size());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesContentAndStatus() throws NelsonException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile);
        Todo todo = new Todo("study openings");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(
                todo,
                new Deadline("submit iP", "2026-09-18"),
                new Event("tournament", "2026-09-19", "2026-09-20")));

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] study openings", loadedTasks.get(0).toString());
        assertEquals("[D][ ] submit iP (by: Sep 18 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] tournament (from: Sep 19 2026 to: Sep 20 2026)",
                loadedTasks.get(2).toString());
    }

    @Test
    public void load_malformedRecords_skipsInvalidLines() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, List.of(
                "",
                "unknown | 0 | task",
                "T | 2 | invalid status",
                "D | 0 | invalid date | tomorrow",
                "E | 0 | invalid range | 2026-09-20 | 2026-09-19",
                "T | 0 | valid task"));
        Storage storage = new Storage(taskFile);

        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("[T][ ] valid task", loadedTasks.get(0).toString());
    }

    @Test
    public void save_missingParentDirectory_createsParentAndFile() {
        Path taskFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(taskFile);

        storage.save(new TaskList(List.of(new Todo("study openings"))));

        assertTrue(Files.isRegularFile(taskFile));
    }
}
