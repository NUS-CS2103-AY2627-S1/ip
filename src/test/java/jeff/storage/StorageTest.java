package jeff.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.Task;
import jeff.task.Todo;

public class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void saveAndLoadTasks_multipleTaskTypes_restoresTasks()
            throws IOException {
        Path filePath =
                temporaryDirectory.resolve("data").resolve("jeff.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> originalTasks = new ArrayList<>();

        Todo todo = new Todo("read book");
        todo.markAsDone();

        originalTasks.add(todo);
        originalTasks.add(
                new Deadline(
                        "return book",
                        LocalDate.of(2026, 9, 15)));
        originalTasks.add(
                new Event(
                        "project meeting",
                        "1400",
                        "1600"));

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());

        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).isDone());

        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Deadline loadedDeadline =
                (Deadline) loadedTasks.get(1);
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(
                LocalDate.of(2026, 9, 15),
                loadedDeadline.getDueDate());
        assertFalse(loadedDeadline.isDone());

        assertInstanceOf(Event.class, loadedTasks.get(2));
        Event loadedEvent = (Event) loadedTasks.get(2);
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals("1400", loadedEvent.getStartTime());
        assertEquals("1600", loadedEvent.getEndTime());
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyList()
            throws IOException {
        Path filePath =
                temporaryDirectory.resolve("missing").resolve("jeff.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    public void saveTasks_missingParentDirectory_createsFile()
            throws IOException {
        Path filePath =
                temporaryDirectory.resolve("new-data").resolve("jeff.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.saveTasks(tasks);

        assertTrue(filePath.toFile().exists());
    }

    @Test
    public void loadTasks_invalidRecord_throwsWithoutChangingFile()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        String[] invalidRecords = {
            "T | 2 | invalid status",
            "T | 0",
            "T | 0 | ",
            "T | 0 | task | extra",
            "X | 0 | unknown type",
            "D | 0 | report | 2026-02-30",
            "E | 0 | meeting | 1400 | ",
            "T | 0 | milk|bread",
            ""
        };

        for (String invalidRecord : invalidRecords) {
            String contents = "T | 0 | valid task\n"
                    + invalidRecord + "\n";
            Files.writeString(filePath, contents);

            IOException exception = assertThrows(
                    IOException.class, () -> storage.loadTasks());

            assertEquals(
                    "Invalid saved task at line 2.",
                    exception.getMessage());
            assertEquals(contents, Files.readString(filePath));
        }
    }

    @Test
    public void loadTasks_emptyFile_returnsEmptyList()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "");
        Storage storage = new Storage(filePath.toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void loadTasks_directoryInsteadOfFile_throwsIoException()
            throws IOException {
        Path directory = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(directory);
        Storage storage = new Storage(directory.toString());

        assertThrows(IOException.class, () -> storage.loadTasks());
    }
}
