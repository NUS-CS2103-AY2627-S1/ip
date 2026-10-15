package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests that task data can be persisted and restored without losing task details. */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_emptyTaskListReturned() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveThenLoad_allTaskTypesAndCompletionStates_preserved() throws Exception {
        Path storageFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(storageFile.toString());
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", "17-07-2004 1800");
        Event event = new Event("team meeting", "18-07-2004 0905");
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(List.of(
                "TODO|false|read book",
                "DEADLINE|true|submit report|17-07-2004 1800",
                "EVENT|false|team meeting|18-07-2004 0905"), Files.readAllLines(storageFile));
        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("[t][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[d][x] submit report (by: 17 Jul 2004, 18:00)", loadedTasks.get(1).toString());
        assertEquals("[e][ ] team meeting (at: 18 Jul 2004, 09:05)", loadedTasks.get(2).toString());
    }

    @Test
    void load_blankMalformedAndUnknownLines_onlyValidTasksLoaded() throws Exception {
        Path storageFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(storageFile, List.of(
                "",
                "not enough fields",
                "UNKNOWN|false|ignore me",
                "TODO|true|keep me"));
        Storage storage = new Storage(storageFile.toString());

        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("[t][x] keep me", loadedTasks.get(0).toString());
    }
}
