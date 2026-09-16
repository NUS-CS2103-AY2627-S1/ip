package turtley.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import turtley.exception.TurtleyException;
import turtley.model.Deadline;
import turtley.model.Event;
import turtley.model.Task;
import turtley.model.ToDo;

/** Tests persistence, validation, and escaping behavior of {@link Storage}. */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void saveThenLoad_allTaskTypesAndEscapedFields_roundTrip() {
        Path dataFile = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        ToDo todo = new ToDo("buy | milk\\bread\nsoon", List.of("#home", "#Fun"));
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 27), List.of("#work"));
        Event event = new Event("team meeting", LocalDateTime.of(2026, 8, 27, 14, 5),
                LocalDateTime.of(2026, 8, 27, 15, 5), List.of("#meeting"));
        event.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        ToDo loadedTodo = assertInstanceOf(ToDo.class, loaded.get(0));
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loaded.get(1));
        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(2));
        assertAll(
                () -> assertTrue(Files.exists(dataFile)),
                () -> assertEquals(3, loaded.size()),
                () -> assertEquals(todo.getDescription(), loadedTodo.getDescription()),
                () -> assertEquals(todo.getTags(), loadedTodo.getTags()),
                () -> assertFalse(loadedTodo.isDone()),
                () -> assertEquals(deadline.getTags(), loadedDeadline.getTags()),
                () -> assertEquals(deadline.getBy(), loadedDeadline.getBy()),
                () -> assertFalse(loadedDeadline.isDone()),
                () -> assertEquals(event.getTags(), loadedEvent.getTags()),
                () -> assertEquals(event.getFrom(), loadedEvent.getFrom()),
                () -> assertEquals(event.getTo(), loadedEvent.getTo()),
                () -> assertTrue(loadedEvent.isDone()));
    }

    @Test
    void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void save_emptyList_deletesExistingFile() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        storage.save(List.of(new ToDo("temporary")));

        storage.save(List.of());

        assertFalse(Files.exists(dataFile));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void load_blankLines_ignoresThem() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT | 0 | keep this\n  \n");

        List<Task> loaded = new Storage(dataFile.toString()).load();

        assertEquals(1, loaded.size());
        assertEquals("keep this", loaded.get(0).getDescription());
    }

    @Test
    void load_legacyRecords_returnsTasksWithoutTags() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | keep this\n"
                + "D | 1 | submit report | 2026-08-27\n"
                + "E | 0 | meeting | 2026-08-27 14:00 | 2026-08-27 15:00\n");

        List<Task> loaded = new Storage(dataFile.toString()).load();

        assertEquals(List.of(), loaded.get(0).getTags());
        assertEquals(List.of(), loaded.get(1).getTags());
        assertEquals(List.of(), loaded.get(2).getTags());
    }

    @Test
    void load_invalidTag_reportsLineContext() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | invalid tag | fun\n");

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Storage(dataFile.toString()).load());

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("tags contain an invalid tag"));
    }

    @Test
    void load_malformedRecord_reportsLineNumber() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 2 | invalid status\n");

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Storage(dataFile.toString()).load());

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("T | 2 | invalid status"));
    }

    @Test
    void load_invalidDate_reportsFieldAndLineContext() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "D | 0 | report | 2026-02-30\n");

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Storage(dataFile.toString()).load());

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("deadline has an invalid date/time format"));
        assertInstanceOf(TurtleyException.class, exception.getCause());
    }

    @Test
    void load_invalidEventRange_reportsLineContext() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "E | 0 | meeting | 2026-08-27 10:00 | 2026-08-27 10:00\n");

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Storage(dataFile.toString()).load());

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("event start time must be before end time"));
    }

    @Test
    void load_duplicateTaskRecords_throwsTurtleyException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | repeated\nT | 1 | repeated\n");

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Storage(dataFile.toString()).load());

        assertEquals("Unable to load tasks from disk: duplicate task data.", exception.getMessage());
    }

    @Test
    void load_malformedEscape_throwsTurtleyException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | malformed\\q\n");

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Storage(dataFile.toString()).load());

        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("malformed escape sequence"));
    }

    @Test
    void load_directoryPath_throwsTurtleyException() {
        Path directory = temporaryDirectory.resolve("directory");
        assertTrue(directory.toFile().mkdir());

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Storage(directory.toString()).load());

        assertEquals("Unable to load tasks from disk: save path is not a file.",
                exception.getMessage());
    }

    @Test
    void save_nullOrOverCapacityList_throwsTurtleyException() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        List<Task> tooManyTasks = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            tooManyTasks.add(new ToDo("task " + i));
        }

        TurtleyException nullException = assertThrows(TurtleyException.class,
                () -> storage.save(null));
        TurtleyException capacityException = assertThrows(TurtleyException.class,
                () -> storage.save(tooManyTasks));

        assertEquals("Unable to save tasks: task list is null.", nullException.getMessage());
        assertEquals("Unable to save tasks: task list exceeds 100 tasks.",
                capacityException.getMessage());
    }

    @Test
    void constructor_blankPath_throwsTurtleyException() {
        assertThrows(TurtleyException.class, () -> new Storage(null));
        assertThrows(TurtleyException.class, () -> new Storage("  "));
        assertThrows(TurtleyException.class, () -> new Storage("bad\u0000path"));
    }
}
