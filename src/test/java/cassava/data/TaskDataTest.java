package cassava.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cassava.task.DeadlineTask;
import cassava.task.EventTask;
import cassava.task.Task;
import cassava.task.ToDoTask;

public class TaskDataTest {

    @TempDir
    Path tempDir;

    private Path taskFile() {
        return tempDir.resolve("tasks.txt");
    }

    @Test
    public void testGetTasks_emptyFile_returnsEmptyList() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.createFile(path);

        List<Task> tasks = TaskData.getTasks(path);

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testPutThenGetTasks_toDoTask_roundTrips() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.createFile(path);

        List<Task> original = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));
        TaskData.putTasks(original, path);

        List<Task> loaded = TaskData.getTasks(path);

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).equals(original.get(0)));
    }

    @Test
    public void testPutThenGetTasks_deadlineTask_roundTrips() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.createFile(path);

        List<Task> original = new ArrayList<>(Arrays.asList(
                new DeadlineTask("finish assignment", true, Task.TaskPriority.HIGH, LocalDate.parse("2026-08-28"))
        ));
        TaskData.putTasks(original, path);

        List<Task> loaded = TaskData.getTasks(path);

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).equals(original.get(0)));
        assertEquals(original.get(0).toString(), loaded.get(0).toString());
    }

    @Test
    public void testPutThenGetTasks_eventTask_roundTrips() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.createFile(path);

        List<Task> original = new ArrayList<>(Arrays.asList(
                new EventTask("project meeting", false, Task.TaskPriority.LOW,
                        LocalDate.parse("2026-08-28"), LocalDate.parse("2026-08-29"))
        ));
        TaskData.putTasks(original, path);

        List<Task> loaded = TaskData.getTasks(path);

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).equals(original.get(0)));
        assertEquals(original.get(0).toString(), loaded.get(0).toString());
    }

    @Test
    public void testPutThenGetTasks_mixedTasksWithPriorities_roundTrips() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.createFile(path);

        List<Task> original = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants", false, Task.TaskPriority.UNKNOWN),
                new DeadlineTask("finish assignment", true, Task.TaskPriority.MEDIUM, LocalDate.parse("2026-08-28")),
                new EventTask("project meeting", false, Task.TaskPriority.HIGH,
                        LocalDate.parse("2026-08-28"), LocalDate.parse("2026-08-29"))
        ));
        TaskData.putTasks(original, path);

        List<Task> loaded = TaskData.getTasks(path);

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertTrue(loaded.get(i).equals(original.get(i)));
        }
    }

    @Test
    public void testGetTasks_unrecognisedTaskType_throwsFileFormatException() throws IOException {
        Path path = taskFile();
        Files.write(path, List.of("X | 0 | mystery task | unknown"));

        assertThrows(FileFormatException.class, () -> TaskData.getTasks(path));
    }

    @Test
    public void testGetTasks_toDoTaskLine_parsesCorrectly() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.write(path, List.of("T | 1 | water the plants | high"));

        List<Task> tasks = TaskData.getTasks(path);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).equals(
                new ToDoTask("water the plants", true, Task.TaskPriority.HIGH)));
    }

    @Test
    public void testGetTasks_deadlineTaskLine_parsesCorrectDate() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.write(path, List.of("D | 0 | finish assignment | unknown | 2026-08-28"));

        List<Task> tasks = TaskData.getTasks(path);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).toString().contains("Aug 28 2026"));
    }

    @Test
    public void testGetTasks_eventTaskLine_parsesCorrectDates() throws IOException, FileFormatException {
        Path path = taskFile();
        Files.write(path, List.of("E | 0 | project meeting | unknown | 2026-08-28 | 2026-08-29"));

        List<Task> tasks = TaskData.getTasks(path);

        assertEquals(1, tasks.size());
        String result = tasks.get(0).toString();
        assertTrue(result.contains("Aug 28 2026"));
        assertTrue(result.contains("Aug 29 2026"));
    }

}
