package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests persistence using isolated files, including corrupt records and missing paths.
 */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyWithoutCreatingFile() throws Exception {
        Path file = temporaryDirectory.resolve("missing/tasks.txt");
        assertEquals(List.of(), new Storage(file).load());
        assertFalse(Files.exists(file));
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesOrderStatusAndDates() throws Exception {
        Path file = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(file);
        LocalDateTime start = LocalDateTime.of(2024, 2, 29, 18, 30);
        Todo todo = new Todo("read café notes");
        todo.markAsDone();
        Event event = new Event("trip", start, start.plusDays(1));
        event.markAsDone();
        storage.save(List.of(todo, new Deadline("return book", start), event));

        List<String> expected = List.of("T | 1 | read café notes",
                "D | 0 | return book | 2024-02-29T18:30:00",
                "E | 1 | trip | 2024-02-29T18:30:00 | 2024-03-01T18:30:00");
        assertEquals(expected, Files.readAllLines(file));
        List<Task> loaded = storage.load();
        assertEquals(expected, loaded.stream().map(Task::toDataString).toList());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
    }

    @Test
    public void save_existingFile_replacesPreviousContentsIncludingEmptyList() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.save(List.of(new Todo("first"), new Todo("second")));
        storage.save(List.of(new Todo("replacement")));
        assertEquals(List.of("T | 0 | replacement"), Files.readAllLines(file));
        storage.save(List.of());
        assertEquals("", Files.readString(file));
        assertEquals(List.of(), storage.load());
    }

    @Test
    public void load_blankLines_skipsThem() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("", "   ", "T | 0 | book", "\t"));
        List<Task> tasks = new Storage(file).load();
        assertEquals(1, tasks.size());
        assertEquals("T | 0 | book", tasks.get(0).toDataString());
    }

    @Test
    public void load_malformedRecords_reportsPhysicalLineNumberAndReason() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String[][] cases = {
            {"T | 0 |   ", "empty task description"},
            {"E | 0 | trip | 2024-03-01 | 2024-02-29", "event end precedes start"},
            {"Q | 0 | book", "unknown task type"},
            {"T | 2 | book", "invalid completion status"},
            {"T | 0", "wrong number of fields"},
            {"T | 0 | book | extra", "wrong number of fields"},
            {"D | 0 | book | 2023-02-29", "invalid date or time"},
            {"E | 0 | trip | 2024-02-29", "wrong number of fields"},
            {"E | 0 | trip | 2024-02-29 | invalid", "invalid date or time"}
        };
        for (String[] testCase : cases) {
            Files.write(file, List.of("T | 0 | valid", "", testCase[0]));
            YapperException exception = assertThrows(YapperException.class, () -> new Storage(file).load());
            assertEquals("Saved task on line 3 is invalid (" + testCase[1] + ").", exception.getMessage());
        }
    }

    @Test
    public void save_parentIsFile_propagatesIoException() throws IOException {
        Path parent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parent, "occupied");
        Storage storage = new Storage(parent.resolve("tasks.txt"));
        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("book"))));
    }

    @Test
    public void load_directoryInsteadOfFile_propagatesIoException() {
        assertThrows(IOException.class, () -> new Storage(temporaryDirectory).load());
    }
}
