package bogos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for saving tasks to and loading tasks from a data file.
 */
public class StorageTest {
    /**
     * A JUnit-managed directory that is unique to each test.
     */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that all supported task types are written in storage format.
     *
     * @throws IOException If the test cannot read the temporary data file.
     */
    @Test
    public void saveTasks_withMultipleTaskTypes_writesExpectedFileContents() throws IOException {
        Path file = temporaryDirectory.resolve("bogos.txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        List<Task> tasks = List.of(
                todo,
                new Deadline("return book", LocalDate.of(2026, 9, 15), List.of("library")),
                new Event("project meeting", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 16),
                        List.of("CS2103", "team-a")));

        storage.saveTasks(tasks, new Ui());

        assertEquals(List.of(
                "T | true | borrow book",
                "D | false | return book | 2026-09-15 | library",
                "E | false | project meeting | 2026-09-15 | 2026-09-16 | CS2103 | team-a"),
                Files.readAllLines(file));
    }

    /**
     * Verifies that valid stored task records are reconstructed correctly.
     *
     * @throws IOException If the test cannot write the temporary data file.
     */
    @Test
    public void loadTasks_withValidTaskRecords_returnsMatchingTasks() throws IOException {
        Path file = temporaryDirectory.resolve("bogos.txt");
        Files.write(file, List.of(
                "T | false | borrow book",
                "D | false | return book | 2026-09-15 | library",
                "E | false | project meeting | 2026-09-15 | 2026-09-16 | CS2103 | team-a"));
        Storage storage = new Storage(file);

        List<Task> tasks = storage.loadTasks(new Ui());

        assertEquals(3, tasks.size());
        assertEquals("borrow book", tasks.get(0).getDescription());
        assertEquals(List.of(), tasks.get(0).getTags());

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), deadline.getDueDate());
        assertEquals(List.of("library"), deadline.getTags());

        Event event = assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), event.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 16), event.getEndDate());
        assertEquals(List.of("CS2103", "team-a"), event.getTags());
    }

    /**
     * Verifies that a stored completed task remains complete after loading.
     *
     * @throws IOException If the test cannot write the temporary data file.
     */
    @Test
    public void loadTasks_withCompletedTask_marksTaskAsDone() throws IOException {
        Path file = temporaryDirectory.resolve("bogos.txt");
        Files.write(file, List.of("T | true | borrow book"));
        Storage storage = new Storage(file);

        List<Task> tasks = storage.loadTasks(new Ui());

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone());
    }

    /**
     * Verifies that loading from a missing data file returns no tasks.
     */
    @Test
    public void loadTasks_whenFileDoesNotExist_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        List<Task> tasks = storage.loadTasks(new Ui());

        assertTrue(tasks.isEmpty());
    }

    /**
     * Verifies that invalid stored records are skipped while valid ones are loaded.
     *
     * @throws IOException If the test cannot write the temporary data file.
     */
    @Test
    public void loadTasks_withInvalidRecord_skipsInvalidRecord() throws IOException {
        Path file = temporaryDirectory.resolve("bogos.txt");
        Files.write(file, List.of(
                "T | false | borrow book",
                "X | false | unknown task"));
        Storage storage = new Storage(file);

        List<Task> tasks = storage.loadTasks(new Ui());

        assertEquals(1, tasks.size());
        Todo todo = assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("borrow book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    /**
     * Verifies that a record containing an empty tag is skipped in full.
     *
     * @throws IOException If the test cannot write the temporary data file.
     */
    @Test
    public void loadTasks_withEmptyTag_skipsInvalidRecord() throws IOException {
        Path file = temporaryDirectory.resolve("bogos.txt");
        Files.write(file, List.of(
                "T | false | borrow book | reading",
                "D | false | return book | 2026-09-15 | "));
        Storage storage = new Storage(file);

        List<Task> tasks = storage.loadTasks(new Ui());

        assertEquals(1, tasks.size());
        assertEquals(List.of("reading"), tasks.get(0).getTags());
    }

    /**
     * Verifies that a record containing duplicate tags is skipped in full.
     *
     * @throws IOException If the test cannot write the temporary data file.
     */
    @Test
    public void loadTasks_withDuplicateTags_skipsInvalidRecord() throws IOException {
        Path file = temporaryDirectory.resolve("bogos.txt");
        Files.write(file, List.of("T | false | borrow book | reading | reading"));
        Storage storage = new Storage(file);

        List<Task> tasks = storage.loadTasks(new Ui());

        assertTrue(tasks.isEmpty());
    }
}
