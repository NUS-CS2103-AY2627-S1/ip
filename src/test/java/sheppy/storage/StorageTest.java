package sheppy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import sheppy.SheppyException;
import sheppy.task.Deadline;
import sheppy.task.Event;
import sheppy.task.Task;
import sheppy.task.TaskList;
import sheppy.task.Todo;

/** Verifies storage compatibility, round trips and failed writes. */
class StorageTest {
    @TempDir
    private Path directory;

    /** Preserves all task types, completion status, Unicode and ordering. */
    @Test
    void roundTrip_preservesTasks() throws SheppyException, IOException {
        Path file = directory.resolve("nested/tasks.txt");
        Storage storage = new Storage(file.toString());
        Task done = new Todo("读书 🐑");
        done.markAsDone();
        TaskList tasks = new TaskList(done, new Deadline("report", LocalDate.of(2028, 2, 29)),
                new Event("meeting", "Monday 2pm", "4pm"));
        storage.save(tasks);
        assertEquals(tasks.asList().stream().map(Task::toStorageString).toList(),
                storage.load().asList().stream().map(Task::toStorageString).toList());
        storage.save(new TaskList());
        assertEquals("", Files.readString(file));
        assertEquals(0, storage.load().size());
    }

    /** Loads a missing file as an empty list without creating it. */
    @Test
    void missingFile_returnsEmptyList() throws SheppyException {
        Path file = directory.resolve("absent.txt");
        assertEquals(0, new Storage(file.toString()).load().size());
        assertTrue(Files.notExists(file));
    }

    /** Rejects malformed records without modifying the original file. */
    @ParameterizedTest
    @ValueSource(strings = {"bad", "X | 0 | task", "T | 2 | task", "T | 0 | ",
        "T | 0 | task | extra", "D | 0 | task", "D | 0 | task | 2026-02-30",
        "E | 0 | task | | end", "E | 0 | task | start |"})
    void malformedRecord_preservesFile(String record) throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, record);
        assertThrows(SheppyException.class, () -> new Storage(file.toString()).load());
        assertEquals(record, Files.readString(file));
    }

    /** Skips blank lines in legacy files. */
    @Test
    void legacyFile_ignoresBlankLines() throws IOException, SheppyException {
        Path file = directory.resolve("tasks.txt");
        Files.write(file, List.of("", "T | 1 | read", "  ", "T | 0 | write"));
        TaskList tasks = new Storage(file.toString()).load();
        assertEquals(2, tasks.size());
        assertEquals("[T][X] read", tasks.get(1).toString());
    }

    /** A nonempty directory cannot be replaced by a task file. */
    @Test
    void failedReplacement_preservesDestinationAndCleansTemporaryFile() throws IOException, SheppyException {
        Path destination = directory.resolve("tasks.txt");
        Files.createDirectory(destination);
        Files.writeString(destination.resolve("keep.txt"), "keep");
        Storage storage = new Storage(destination.toString());
        assertThrows(SheppyException.class, () -> storage.save(new TaskList(new Todo("new"))));
        assertEquals("keep", Files.readString(destination.resolve("keep.txt")));
        try (var files = Files.list(directory)) {
            assertEquals(List.of(destination), files.toList());
        }
        assertThrows(SheppyException.class, storage::load);
    }
}
