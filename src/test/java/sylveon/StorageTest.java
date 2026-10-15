package sylveon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence of task descriptions and task-specific fields. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void saveAndLoad_descriptionsContainingPipes_preservesAllTasks() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("tea | coffee");
        Deadline deadline = new Deadline("elon | musk", LocalDate.of(2099, 9, 20));
        Event event = new Event("fish | chips", LocalDate.of(2099, 9, 21), LocalDate.of(2099, 9, 22));
        deadline.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("tea | coffee", loadedTasks.get(0).getDescription());
        assertEquals("elon | musk", loadedTasks.get(1).getDescription());
        assertTrue(loadedTasks.get(1).isDone());
        assertEquals(LocalDate.of(2099, 9, 20), ((Deadline) loadedTasks.get(1)).getBy());
        assertEquals("fish | chips", loadedTasks.get(2).getDescription());
        assertEquals(LocalDate.of(2099, 9, 21), ((Event) loadedTasks.get(2)).getFrom());
        assertEquals(LocalDate.of(2099, 9, 22), ((Event) loadedTasks.get(2)).getTo());
    }
}
