package nutrisoy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Deadline;
import nutrisoy.task.Event;
import nutrisoy.task.Task;
import nutrisoy.task.Todo;

public class StorageTest {
    @Test
    public void loadTasks_supportsLegacyAndTaggedRecords(@TempDir Path tempDirectory)
            throws IOException, DukeException {
        Path storagePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 0 | legacy task\nT | 1 | tagged task | fun,school\n");

        ArrayList<Task> tasks = new Storage(storagePath.toString()).loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] legacy task", tasks.get(0).toString());
        assertEquals("[T][X] tagged task #fun #school", tasks.get(1).toString());
        assertEquals("T | 1 | tagged task | fun,school", tasks.get(1).toFileFormat());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypesWithTags_roundTripsSuccessfully(@TempDir Path tempDirectory)
            throws DukeException {
        Path storagePath = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(storagePath.toString());
        ArrayList<Task> originalTasks = new ArrayList<>();

        Todo todo = new Todo("buy tofu");
        todo.addTag("errand");
        Deadline deadline = new Deadline("submit report", LocalDate.parse("2026-09-20"));
        deadline.addTag("school");
        Event event = new Event("project sprint", LocalDate.parse("2026-09-21"),
                LocalDate.parse("2026-09-23"));
        event.addTag("team");
        event.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(deadline);
        originalTasks.add(event);

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals(originalTasks.stream().map(Task::toFileFormat).toList(),
                loadedTasks.stream().map(Task::toFileFormat).toList());
    }

    @Test
    public void loadTasks_tagBeforeDateRecords_recoversPreviouslyWrittenFormat(
            @TempDir Path tempDirectory) throws IOException, DukeException {
        Path storagePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "D | 0 | submit report | school | 2026-09-20\n"
                + "E | 1 | project sprint | team | 2026-09-21 | 2026-09-23\n");

        ArrayList<Task> tasks = new Storage(storagePath.toString()).loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("D | 0 | submit report | 2026-09-20 | school", tasks.get(0).toFileFormat());
        assertEquals("E | 1 | project sprint | 2026-09-21 | 2026-09-23 | team",
                tasks.get(1).toFileFormat());
    }

    @Test
    public void loadTasks_invalidRecords_skipsInvalidData(@TempDir Path tempDirectory)
            throws IOException, DukeException {
        Path storagePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, "T | 2 | invalid status\n"
                + "E | 0 | reversed event | 2026-09-23 | 2026-09-21\n"
                + "T | 0 | valid task\n"
                + "T | 0 | valid task\n");

        ArrayList<Task> tasks = new Storage(storagePath.toString()).loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | valid task", tasks.get(0).toFileFormat());
    }

    @Test
    public void storage_directoryUsedAsFile_exceptionThrown(@TempDir Path tempDirectory) {
        Storage storage = new Storage(tempDirectory.toString());

        assertThrows(DukeException.class, storage::loadTasks);
        assertThrows(DukeException.class, () -> storage.saveTasks(new ArrayList<>()));
    }
}
