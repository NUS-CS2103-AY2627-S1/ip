package sherlock.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sherlock.exception.SherlockException;
import sherlock.task.Deadline;
import sherlock.task.TaskList;
import sherlock.task.Todo;

/** Tests saving, loading, and validation of Sherlock's task data. */
class StorageTest {
    @TempDir
    private Path temporaryFolder;

    @Test
    void saveThenLoad_preservesTasksAndCompletionState() throws IOException, SherlockException {
        Path saveFile = temporaryFolder.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(saveFile);
        TaskList tasks = new TaskList(2);
        Todo completedTodo = new Todo("inspect footprint");
        completedTodo.markAsDone();
        tasks.add(completedTodo);
        tasks.add(new Deadline("file report", "2026-09-18"));

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("[T][X] inspect footprint", loadedTasks.get(0).toString());
        assertEquals("[D][ ] file report (by: Sep 18 2026)", loadedTasks.get(1).toString());
    }

    @Test
    void loadMalformedTask_throwsHelpfulException() throws IOException {
        Path saveFile = temporaryFolder.resolve("tasks.txt");
        Files.writeString(saveFile, "Z | 0 | mysterious record");
        Storage storage = new Storage(saveFile);

        SherlockException exception = assertThrows(SherlockException.class, storage::load);

        assertEquals("a saved task has an unknown type.", exception.getMessage());
    }
}
