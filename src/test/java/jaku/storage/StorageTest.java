package jaku.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jaku.JakuException;
import jaku.task.Deadline;
import jaku.task.Event;
import jaku.task.Recurrence;
import jaku.task.RecurringEvent;
import jaku.task.RecurringTodo;
import jaku.task.Task;
import jaku.task.Todo;

/** Verifies task persistence, malformed data handling, and write failures. */
class StorageTest {
    /** Verifies every supported task type survives a save-and-load round trip. */
    @Test
    void saveAndLoad_roundTripsEveryTaskType(@TempDir Path temporaryDirectory) throws JakuException {
        Storage storage = new Storage(temporaryDirectory.resolve("jaku.txt"));
        Todo todo = new Todo("read\tbook");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 20));
        deadline.markAsDone();
        Event event = new Event("discuss\nbook", "14:00", "15:00");
        RecurringTodo recurringTodo = new RecurringTodo("review", LocalDate.of(2026, 9, 21), Recurrence.DAILY);
        RecurringEvent recurringEvent = new RecurringEvent("meeting", LocalDateTime.of(2026, 9, 22, 14, 0),
                LocalDateTime.of(2026, 9, 22, 15, 0), Recurrence.WEEKLY);

        storage.save(List.of(todo, deadline, event, recurringTodo, recurringEvent));
        List<Task> restored = storage.load();

        assertEquals(List.of("[T][ ] read\tbook", "[D][X] return book (by: Sep 20 2026)",
                "[E][ ] discuss\nbook (from: 14:00 to: 15:00)",
                "[R][ ] review (every: daily, next: Sep 21 2026)",
                "[R][ ] meeting (every: weekly, from: Sep 22 2026 14:00 to: Sep 22 2026 15:00)"),
                restored.stream().map(Task::toString).toList());
    }

    /** Verifies malformed task records are rejected rather than silently discarded. */
    @Test
    void load_rejectsMalformedRecord(@TempDir Path temporaryDirectory) throws IOException {
        Path dataFile = temporaryDirectory.resolve("jaku.txt");
        Files.writeString(dataFile, "T\t0\tvalid\nINVALID");

        assertThrows(JakuException.class, () -> new Storage(dataFile).load());
    }

    /** Verifies a regular file cannot be used as the data file's parent directory. */
    @Test
    void save_rejectsUnusableParentPath(@TempDir Path temporaryDirectory) throws IOException {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "not a directory");

        assertThrows(JakuException.class, () -> new Storage(blockedParent.resolve("jaku.txt")).save(List.of()));
    }
}
