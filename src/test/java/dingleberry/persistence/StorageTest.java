package dingleberry.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.model.Deadlines;
import dingleberry.model.Events;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.model.Todo;

/** Tests resilient UTF-8 persistence and recovery from corrupted records. */
class StorageTest {
    /** Defines the first fixture date. */
    private static final LocalDateTime FIRST_DATE =
            LocalDateTime.of(2026, 9, 16, 14, 0);
    /** Defines the second fixture date. */
    private static final LocalDateTime SECOND_DATE =
            LocalDateTime.of(2026, 9, 16, 15, 0);
    /** Defines the number of task types in the complete round-trip fixture. */
    private static final int ALL_TASK_TYPE_COUNT = 3;
    /** Defines the number of malformed records in the warning fixture. */
    private static final int MALFORMED_RECORD_COUNT = 3;

    /** Provides an isolated filesystem location for each test. */
    @TempDir
    private Path tempDir;

    @Test
    void saveAndLoadEscapedUnicodeDescriptionPreservesContent()
            throws Exception {
        Path file = tempDir.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        String description = "Read | C:\\temp\\notes - café 🎉";
        TaskList tasks = new TaskList(new Todo(description));

        storage.save(tasks);

        TaskList loadedTasks = new TaskList(storage.load());
        assertEquals(1, loadedTasks.size());
        assertEquals(description, loadedTasks.get(0).getDescription());
        assertTrue(Files.exists(file));
    }

    @Test
    void loadValidRecordsSkipsAndReportsCorruptedRecords() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "T | 0 | valid task",
                "not a task record",
                "T | 2 | invalid completion flag",
                "D | 0 | invalid date | 2026-02-30T18:00:00",
                "E | 0 | invalid range | 2026-09-16T15:00:00"
                        + " | 2026-09-16T14:00:00",
                "E | 1 | valid event | 2026-09-16T14:00:00"
                        + " | 2026-09-16T15:00:00"),
                StandardCharsets.UTF_8);

        ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
        PrintStream originalError = System.err;
        try {
            System.setErr(new PrintStream(errorOutput, true,
                    StandardCharsets.UTF_8));
            TaskList loadedTasks = new TaskList(storageFor(file).load());

            assertEquals(2, loadedTasks.size());
            assertEquals("valid task", loadedTasks.get(0).getDescription());
            assertEquals("valid event", loadedTasks.get(1).getDescription());
        } finally {
            System.setErr(originalError);
        }
        assertTrue(errorOutput.toString(StandardCharsets.UTF_8)
                .contains("Skipping corrupted task record"));
    }

    @Test
    void loadWithUnknownEscapeReportsCorruptedRecord() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | bad\\q", StandardCharsets.UTF_8);

        Storage.LoadResult result = storageFor(file).loadWithReport();

        assertEquals(0, result.getTasks().size());
        assertEquals(1, result.getWarnings().size());
        assertTrue(result.getWarnings().get(0).contains("unknown escape"));
    }

    @Test
    void saveFailureDoesNotReplaceExistingFile() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = storageFor(file);
        storage.save(new TaskList(new Todo("original")));
        Task failingTask = new Task("broken") {
            @Override
            public String toSaveFormat() {
                throw new IllegalStateException("serialization failed");
            }
        };

        assertThrows(IllegalStateException.class,
                () -> storage.save(new TaskList(failingTask)));
        assertEquals("T | 0 | original" + System.lineSeparator(),
                Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void saveAndLoadEventPreservesDatesAndCompletion() throws Exception {
        Path file = tempDir.resolve("events.txt");
        Storage storage = storageFor(file);
        Events event = new Events("meeting", FIRST_DATE, SECOND_DATE);
        event.markAsDone();

        storage.save(new TaskList(event));

        TaskList loadedTasks = new TaskList(storage.load());
        assertEquals(1, loadedTasks.size());
        assertEquals(event.toSaveFormat(), loadedTasks.get(0).toSaveFormat());
    }

        @Test
        void saveAndLoadAllTaskTypesPreservesRecords() throws Exception {
        Path file = tempDir.resolve("all-task-types.txt");
        Storage storage = storageFor(file);
        Task todo = new Todo("read notes");
        Task deadline = new Deadlines("submit report", FIRST_DATE);
        Task event = new Events("team meeting", FIRST_DATE, SECOND_DATE);
        event.markAsDone();
        TaskList tasks = new TaskList(todo, deadline, event);

        storage.save(tasks);

        TaskList loadedTasks = new TaskList(storage.load());
        assertEquals(ALL_TASK_TYPE_COUNT, loadedTasks.size());
        assertEquals(todo.toSaveFormat(),
            loadedTasks.get(0).toSaveFormat());
        assertEquals(deadline.toSaveFormat(),
            loadedTasks.get(1).toSaveFormat());
        assertEquals(event.toSaveFormat(),
            loadedTasks.get(2).toSaveFormat());
        }

        @Test
        void saveAndLoadEscapedLineBreaksPreservesDescription()
            throws Exception {
        Path file = tempDir.resolve("line-breaks.txt");
        Storage storage = storageFor(file);
        String description = "first line\nsecond line\rthird line";

        storage.save(new TaskList(new Todo(description)));

        TaskList loadedTasks = new TaskList(storage.load());
        assertEquals(description, loadedTasks.get(0).getDescription());
        }

        @Test
        void loadWithReportReturnsExactWarningsForMalformedRecords()
            throws Exception {
        Path file = tempDir.resolve("malformed-records.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
            "T | 0 | valid task",
            "T | 2 | invalid flag",
            "T | 0 | extra field | unexpected",
            "D | 0 | missing deadline"), StandardCharsets.UTF_8);

        Storage.LoadResult result = storageFor(file).loadWithReport();

        assertEquals(1, result.getTasks().size());
        assertEquals(MALFORMED_RECORD_COUNT, result.getWarnings().size());
        assertEquals(
            "Skipping corrupted task record: T | 2 | invalid flag"
                + " (invalid completion flag)",
            result.getWarnings().get(0));
        assertEquals(
            "Skipping corrupted task record: T | 0 | extra field"
                + " | unexpected (expected 3 fields but found 4)",
            result.getWarnings().get(1));
        assertEquals(
            "Skipping corrupted task record: D | 0 | missing deadline"
                + " (expected 4 fields but found 3)",
            result.getWarnings().get(2));
        }

    @Test
    void loadWithFileParentReportsDirectoryFailure() throws Exception {
        Path parentFile = tempDir.resolve("parent-file");
        Files.writeString(parentFile, "not a directory",
            StandardCharsets.UTF_8);
        Storage storage = new Storage(parentFile.resolve("tasks.txt")
                .toString());

        IOException exception = assertThrows(IOException.class, storage::load);

        assertTrue(exception.getMessage().contains("not a directory"));
    }

    @Test
    void invalidStoragePathReportsClearError() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Storage("\u0000"));

        assertTrue(exception.getMessage().contains("Invalid storage path"));
    }

    private Storage storageFor(final Path file) {
        return new Storage(file.toString());
    }
}
