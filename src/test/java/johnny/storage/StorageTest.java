package johnny.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import johnny.task.Deadline;
import johnny.task.Event;
import johnny.task.Task;
import johnny.task.TaskList;
import johnny.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    private Storage storageAt(String filename) {
        return new Storage(tempDir.resolve(filename).toString());
    }

    // --- load ---

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws IOException {
        Storage storage = storageAt("missing.txt");
        ArrayList<Task> tasks = storage.load();
        assertEquals(0, tasks.size());
    }

    @Test
    public void load_todoLine_parsesCorrectly() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | read book\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void load_todoDone_parsesCorrectly() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 1 | read book\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    public void load_deadlineLine_parsesCorrectly() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | homework | 2024-09-15\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).toString().contains("homework"));
        assertEquals("D | 0 | homework | 2024-09-15", tasks.get(0).toFileString());
    }

    @Test
    public void load_eventLine_parsesCorrectly() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "E | 0 | meeting | 2024-03-01 | 2024-03-03\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("E | 0 | meeting | 2024-03-01 | 2024-03-03",
                tasks.get(0).toFileString());
    }

    @Test
    public void load_multipleTasks_parsesAll() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file,
                "T | 1 | read book\n"
                + "D | 0 | homework | 2024-09-15\n"
                + "E | 0 | meeting | 2024-03-01 | 2024-03-03\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(3, tasks.size());
    }

    @Test
    public void load_corruptedLine_skipsIt() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file,
                "T | 1 | read book\n"
                + "this is garbage\n"
                + "D | 0 | homework | 2024-09-15\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(2, tasks.size());
    }

    @Test
    public void load_emptyLines_skipsIt() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file,
                "T | 0 | read book\n"
                + "\n"
                + "   \n"
                + "T | 0 | return book\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(2, tasks.size());
    }

    @Test
    public void load_unknownType_skipsLine() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "X | 0 | mystery task\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(0, tasks.size());
    }

    @Test
    public void load_invalidDate_skipsLine() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | homework | not-a-date\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(0, tasks.size());
    }

    @Test
    public void load_missingFields_skipsLine() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "D | 0\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertEquals(0, tasks.size());
    }

    @Test
    public void load_flexiblePipeSpacing_parsesCorrectly() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "  T|1|read book  \n");
        Storage storage = new Storage(file.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    public void load_invalidStatus_skipsLine() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | yes | read book\n");
        Storage storage = new Storage(file.toString());

        assertEquals(0, storage.load().size());
    }

    @Test
    public void load_emptyDescription_skipsLine() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 0 |   \n");
        Storage storage = new Storage(file.toString());

        assertEquals(0, storage.load().size());
    }

    @Test
    public void load_extraFields_skipsLine() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | read book | unexpected\n");
        Storage storage = new Storage(file.toString());

        assertEquals(0, storage.load().size());
    }

    @Test
    public void load_invalidEventDateRange_skipsLine() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "E | 0 | meeting | 2024-03-03 | 2024-03-01\n");
        Storage storage = new Storage(file.toString());

        assertEquals(0, storage.load().size());
    }

    // --- save ---

    @Test
    public void save_emptyList_createsEmptyFile() throws IOException {
        Path file = tempDir.resolve("sub/tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.save(new TaskList());
        assertTrue(Files.exists(file));
        assertEquals("", Files.readString(file));
    }

    @Test
    public void save_multipleTasks_writesCorrectFormat() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Deadline d = new Deadline("homework", LocalDate.of(2024, 9, 15));
        d.markAsDone();
        tasks.add(d);
        tasks.add(new Event("meeting",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3)));
        storage.save(tasks);
        String content = Files.readString(file);
        String[] lines = content.strip().split(System.lineSeparator());
        assertEquals(3, lines.length);
        assertEquals("T | 0 | read book", lines[0]);
        assertEquals("D | 1 | homework | 2024-09-15", lines[1]);
        assertEquals("E | 0 | meeting | 2024-03-01 | 2024-03-03", lines[2]);
    }

    @Test
    public void save_createsParentDirectories() throws IOException {
        Path file = tempDir.resolve("a/b/c/tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("test"));
        storage.save(tasks);
        assertTrue(Files.exists(file));
    }

    // --- round-trip ---

    @Test
    public void saveAndLoad_roundTrip_preservesAllData() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("homework", LocalDate.of(2024, 9, 15)));
        Event event = new Event("meeting",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));
        event.markAsDone();
        original.add(event);

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), loaded.get(i).toString());
            assertEquals(original.get(i).toFileString(), loaded.get(i).toFileString());
        }
    }
}
