package apollo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Task;
import apollo.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws IOException {
        // Setting up
        Path missingFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(missingFile.toString());

        // Action to test
        List<Task> tasks = storage.load();

        // Assertion
        Assertions.assertTrue(tasks.isEmpty());
    }

    @Test
    public void save_todo_writesExpectedFileContent() throws IOException {
        // Setting up
        Path tempFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(tempFile.toString());
        Todo todo = new Todo("test 1");

        // Action to test
        storage.save(List.of(todo));

        // Assertion
        List<String> lines = Files.readAllLines(tempFile);
        Assertions.assertEquals(List.of("T | 0 | test 1"), lines);
    }

    @Test
    public void load_todoFile_returnsTodo() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Files.write(tempFile, List.of("T | 0 | test todo"));
        Storage storage = new Storage(tempFile.toString());

        List<Task> tasks = storage.load();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertInstanceOf(Todo.class, tasks.get(0));
        Assertions.assertEquals("T | 0 | test todo", tasks.get(0).toFileString());
    }

    @Test
    public void load_eventFile_returnsEvent() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Files.write(tempFile, List.of("E | 0 | test event | 2026-08-24T00:00:00 | 2026-08-25T23:59:00"));
        Storage storage = new Storage(tempFile.toString());

        List<Task> tasks = storage.load();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertInstanceOf(Event.class, tasks.get(0));
        Assertions.assertEquals(
                "E | 0 | test event | 2026-08-24T00:00:00 | 2026-08-25T23:59:00",
                tasks.get(0).toFileString());
    }

    @Test
    public void load_deadlineFile_returnsDeadline() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Files.write(tempFile, List.of("D | 0 | test deadline | 2026-08-25T23:59:00"));
        Storage storage = new Storage(tempFile.toString());

        List<Task> tasks = storage.load();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertInstanceOf(Deadline.class, tasks.get(0));
        Assertions.assertEquals("D | 0 | test deadline | 2026-08-25T23:59:00", tasks.get(0).toFileString());
    }

    @Test
    public void saveAndLoad_mixedMarkedTasks_preservesAllTaskData() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(tempFile.toString());
        Todo todo = new Todo("read textbook");
        todo.markAsDone(true);
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 8, 25, 23, 59));
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 8, 26, 9, 0),
                LocalDateTime.of(2026, 8, 26, 10, 30));
        event.markAsDone(true);

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        Assertions.assertEquals(3, loadedTasks.size());
        Assertions.assertInstanceOf(Todo.class, loadedTasks.get(0));
        Assertions.assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Assertions.assertInstanceOf(Event.class, loadedTasks.get(2));
        Assertions.assertEquals(todo.toFileString(), loadedTasks.get(0).toFileString());
        Assertions.assertEquals(deadline.toFileString(), loadedTasks.get(1).toFileString());
        Assertions.assertEquals(event.toFileString(), loadedTasks.get(2).toFileString());
        Assertions.assertTrue(loadedTasks.get(0).getIsDone());
        Assertions.assertFalse(loadedTasks.get(1).getIsDone());
        Assertions.assertTrue(loadedTasks.get(2).getIsDone());
    }

    @Test
    public void save_parentDirectoriesDoNotExist_createsDirectoriesAndFile() throws IOException {
        Path nestedFile = tempDir.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(nestedFile.toString());

        storage.save(List.of(new Todo("nested task")));

        Assertions.assertTrue(Files.isRegularFile(nestedFile));
        Assertions.assertEquals(List.of("T | 0 | nested task"), Files.readAllLines(nestedFile));
    }

    @Test
    public void load_unknownTaskType_ignoresUnknownLineAndLoadsValidTasks() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Files.write(tempFile, List.of(
                "X | 0 | unsupported task",
                "T | 0 | valid todo"));
        Storage storage = new Storage(tempFile.toString());

        List<Task> tasks = storage.load();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals("T | 0 | valid todo", tasks.get(0).toFileString());
    }

}
