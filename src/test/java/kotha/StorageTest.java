package kotha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kotha.tasks.Constraint;
import kotha.tasks.Deadline;
import kotha.tasks.Event;
import kotha.tasks.Task;
import kotha.tasks.ToDo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_roundTripsEveryTaskTypeAndCompletionStatus() {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        ToDo todo = new ToDo("read | book");
        todo.setDone(true);
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2027, 5, 16, 11, 59));
        Event event = new Event("team meeting", LocalDateTime.of(2027, 5, 16, 9, 0),
                LocalDateTime.of(2027, 5, 16, 10, 0));
        Constraint textConstraint = new Constraint("return book", "exam");
        Constraint dateConstraint = new Constraint("call doctor", LocalDateTime.of(2027, 6, 1, 8, 30));

        storage.saveTasks(List.of(todo, deadline, event, textConstraint, dateConstraint));
        List<Task> loaded = storage.loadTasks();

        assertEquals(5, loaded.size());
        assertTrue(loaded.get(0) instanceof ToDo);
        assertTrue(loaded.get(0).isDone());
        assertEquals(deadline.getDeadline(), ((Deadline) loaded.get(1)).getDeadline());
        assertEquals(event.getFrom(), ((Event) loaded.get(2)).getFrom());
        assertEquals(event.getTo(), ((Event) loaded.get(2)).getTo());
        assertEquals("exam", ((Constraint) loaded.get(3)).getAfter());
        assertEquals(dateConstraint.getAfterDate(), ((Constraint) loaded.get(4)).getAfterDate());
        assertTrue(Files.exists(file));
    }

    @Test
    void loadTasks_ignoresMalformedAndUnknownLines() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "T | 0 | valid task",
                "unknown | 0 | ignored",
                "D | 2 | invalid status | 2027-05-16T11:59",
                "E | 0 | missing end | 2027-05-16T11:59",
                "C | 0 | valid constraint | trigger",
                "not enough fields"));

        List<Task> loaded = new Storage(file.toString()).loadTasks();

        assertEquals(2, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
        assertFalse(loaded.get(0).isDone());
        assertEquals("trigger", ((Constraint) loaded.get(1)).getAfter());
    }

    @Test
    void loadTasks_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }
}
