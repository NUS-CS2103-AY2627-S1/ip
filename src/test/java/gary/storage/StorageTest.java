package gary.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gary.task.Deadline;
import gary.task.Event;
import gary.task.Task;
import gary.task.Todo;

class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void loadTasks_fileMissing_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveAndLoadTasks_mixedTaskTypes_preservesData() throws IOException {
        Path filePath = tempDirectory.resolve("data").resolve("gary.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read book");
        todo.markAsDone();
        List<Task> tasks = List.of(
                todo,
                new Deadline("return book", LocalDate.parse("2019-12-02")),
                new Event("project meeting", LocalDate.parse("2019-08-06"),
                        LocalDate.parse("2019-08-07"))
        );

        storage.saveTasks(tasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-12-02",
                "E | 0 | project meeting | 2019-08-06 | 2019-08-07"
        ), Files.readAllLines(filePath, StandardCharsets.UTF_8));
        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: 2019-12-02)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: 2019-08-06 to: 2019-08-07)",
                loadedTasks.get(2).toString());
    }

    @Test
    void loadTasks_corruptedRecords_skipsInvalidLines() throws IOException {
        Path filePath = tempDirectory.resolve("gary.txt");
        Files.write(filePath, List.of(
                "T | 1 | read book",
                "D | 0 | missing date",
                "D | 0 | return book | not-a-date",
                "E | 0 | project meeting | 2019-08-06 | 2019-08-07",
                "X | 0 | unsupported task"
        ), StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[E][ ] project meeting (from: 2019-08-06 to: 2019-08-07)",
                loadedTasks.get(1).toString());
    }

    @Test
    void saveTasks_parentDirectoryMissing_createsDirectory() throws IOException {
        Path filePath = tempDirectory.resolve("nested").resolve("data").resolve("gary.txt");
        Storage storage = new Storage(filePath);

        storage.saveTasks(List.of(new Todo("read book")));

        assertTrue(Files.isRegularFile(filePath));
    }
}
