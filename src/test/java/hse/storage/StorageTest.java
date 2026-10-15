package hse.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hse.task.Deadline;
import hse.task.Event;
import hse.task.Task;
import hse.task.TaskList;
import hse.task.ToDos;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyTaskList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().getTasks().isEmpty());
    }

    @Test
    void saveAndLoad_tasks_roundTripsTaskDetailsAndCompletionStatus() throws IOException {
        ToDos todo = new ToDos("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 3).atTime(14, 30));
        Event event = new Event("meeting", LocalDate.of(2026, 9, 4).atTime(10, 0),
                LocalDate.of(2026, 9, 4).atTime(11, 0));
        deadline.markAsDone();
        Storage storage = new Storage(temporaryDirectory.resolve("nested/tasks.txt").toString());

        storage.save(new TaskList(List.of(todo, deadline, event)));

        List<String> loadedTaskStrings = storage.load().getTasks().stream()
                .map(Task::toString)
                .toList();
        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()), loadedTaskStrings);
    }

    @Test
    void load_corruptLine_ignoresLineAndKeepsValidTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "[T][ ] read book" + System.lineSeparator()
                + "not a task" + System.lineSeparator()
                + "[D][ ] invalid deadline (by: Sep 31 2026)");
        Storage storage = new Storage(dataFile.toString());

        assertEquals(List.of("[T][ ] read book"), storage.load().getTasks().stream()
                .map(Task::toString)
                .toList());
    }
}
