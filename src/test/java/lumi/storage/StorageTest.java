package lumi.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lumi.task.Deadline;
import lumi.task.Event;
import lumi.task.Task;
import lumi.task.Todo;

/** Tests loading and saving Lumi's pipe-delimited task records. */
public class StorageTest {
    /** Isolates each storage test from the project and from other test cases. */
    @TempDir
    Path temporaryDirectory;

    @Test
    public void load_missingFile_emptyResultReturned() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        Storage.LoadResult result = storage.load();

        assertTrue(result.tasks().isEmpty());
        assertEquals(0, result.skippedLineCount());
    }

    @Test
    public void load_validRecords_taskDetailsAndStatusesRestored() throws Exception {
        Path dataFile = temporaryDirectory.resolve("lumi.txt");
        Files.write(dataFile, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-06-06T00:00:00",
                "E | 1 | project meeting | 2019-08-06T14:00:00 | 2019-08-06T16:00:00"
        ), StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(dataFile).load();

        Todo todo = assertInstanceOf(Todo.class, result.tasks().get(0));
        Deadline deadline = assertInstanceOf(Deadline.class, result.tasks().get(1));
        Event event = assertInstanceOf(Event.class, result.tasks().get(2));
        assertEquals(0, result.skippedLineCount());
        assertEquals("read book", todo.getDescription());
        assertTrue(todo.isDone());
        assertEquals("return book", deadline.getDescription());
        assertFalse(deadline.isDone());
        assertEquals(LocalDateTime.of(2019, 6, 6, 0, 0), deadline.getBy());
        assertEquals("project meeting", event.getDescription());
        assertTrue(event.isDone());
        assertEquals(LocalDateTime.of(2019, 8, 6, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 8, 6, 16, 0), event.getTo());
    }

    @Test
    public void load_blankAndMalformedLines_validRecordsKeptAndInvalidRecordsCounted()
            throws Exception {
        Path dataFile = temporaryDirectory.resolve("lumi.txt");
        Files.write(dataFile, List.of(
                "",
                "T | 0 | valid task",
                "missing fields",
                "T | 2 | invalid status",
                "X | 0 | unknown type",
                "T | 0 | too | many",
                "D | 0 | missing date",
                "D | 0 | bad date | 2019-02-29T00:00:00",
                "E | 0 | missing end | 2019-01-01T00:00:00",
                "E | 0 | backwards | 2019-01-02T00:00:00 | 2019-01-01T00:00:00",
                "E | 0 | no duration | 2019-01-01T00:00:00 | 2019-01-01T00:00:00",
                "T | 0 |   ",
                "T | 0 | bad\\qescape",
                "T | 0 | trailing\\"
        ), StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(dataFile).load();

        assertEquals(1, result.tasks().size());
        assertEquals("valid task", result.tasks().get(0).getDescription());
        assertEquals(12, result.skippedLineCount());
    }

    @Test
    public void save_nestedFile_allTaskTypesWrittenInCanonicalFormat() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data").resolve("lumi.txt");
        Todo todo = new Todo("pipe | slash \\ newline\ncarriage\rreturn");
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 10, 15, 18, 0));
        deadline.markAsDone();
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 10, 16, 14, 0),
                LocalDateTime.of(2019, 10, 16, 16, 0));

        new Storage(dataFile).save(List.of(todo, deadline, event));

        assertEquals(List.of(
                "T | 0 | pipe \\| slash \\\\ newline\\ncarriage\\rreturn",
                "D | 1 | return book | 2019-10-15T18:00:00",
                "E | 0 | project meeting | 2019-10-16T14:00:00 | 2019-10-16T16:00:00"
        ), Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void saveThenLoad_escapedTaskText_originalTextRestored() throws Exception {
        Path dataFile = temporaryDirectory.resolve("lumi.txt");
        String description = "pipe | slash \\ newline\ncarriage\rreturn";
        Todo original = new Todo(description);
        original.markAsDone();
        Storage storage = new Storage(dataFile);

        storage.save(List.of(original));
        Storage.LoadResult result = storage.load();

        Todo restored = assertInstanceOf(Todo.class, result.tasks().get(0));
        assertEquals(0, result.skippedLineCount());
        assertEquals(description, restored.getDescription());
        assertTrue(restored.isDone());
    }

    @Test
    public void save_fewerTasksThanExistingFile_oldRecordsRemoved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("lumi.txt");
        Files.write(dataFile, List.of(
                "T | 0 | old first task",
                "T | 0 | old second task"
        ), StandardCharsets.UTF_8);

        new Storage(dataFile).save(List.of(new Todo("replacement")));

        assertEquals(List.of("T | 0 | replacement"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void save_emptyTaskList_existingFileTruncated() throws Exception {
        Path dataFile = temporaryDirectory.resolve("lumi.txt");
        Files.writeString(dataFile, "T | 0 | old task", StandardCharsets.UTF_8);

        new Storage(dataFile).save(List.<Task>of());

        assertEquals("", Files.readString(dataFile, StandardCharsets.UTF_8));
    }
}
