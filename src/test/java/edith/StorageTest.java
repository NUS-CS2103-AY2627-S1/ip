package edith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edith.task.SortOrder;
import edith.task.TaskList;
import edith.task.ToDo;

/** Tests loading and saving task data. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveAndLoad_sortedTasks_canRestorePersistedInsertionOrder() throws EdithException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("write report"));
        tasks.add(new ToDo("buy groceries"));
        tasks.sort(SortOrder.ALPHABETICAL);
        storage.save(tasks);

        TaskList loadedTasks = storage.load();
        loadedTasks.sort(SortOrder.ADDED);

        assertEquals("write report", loadedTasks.get(0).getDescription());
        assertEquals("buy groceries", loadedTasks.get(1).getDescription());
    }

    @Test
    void load_invalidSavedTask_throwsHelpfulException() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "D | 0 | pay bill | 2026-02-30 | 0\n");
        Storage storage = new Storage(taskFile.toString());

        assertThrows(EdithException.class, storage::load);
    }
}
