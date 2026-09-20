package larper.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import larper.task.Deadline;
import larper.task.Event;
import larper.task.Task;
import larper.task.Todo;

public class StorageTest {
    @TempDir
    public Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("larperdata.txt"));

        ArrayList<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
        assertTrue(storage.getSkippedLineNumbers().isEmpty());
    }

    @Test
    public void saveTasks_newFolder_fileCreatedWithTaskLines() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data").resolve("larperdata.txt");
        Storage storage = new Storage(dataFile);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", LocalDate.of(2019, 10, 15), "1400"));
        tasks.add(new Event("meeting", LocalDate.of(2026, 8, 24), "1400",
                LocalDate.of(2026, 8, 25), "1600"));

        storage.saveTasks(tasks);

        assertTrue(Files.exists(dataFile));
        assertEquals("""
                T | 0 | read book
                D | 0 | submit report | 2019-10-15 | 1400
                E | 0 | meeting | 2026-08-24 | 1400 | 2026-08-25 | 1600
                """, Files.readString(dataFile));
    }

    @Test
    public void loadTasks_savedFile_taskTypesAndStateRestored() throws Exception {
        Path dataFile = temporaryDirectory.resolve("larperdata.txt");
        Files.writeString(dataFile, """
                T | 1 | read book
                D | 0 | submit report | 2019-10-15 | 1400
                E | 0 | meeting | 2026-08-24 | 1400 | 2026-08-25 | 1600
                """);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertTrue(tasks.get(0).isDone());
        assertEquals("[T][X] read book", tasks.get(0).toString());

        assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("[D][ ] submit report (by: Oct 15 2019 1400)", tasks.get(1).toString());

        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("[E][ ] meeting (from: Aug 24 2026 1400 to: Aug 25 2026 1600)",
                tasks.get(2).toString());
    }

    @Test
    public void saveAndLoadTasks_roundTrip_taskDataRetained() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data").resolve("larperdata.txt");
        Storage storage = new Storage(dataFile);
        ArrayList<Task> originalTasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(new Deadline("submit report", LocalDate.of(2019, 10, 15), "1400"));
        originalTasks.add(new Event("meeting", LocalDate.of(2026, 8, 24), "no time",
                LocalDate.of(2026, 8, 25), "1600"));

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] submit report (by: Oct 15 2019 1400)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] meeting (from: Aug 24 2026 to: Aug 25 2026 1600)",
                loadedTasks.get(2).toString());
    }

    @Test
    public void loadTasks_legacyDeadlineLine_taskRestored() throws Exception {
        Path dataFile = temporaryDirectory.resolve("larperdata.txt");
        Files.writeString(dataFile, """
                D | 0 | return library book | by 2/12/2019 1800
                """);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("[D][ ] return library book (by: Dec 02 2019 1800)", tasks.get(0).toString());
    }

    @Test
    public void loadTasks_legacyEventLine_taskRestored() throws Exception {
        Path dataFile = temporaryDirectory.resolve("larperdata.txt");
        Files.writeString(dataFile, """
                E | 0 | project meeting | 8 aug 2pm | 8 aug 4pm
                """);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("[E][ ] project meeting (from: Aug 08 2026 1400 to: Aug 08 2026 1600)",
                tasks.get(0).toString());
    }

    @Test
    public void loadTasks_malformedLine_validLinesStillLoaded() throws Exception {
        Path dataFile = temporaryDirectory.resolve("larperdata.txt");
        Files.writeString(dataFile, """
                T | 1 | read book
                this is not valid
                D | 0 | submit report | 2019-10-15 | 1400
                """);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] submit report (by: Oct 15 2019 1400)", tasks.get(1).toString());
        assertEquals(List.of(2), storage.getSkippedLineNumbers());
    }

    @Test
    public void saveAndLoadTasks_taggedTasks_tagsRetainedAtEndOfFormats() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tagged-larperdata.txt");
        Storage storage = new Storage(dataFile);
        ArrayList<Task> originalTasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.addTag("#FUN");
        originalTasks.add(todo);
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 23), "no time");
        deadline.addTag("school");
        originalTasks.add(deadline);

        storage.saveTasks(originalTasks);

        assertEquals("T | 0 | read book | #fun\n"
                + "D | 0 | return book | 2026-08-23 | no time | #school\n", Files.readString(dataFile));

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals("[T][ ] read book [#fun]", loadedTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Aug 23 2026) [#school]", loadedTasks.get(1).toString());
    }
}
