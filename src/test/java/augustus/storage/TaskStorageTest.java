package augustus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import augustus.exception.AugustusException;
import augustus.task.Deadline;
import augustus.task.Event;
import augustus.task.Task;
import augustus.task.ToDo;

public class TaskStorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void loadTasks_validFile_loadsTasksCorrectly()
            throws IOException, AugustusException {

        Path file = tempDir.resolve("augustus.txt");

        String data = "T | 1 | read book\n"
                + "D | 0 | submit assignment | 2026-09-10\n"
                + "E | 0 | project meeting | 2pm | 4pm\n";

        Files.writeString(file, data);

        TaskStorage storage = new TaskStorage(file.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("T | 1 | read book", tasks.get(0).toFileString());
        assertEquals("D | 0 | submit assignment | 2026-09-10", tasks.get(1).toFileString());
        assertEquals("E | 0 | project meeting | 2pm | 4pm", tasks.get(2).toFileString());
    }

    @Test
    public void loadTasks_emptyFile_returnsEmptyList()
            throws IOException, AugustusException {

        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        TaskStorage storage = new TaskStorage(file.toString());
        ArrayList<Task> tasks = storage.loadTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    public void saveTasks_validTasks_savesCorrectly()
            throws IOException, AugustusException {

        Path file = tempDir.resolve("augustus.txt");

        TaskStorage storage = new TaskStorage(file.toString());
        storage.createFile();

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("submit assignment", LocalDate.of(2026, 9, 10)));
        tasks.add(new Event("project meeting", "2pm", "4pm"));

        storage.saveTasks(tasks);
        String result = Files.readString(file);

        String expected = "T | 0 | read book\n"
                + "D | 0 | submit assignment | 2026-09-10\n"
                + "E | 0 | project meeting | 2pm | 4pm\n";

        assertEquals(expected, result);
    }

    @Test
    public void saveAndLoadTasks_withTags_tagsPreserved()
            throws IOException, AugustusException {

        Path file = tempDir.resolve("taggedTasks.txt");

        TaskStorage storage = new TaskStorage(file.toString());
        storage.createFile();

        ArrayList<Task> tasks = new ArrayList<>();

        Task todo = new ToDo("read book");
        todo.setTag("school work");

        Task deadline = new Deadline(
                "submit assignment",
                LocalDate.of(2026, 9, 10));
        deadline.setTag("urgent");

        Task event = new Event(
                "project meeting",
                "2pm",
                "4pm");
        event.setTag("group project");

        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.saveTasks(tasks);

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("school work", loadedTasks.get(0).getTag());
        assertEquals("urgent", loadedTasks.get(1).getTag());
        assertEquals("group project", loadedTasks.get(2).getTag());
    }

    @Test
    public void createFile_missingParentDirectory_createsFile()
            throws AugustusException {

        Path file = tempDir.resolve("data").resolve("augustus.txt");

        TaskStorage storage = new TaskStorage(file.toString());
        storage.createFile();

        assertTrue(Files.exists(file));
    }
}
