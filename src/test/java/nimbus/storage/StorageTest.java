package nimbus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nimbus.task.Deadline;
import nimbus.task.Event;
import nimbus.task.Task;
import nimbus.task.Todo;

class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveAndLoad_mixedTasks_preservesTaskDetails() throws IOException {
        Path storageFile = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(storageFile);
        Todo todo = new Todo("read | annotate");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", "2026-09-18");
        Event event = new Event("project meeting", "2pm", "3pm");

        storage.save(List.of(todo, deadline, event));
        Storage.LoadResult result = storage.load();

        assertEquals(0, result.skippedRecordCount());
        assertEquals(3, result.tasks().size());
        assertTrue(result.tasks().get(0).isDone());
        assertEquals("read | annotate", result.tasks().get(0).getDescription());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, result.tasks().get(1));
        assertEquals("2026-09-18", loadedDeadline.getStorageDate());
        Event loadedEvent = assertInstanceOf(Event.class, result.tasks().get(2));
        assertEquals("2pm", loadedEvent.getFrom());
        assertEquals("3pm", loadedEvent.getTo());
    }

    @Test
    void load_damagedRecord_recoversValidRecords() throws IOException {
        Path storageFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(storageFile);
        storage.save(List.of(new Todo("keep me")));
        Files.writeString(storageFile, "broken record", StandardOpenOption.APPEND);

        Storage.LoadResult result = storage.load();

        assertEquals(1, result.skippedRecordCount());
        assertEquals(List.of("keep me"), result.tasks().stream().map(Task::getDescription).toList());
    }

    @Test
    void save_existingFile_replacesOldTasks() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        storage.save(List.of(new Todo("old task")));

        storage.save(List.of(new Todo("new task")));

        Storage.LoadResult result = storage.load();
        assertEquals(1, result.tasks().size());
        assertEquals("new task", result.tasks().get(0).getDescription());
    }
}
