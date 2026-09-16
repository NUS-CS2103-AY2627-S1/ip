package arin.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import arin.task.Deadline;
import arin.task.Event;
import arin.task.Task;
import arin.task.Todo;

/**
 * Tests task-file writing by {@link Storage}.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveTasks_tasksOfAllTypes_writesRequiredStorageFormat() throws Exception {
        Path taskFile = temporaryDirectory.resolve("data/arin.txt");
        Todo todo = new Todo("read book");
        todo.markTask();
        List<Task> tasks = List.of(
                todo,
                new Deadline("return book", LocalDate.of(2019, 12, 2)),
                new Event("project meeting", "Aug 6th 2pm", "Aug 6th 4pm")
        );

        new Storage(taskFile).saveTasks(tasks);

        assertTrue(Files.exists(taskFile));
        assertEquals(List.of(
                "T | X | read book",
                "D |   | return book | 2019-12-02",
                "E |   | project meeting | Aug 6th 2pm | Aug 6th 4pm"
        ), Files.readAllLines(taskFile));
    }

    @Test
    void saveTasks_existingFile_replacesPreviousTasks() throws Exception {
        Path taskFile = temporaryDirectory.resolve("arin.txt");
        Storage storage = new Storage(taskFile);
        storage.saveTasks(List.of(new Todo("old task")));

        storage.saveTasks(List.of(new Todo("new task")));

        assertEquals(List.of("T |   | new task"), Files.readAllLines(taskFile));
    }

    @Test
    void saveTasks_emptyList_createsEmptyTaskFile() throws Exception {
        Path taskFile = temporaryDirectory.resolve("arin.txt");

        new Storage(taskFile).saveTasks(List.of());

        assertTrue(Files.exists(taskFile));
        assertEquals(List.of(), Files.readAllLines(taskFile));
    }

    @Test
    void saveTasks_duplicateTasks_preservesOrderAndDuplicates() throws Exception {
        Path taskFile = temporaryDirectory.resolve("arin.txt");
        Task readBook = new Todo("read book");
        readBook.markTask();
        List<Task> tasks = List.of(readBook, new Todo("buy milk"), readBook);

        new Storage(taskFile).saveTasks(tasks);

        assertEquals(List.of("T | X | read book", "T |   | buy milk", "T | X | read book"),
                Files.readAllLines(taskFile));
    }
}
