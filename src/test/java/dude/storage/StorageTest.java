package dude.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dude.task.Deadline;
import dude.task.Event;
import dude.task.EventPeriod;
import dude.task.TaskList;
import dude.task.Todo;

/**
 * Tests storage round trips and malformed-record recovery.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoad_roundTripsTaskTypesAndCompletion() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested/tasks.jsonl");
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("legacy.txt"));
        Todo todo = new Todo("read \"book\"");
        todo.markAsDone();
        TaskList original = new TaskList(List.of(todo,
                new Deadline("return book", dude.task.TaskDate.parse("2019-12-02")),
                new Event("project", new EventPeriod(dude.task.TaskDate.parse("2019-12-01"),
                        dude.task.TaskDate.parse("2019-12-03")))));

        storage.saveTasks(original);
        TaskList loaded = storage.loadTasks();

        assertEquals(original.asList().toString(), loaded.asList().toString());
        assertTrue(loaded.get(0).isDone());
    }

    @Test
    public void loadMalformedRecord_preservesItAsCorruptedTask() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.jsonl");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "not json\n");
        Storage storage = new Storage(dataFile, temporaryDirectory.resolve("legacy.txt"));

        TaskList loaded = storage.loadTasks();

        assertEquals("[C][ ] [Corrupted: not json]", loaded.get(0).toString());
    }
}
