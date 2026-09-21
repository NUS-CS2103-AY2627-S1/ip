package twizzy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests loading and saving task data. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveThenLoad_mixedTasks_preservesTypeDetailsAndCompletionState() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("tasks.txt"));
        Todo todo = new Todo("read A | B");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        deadline.markAsDone();
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 21),
                LocalDate.of(2026, 9, 22));

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(List.of("◷ read A | B", "★ submit report" + System.lineSeparator()
                        + "  ↳ Due: 30 Sep 2026", "◷ project meeting" + System.lineSeparator()
                        + "  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026"),
                loadedTasks.stream().map(Task::toString).toList());
    }

    @Test
    void load_missingFile_returnsEmptyTaskList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("new-data").resolve("tasks.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveThenLoad_snoozedTask_preservesSnoozeEndDate() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        Todo task = new Todo("postponed task");
        task.snoozeUntil(LocalDate.of(2099, 12, 31));

        storage.save(List.of(task));
        List<Task> loadedTasks = storage.load();

        assertEquals(LocalDate.of(2099, 12, 31), loadedTasks.getFirst().getSnoozedUntil());
    }

    @Test
    void load_invalidData_throwsIoExceptionWithLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | invalid-status | task");
        Storage storage = new Storage(dataFile);

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task data on line 1.", exception.getMessage());
    }
}
