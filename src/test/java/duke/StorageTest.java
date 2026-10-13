package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests durable task round-trips, first-run behavior, and corrupted data handling. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "T | 0;not enough fields",
        "T | 2 | task;status must be 0 or 1",
        "T | 0 | ;task description cannot be empty",
        "T | 0 | task | extra;unexpected number of fields",
        "D | 0 | task | ;deadline value cannot be empty",
        "E | 0 | task | start | ;event start and end values cannot be empty",
        "T | 0 | \\n;task description cannot be empty",
        "E | 0 | task | \\r | end;event start and end values cannot be empty",
        "X | 0 | task;unknown task type"
    })
    void loadTasks_invalidFields_reportsReasonAndLine(String line, String reason) throws Exception {
        Path file = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(file, "T | 0 | valid\n" + line + "\n");

        StorageException exception = assertThrows(StorageException.class, new Storage(file)::loadTasks);

        assertTrue(exception.getMessage().contains("line 2: " + reason));
        assertTrue(exception.getMessage().contains(file.toString()));
        assertEquals("T | 0 | valid\n" + line + "\n", Files.readString(file));
    }

    @Test
    void saveAndLoadTasks_roundTripsTypesStatusesAndEscapedText() throws Exception {
        Path file = temporaryDirectory.resolve("data/tasks.txt");
        Storage storage = new Storage(file);
        Task todo = new Todo("read | book\\notes");
        todo.markAsDone();
        Task deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Task event = new Event("meeting", "2pm", "4pm");

        storage.saveTasks(List.of(todo, deadline, event));

        assertTrue(Files.exists(file));
        assertTrue(Files.readString(file).contains("D | 0 | return book | 2019-12-02"));
        List<Task> loaded = storage.loadTasks();
        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0).isDone());
        assertEquals("read | book\\notes", loaded.get(0).getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), ((Deadline) loaded.get(1)).getBy());
        assertEquals("4pm", ((Event) loaded.get(2)).getTo());
    }

    @Test
    void loadTasks_whenFileIsMissing_returnsEmptyList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/tasks.txt"));

        assertTrue(storage.loadTasks().isEmpty());
        assertFalse(Files.exists(temporaryDirectory.resolve("missing")));
    }

    @Test
    void loadTasks_whenDateIsCorrupted_reportsTheLineNumber() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | return book | not-a-date\n");

        StorageException exception = assertThrows(StorageException.class, new Storage(file)::loadTasks);
        assertTrue(exception.getMessage().contains("line 1"));
        assertTrue(exception.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    void saveAndLoadTasks_preservesUnicodeAndEscapedEventFields() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("My tasks 王/wangsa.txt"));
        Event event = new Event("réviser 王\nchapter 2", "Monday | 2pm", "Tuesday\\evening\r4pm");

        storage.saveTasks(List.of(event));
        Event reloaded = (Event) storage.loadTasks().getFirst();

        assertEquals(event.getDescription(), reloaded.getDescription());
        assertEquals(event.getFrom(), reloaded.getFrom());
        assertEquals(event.getTo(), reloaded.getTo());
    }

    @Test
    void saveAndLoadTasks_escapedLineBreaksAtFieldEdges_preservesText() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        Event event = new Event("\nread notes\r", "\rstart\n", "\nend\r");

        storage.saveTasks(List.of(event));
        Event reloaded = (Event) storage.loadTasks().getFirst();

        assertEquals(event.getDescription(), reloaded.getDescription());
        assertEquals(event.getFrom(), reloaded.getFrom());
        assertEquals(event.getTo(), reloaded.getTo());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 9999})
    void saveAndLoadTasks_fourDigitYearBoundaries_preservesDate(int year) throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        Deadline deadline = new Deadline("boundary date", LocalDate.of(year, 1, 1));

        storage.saveTasks(List.of(deadline));

        assertEquals(deadline.getBy(), ((Deadline) storage.loadTasks().getFirst()).getBy());
    }

    @Test
    void saveTasks_repeatedAndEmptySaves_replaceFileAndLeaveNoTemporaryFiles() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("first")));
        storage.saveTasks(List.of(new Todo("second")));
        assertEquals("second", storage.loadTasks().getFirst().getDescription());

        storage.saveTasks(List.of());

        assertTrue(storage.loadTasks().isEmpty());
        assertEquals("", Files.readString(file));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    void saveTasks_invalidSnapshot_doesNotReplaceExistingData() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.saveTasks(List.of(new Todo("keep this")));
        String original = Files.readString(file);

        assertThrows(StorageException.class, () -> storage.saveTasks(List.of(new Todo(" "))));
        assertThrows(StorageException.class, () -> storage.saveTasks(null));

        assertEquals(original, Files.readString(file));
    }

    @Test
    void saveTasks_destinationIsDirectory_preservesContentsAndCleansTemporaryFile() throws Exception {
        Path file = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        Path marker = file.resolve("keep.txt");
        Files.writeString(marker, "keep this data");

        StorageException exception = assertThrows(StorageException.class,
                () -> new Storage(file).saveTasks(List.of(new Todo("cannot save"))));

        assertTrue(exception.getMessage().contains("couldn't save"));
        assertEquals("keep this data", Files.readString(marker));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    void saveTasks_parentIsAFile_reportsRecoveryAdviceWithoutChangingIt() throws Exception {
        Path parent = temporaryDirectory.resolve("data");
        Files.writeString(parent, "do not overwrite");

        StorageException exception = assertThrows(StorageException.class,
                () -> new Storage(parent.resolve("tasks.txt")).saveTasks(List.of(new Todo("task"))));

        assertTrue(exception.getMessage().contains("Check folder access"));
        assertEquals("do not overwrite", Files.readString(parent));
    }

    @Test
    void loadAndSaveTasks_overCapacity_rejectWithoutChangingFile() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String oversized = "T | 0 | task\n".repeat(101);
        Files.writeString(file, oversized);
        Storage storage = new Storage(file);

        assertThrows(StorageException.class, storage::loadTasks);
        List<Task> tasks = IntStream.range(0, 101).mapToObj(index -> (Task) new Todo("task")).toList();
        assertThrows(StorageException.class, () -> storage.saveTasks(tasks));

        assertEquals(oversized, Files.readString(file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-02-30", "26-09-20", "+10000-09-20", "2026-9-2"})
    void loadTasks_invalidDate_rejectsWithoutChangingFile(String date) throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String original = "D | 0 | task | " + date + "\n";
        Files.writeString(file, original);

        assertThrows(StorageException.class, new Storage(file)::loadTasks);

        assertEquals(original, Files.readString(file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"T | 0 | bad\\q", "T | 0 | unfinished\\"})
    void loadTasks_invalidEscape_reportsLineWithoutChangingFile(String original) throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, original);

        StorageException exception = assertThrows(StorageException.class, new Storage(file)::loadTasks);

        assertTrue(exception.getMessage().contains("line 1"));
        assertEquals(original, Files.readString(file));
    }
}
