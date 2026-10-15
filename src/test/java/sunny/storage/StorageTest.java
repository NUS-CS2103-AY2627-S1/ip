package sunny.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import task.Deadline;
import task.Task;
import task.ToDo;

public class StorageTest {
    Storage storage = Storage.getInstance();

    @TempDir
    Path tempDir;

    @Test
    public void saveTodoWritesCorrectFormat() throws IOException {
        storage.setFilePath(tempDir.resolve("test.txt").toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("buy milk"));

        storage.saveTasks(tasks);

        String contents = Files.readString(
                tempDir.resolve("test.txt")
        );

        assertEquals("T | buy milk | 0\r\n", contents); //apparently writer.newLine() writes \r\n
    }

    @Test
    public void saveMarkedTodoWritesCorrectFormat() throws IOException {
        storage.setFilePath(tempDir.resolve("test.txt").toString());

        ToDo todo = new ToDo("buy milk");
        todo.mark();

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);

        storage.saveTasks(tasks);

        String contents = Files.readString(
                tempDir.resolve("test.txt")
        );

        assertEquals("T | buy milk | 1\r\n", contents);
    }

    @Test
    public void saveMultipleTasksWritesAllTasks() throws IOException {
        storage.setFilePath(tempDir.resolve("test.txt").toString());

        ArrayList<Task> tasks = new ArrayList<>();

        tasks.add(new ToDo("buy milk"));

        ToDo homework = new ToDo("do homework");
        homework.mark();
        tasks.add(homework);

        storage.saveTasks(tasks);

        String contents = Files.readString(
                tempDir.resolve("test.txt")
        );

        String expected =
                "T | buy milk | 0\r\n" +
                        "T | do homework | 1\r\n";

        assertEquals(expected, contents);
    }

    @Test
    public void loadTodo() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "T | buy milk | 0\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof ToDo);
        assertEquals("buy milk", tasks.get(0).getDesc());
        assertFalse(tasks.get(0).isDone());
    }
    @Test
    public void loadMarkedTodo() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "T | buy milk | 1\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void invalidTodoFormatIsRejected() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "T | buy milk\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void corruptedFileIsDeleted() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "T | buy milk\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertTrue(tasks.isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    public void invalidCompletionStatusIsRejected() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "T | buy milk | 5\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertTrue(tasks.isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    public void unknownTaskTypeIsRejected() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "X | something | 0\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertTrue(tasks.isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    public void corruptedFileDoesNotPartiallyLoad() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "T | first task | 0\n" +
                        "T | second task | 1\n" +
                        "THIS IS CORRUPTED\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void loadDeadline() throws IOException {
        Path file = tempDir.resolve("test.txt");
        storage.setFilePath(file.toString());

        Files.writeString(
                file,
                "D | submit report | 24/08/2026 1430 | 0\n"
        );

        ArrayList<Task> tasks = new ArrayList<>();

        storage.loadTasks(tasks);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Deadline);
        assertEquals("submit report", tasks.get(0).getDesc());
        assertFalse(tasks.get(0).isDone());
    }
}
