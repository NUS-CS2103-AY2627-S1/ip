package sioet.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import sioet.storage.Storage;

/**
 * Tests saving and loading tasks to the hard disk.
 */
public class StorageTest {
    private static final Path FILE_PATH = Path.of("data", "sioet.txt");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private final Storage storage = new Storage();

    /**
     * Removes the saved file after each test.
     */
    @AfterEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(FILE_PATH);
    }

    /**
     * Tests that a todo task is saved correctly.
     */
    @Test
    public void saveTodo() throws IOException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertTrue(Files.exists(FILE_PATH));
        assertEquals(
                "T | 0 | read book",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that a completed todo task is saved correctly.
     */
    @Test
    public void saveCompletedTodo() throws IOException {
        TaskList tasks = new TaskList();

        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);

        storage.save(tasks);

        assertEquals(
                "T | 1 | read book",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that a deadline task is saved correctly.
     */
    @Test
    public void saveDeadline() throws IOException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline(
                "return book",
                LocalDateTime.of(2026, 6, 6, 23, 59)
        ));
        storage.save(tasks);

        assertEquals(
                "D | 0 | return book | 6/6/2026 2359",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that an event task is saved correctly.
     */
    @Test
    public void saveEvent() throws IOException {
        TaskList tasks = new TaskList();
        tasks.add(new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0)
        ));

        storage.save(tasks);

        assertEquals(
                "E | 0 | project meeting | 6/8/2026 1400 | 6/8/2026 1600",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that a recurring task is saved correctly.
     */
    @Test
    public void saveRecurringTask() throws IOException {
        TaskList tasks = new TaskList();
        tasks.add(new RecurringTask("project meeting", "week"));

        storage.save(tasks);

        assertEquals(
                "R | 0 | project meeting | week",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that multiple tasks are saved in the correct order.
     */
    @Test
    public void saveMultipleTasks() throws IOException {
        TaskList tasks = new TaskList();

        Todo todo = new Todo("read book");
        todo.markAsDone();

        tasks.add(todo);
        tasks.add(new Deadline(
                "return book",
                LocalDateTime.of(2026, 6, 6, 23, 59)
        ));
        tasks.add(new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0)
        ));

        storage.save(tasks);

        assertEquals(
                "T | 1 | read book\n"
                        + "D | 0 | return book | 6/6/2026 2359\n"
                        + "E | 0 | project meeting | 6/8/2026 1400 | 6/8/2026 1600",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that a todo task is loaded correctly.
     */
    @Test
    public void loadTodo() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(FILE_PATH, "T | 0 | read book");

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Tests that a completed todo task is loaded with the correct status.
     */
    @Test
    public void loadCompletedTodo() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(FILE_PATH, "T | 1 | read book");

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone());
    }

    /**
     * Tests that a deadline task is loaded correctly.
     */
    @Test
    public void loadDeadline() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(
                FILE_PATH,
                "D | 0 | return book | 6/6/2026 2359"
        );

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(
                "[D][ ] return book (by: Jun 06 2026, 11:59 PM)",
                tasks.get(0).toString()
        );
    }

    /**
     * Tests that an event task is loaded correctly.
     */
    @Test
    public void loadEvent() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(
                FILE_PATH,
                "E | 0 | project meeting | 6/8/2026 1400 | 6/8/2026 1600"
        );

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(
                "[E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)",
                tasks.get(0).toString()
        );
    }

    /**
     * Tests that a recurring task is loaded correctly.
     */
    @Test
    public void loadRecurringTask() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "R | 0 | project meeting | week"
        );

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(
                "[R][ ] project meeting (every week)",
                tasks.get(0).toString()
        );
    }

    /**
     * Tests that multiple tasks are loaded in the correct order
     * with their completion statuses preserved.
     */
    @Test
    public void loadMultipleTasks() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 1 | read book\n"
                        + "D | 0 | return book | 6/6/2026 2359\n"
                        + "E | 0 | project meeting | 6/8/2026 1400 | 6/8/2026 1600"
        );

        TaskList tasks = storage.load();

        assertEquals(3, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertEquals(
                "[D][ ] return book (by: Jun 06 2026, 11:59 PM)",
                tasks.get(1).toString()
        );
        assertEquals(
                "[E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)",
                tasks.get(2).toString()
        );
    }

    /**
     * Tests that an empty task list is returned when the data file does not exist.
     */
    @Test
    public void loadMissingFile() throws IOException {
        Files.deleteIfExists(FILE_PATH);

        TaskList tasks = storage.load();

        assertEquals(0, tasks.size());
    }

    /**
     * Tests that blank lines in the data file are ignored.
     */
    @Test
    public void loadIgnoresBlankLines() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "\n"
                        + "T | 0 | read book\n"
                        + "\n"
        );

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Tests that corrupted task entries are skipped while valid tasks are loaded.
     */
    @Test
    public void loadSkipsCorruptedTask() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 0 | read book\n"
                        + "THIS IS CORRUPTED\n"
                        + "D | 0 | return book | 6/6/2026 2359"
        );

        TaskList tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals(
                "[D][ ] return book (by: Jun 06 2026, 11:59 PM)",
                tasks.get(1).toString()
        );
    }

    /**
     * Tests that an unknown task type is skipped.
     */
    @Test
    public void loadSkipsUnknownTaskType() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 0 | read book\n"
                        + "X | 0 | invalid task\n"
                        + "D | 0 | return book | 6/6/2026 2359"
        );

        TaskList tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals(
                "[D][ ] return book (by: Jun 06 2026, 11:59 PM)",
                tasks.get(1).toString()
        );
    }

    /**
     * Tests that task entries with missing or extra fields are skipped.
     */
    @Test
    public void loadSkipsIncorrectFormat() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 0\n"
                        + "D | 0 | return book\n"
                        + "E | 0 | meeting | 2pm\n"
                        + "T | 0 | valid task"
        );

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] valid task", tasks.get(0).toString());
    }
}
