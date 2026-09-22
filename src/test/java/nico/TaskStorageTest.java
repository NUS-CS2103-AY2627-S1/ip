package nico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void readTasks_prioritiesSaved_allTaskTypesRestored() throws NicoException {
        Path filePath = temporaryDirectory.resolve("priorities.txt");
        Task todo = new Todo("[high] read book");
        Task deadline = new Deadline("report", LocalDateTime.of(2026, 8, 25, 19, 0));
        Task event = new Event("meeting", LocalDateTime.of(2026, 8, 25, 19, 0),
                LocalDateTime.of(2026, 8, 25, 20, 0));
        todo.setPriority(Priority.HIGH);
        deadline.setPriority(Priority.MEDIUM);
        event.setPriority(Priority.LOW);
        todo.markAsDone();
        List<Task> tasks = List.of(todo, deadline, event, new Todo("[high] unassigned"));

        TaskStorage.writeAllTasks(filePath, tasks);
        List<Task> restored = TaskStorage.readTasks(filePath);

        assertEquals(tasks.stream().map(Task::toString).toList(),
                restored.stream().map(Task::toString).toList());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).getPriority(), restored.get(i).getPriority());
            assertEquals(tasks.get(i).getDescription(), restored.get(i).getDescription());
        }
    }

    @Test
    void createTasksFile_nestedMissingPath_fileAndParentDirectoriesCreated() throws IOException {
        Path filePath = temporaryDirectory.resolve("nested/tasks.txt");

        TaskStorage.createTasksFile(filePath);

        assertTrue(Files.isRegularFile(filePath));
    }

    @Test
    void readTasks_savedTasks_tasksRestoredWithTypesAndStatuses() throws IOException, NicoException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.write(filePath, List.of(
                "[T][X] read book",
                "[D][ ] return book (by: Aug 25 2026 19:00)",
                "[E][ ] project meeting (from: Aug 26 2026 14:00 to: Aug 26 2026 16:00)"));

        List<Task> tasks = TaskStorage.readTasks(filePath);

        assertEquals(List.of(
                "[T][X] read book",
                "[D][ ] return book (by: Aug 25 2026 19:00)",
                "[E][ ] project meeting (from: Aug 26 2026 14:00 to: Aug 26 2026 16:00)"),
                tasks.stream().map(Task::toString).toList());
    }

    @Test
    void readTasks_missingFile_emptyFileCreated() throws NicoException {
        Path filePath = temporaryDirectory.resolve("nested/tasks.txt");

        List<Task> tasks = TaskStorage.readTasks(filePath);

        assertTrue(tasks.isEmpty());
        assertTrue(Files.isRegularFile(filePath));
    }

    @Test
    void readTasks_invalidRecord_nicoExceptionThrown() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "invalid task record");

        NicoException exception = assertThrows(NicoException.class, () -> TaskStorage.readTasks(filePath));

        assertEquals("Aiyoh, got a task in tasks.txt I cannot understand.", exception.getMessage());
    }

    @Test
    void writeTask_multipleTasks_tasksAppendedInOrder() throws IOException, NicoException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");

        TaskStorage.writeTask(filePath, new Todo("read book"));
        TaskStorage.writeTask(filePath, new Todo("return book"));

        assertEquals(List.of("[T][ ] read book", "[T][ ] return book"), Files.readAllLines(filePath));
    }

    @Test
    void writeAllTasks_existingTasks_fileReplacedWithFormattedTasks() throws IOException, NicoException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        TaskStorage.writeTask(filePath, new Todo("old task"));
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 8, 25, 19, 0));

        TaskStorage.writeAllTasks(filePath, List.of(completedTodo, deadline));

        assertEquals(List.of(
                "[T][X] read book",
                "[D][ ] return book (by: Aug 25 2026 19:00)"), Files.readAllLines(filePath));
    }

    @Test
    void writeAllTasks_emptyList_existingFileEmptied() throws IOException, NicoException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        TaskStorage.writeTask(filePath, new Todo("read book"));

        TaskStorage.writeAllTasks(filePath, List.of());

        assertEquals(List.of(), Files.readAllLines(filePath));
    }

    @Test
    void writeTask_directoryPath_nicoExceptionThrown() {
        NicoException exception = assertThrows(NicoException.class, () ->
                TaskStorage.writeTask(temporaryDirectory, new Todo("read book")));

        assertEquals("Aiyoh, cannot save this task to tasks.txt.", exception.getMessage());
    }
}
