package todd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests saving and loading task data through {@link Storage}. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyListAndCreatesParentDirectory() throws TodException {
        Path filePath = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(filePath);

        ArrayList<Task> result = storage.load();

        assertTrue(result.isEmpty());
        assertTrue(Files.isDirectory(filePath.getParent()));
    }

    @Test
    public void saveThenLoad_mixedTasks_preservesTaskData() throws TodException {
        Path filePath = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 4, 23, 59));
        Event event = new Event("conference",
                LocalDateTime.of(2026, 9, 5, 9, 0),
                LocalDateTime.of(2026, 9, 6, 17, 0));

        storage.save(List.of(todo, deadline, event));
        ArrayList<Task> result = storage.load();

        assertEquals(3, result.size());
        assertInstanceOf(Todo.class, result.get(0));
        assertEquals("read book", result.get(0).getDescription());
        assertTrue(result.get(0).isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, result.get(1));
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 4, 23, 59), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, result.get(2));
        assertEquals("conference", loadedEvent.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 5, 9, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 6, 17, 0), loadedEvent.getTo());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    public void load_blankLines_ignoresBlankLines() throws IOException, TodException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.write(filePath, List.of("", "T | 0 | read book", "   "), StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ArrayList<Task> result = storage.load();

        assertEquals(1, result.size());
        assertEquals("read book", result.get(0).getDescription());
    }

    @Test
    public void load_invalidTaskType_throwsTodExceptionWithLineNumber() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.write(filePath, List.of(
                "T | 0 | valid task",
                "X | 0 | invalid task"), StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        TodException exception = assertThrows(TodException.class, storage::load);

        assertEquals("The saved task on line 2 is invalid.", exception.getMessage());
    }

    @Test
    public void load_eventEndingBeforeStart_throwsTodException() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath,
                "E | 0 | conference | 2026-09-06T17:00 | 2026-09-05T09:00",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        assertThrows(TodException.class, storage::load);
    }
    @Test
    public void load_invalidFields_throwsErrorWithLineNumber() throws IOException {
        Path dataPath = temporaryDirectory.resolve("tasks.txt");
        String[] invalidLines = {
            "T | 2 | read", "T | 0 | ", "T | 0 | read | extra",
            "D | 0 | report | 2026-02-30T12:00",
            "E | 0 | meeting | 2026-09-04T12:00 | 2026-09-04T12:00"
        };
        for (String line : invalidLines) {
            Files.writeString(dataPath, line);
            TodException exception = assertThrows(TodException.class, () -> new Storage(dataPath).load());
            assertTrue(exception.getMessage().contains("line 1"));
        }
    }

    @Test
    public void load_parentIsFile_throwsFriendlyError() throws IOException {
        Path parent = temporaryDirectory.resolve("blocked");
        Files.writeString(parent, "keep");
        Storage storage = new Storage(parent.resolve("tasks.txt"));
        assertTrue(assertThrows(TodException.class, storage::load).getMessage().contains("couldn't load"));
        assertEquals("keep", Files.readString(parent));
    }

    @Test
    public void save_repeatedWrites_replacesFileAndCleansTemporaryFiles() throws IOException, TodException {
        Path dataPath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataPath);
        storage.save(List.of(new Todo("first")));
        storage.save(List.of(new Todo("second")));
        assertEquals("second", storage.load().get(0).getDescription());
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(dataPath), files.toList());
        }
    }
}
