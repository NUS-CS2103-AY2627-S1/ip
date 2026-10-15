package duchess.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duchess.tasks.Task;
import duchess.tasks.collections.TaskList;
import duchess.tasks.exceptions.TaskException;
import duchess.tasks.factories.TaskFactory;

/** Tests that storage preserves a task list when it is saved and loaded again. */
class StorageTest {

    @Test
    void saveThenLoad_todo_preservesStorageFormat(@TempDir Path temporaryDirectory)
            throws IOException, TaskException {
        Task todo = TaskFactory.createFromCommand("todo read book");
        todo.mark();
        Storage storage = new Storage(temporaryDirectory.resolve("duchess.txt"));

        storage.save(new TaskList(new ArrayList<>(List.of(todo))));

        assertEquals(todo.getStorageFormat(), storage.load().getFirst().getStorageFormat());
    }

    @Test
    void saveThenLoad_deadline_preservesStorageFormat(@TempDir Path temporaryDirectory)
            throws IOException, TaskException {
        Task deadline = TaskFactory.createFromCommand("deadline submit assignment /by 2024-12-30");
        Storage storage = new Storage(temporaryDirectory.resolve("duchess.txt"));

        storage.save(new TaskList(new ArrayList<>(List.of(deadline))));

        assertEquals(deadline.getStorageFormat(), storage.load().getFirst().getStorageFormat());
    }

    @Test
    void saveThenLoad_event_preservesStorageFormat(@TempDir Path temporaryDirectory)
            throws IOException, TaskException {
        Task event = TaskFactory.createFromCommand(
                "event team meeting /from 2024-10-15 /to 2024-10-16");
        Storage storage = new Storage(temporaryDirectory.resolve("duchess.txt"));

        storage.save(new TaskList(new ArrayList<>(List.of(event))));

        assertEquals(event.getStorageFormat(), storage.load().getFirst().getStorageFormat());
    }
}
