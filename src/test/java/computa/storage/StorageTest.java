package computa.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import computa.task.Deadline;
import computa.task.Event;
import computa.task.Task;
import computa.task.Todo;

/** Tests persistence, reconstruction, and corruption handling for task storage. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void initialiseDataFile_missingParent_createsParentAndFile() {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.initialiseDataFile();

        assertTrue(Files.isRegularFile(dataFile));
    }

    @Test
    void loadTasks_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveTasks_thenLoadTasks_roundTripsTypesStatusesAndDates() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return book", "2019-10-15"));
        tasks.add(new Event("project meeting", "2019-10-15 1400", "2019-10-15 1600"));

        storage.saveTasks(tasks);
        ArrayList<Task> loaded = storage.loadTasks();

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)",
                loaded.get(2).toString());
    }

    @Test
    void loadTasks_corruptedRecords_areSkippedButValidRecordsRemain() throws Exception {
        Path dataFile = temporaryDirectory.resolve("corrupted.txt");
        Files.writeString(dataFile,
                "T | 0 | read book\n"
                        + "not a task record\n"
                        + "T | 2 | invalid status\n"
                        + "D | 0 | invalid deadline | 2019-02-30\n"
                        + "E | 0 | reversed event | 2019-10-16 | 2019-10-15\n"
                        + "D | 1 | return book | Sunday\n",
                StandardCharsets.UTF_8);

        ArrayList<Task> loaded = new Storage(dataFile.toString()).loadTasks();

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("[D][X] return book (by: Sunday)", loaded.get(1).toString());
    }
}
