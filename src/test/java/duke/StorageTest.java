package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests task persistence using an isolated temporary data file. */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void saveThenLoad_mixedTasks_preservesTypesAndState() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("data").resolve("tasks.txt"));
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 18, 18, 0));
        Reminder reminder = new Reminder("call mum",
                LocalDateTime.of(2026, 9, 19, 9, 0));

        storage.save(List.of(todo, deadline, reminder));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("X", loadedTasks.get(0).getStatusIcon());
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Reminder.class, loadedTasks.get(2));
    }
}
