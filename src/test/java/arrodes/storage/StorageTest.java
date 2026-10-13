package arrodes.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import arrodes.exception.ArrodesException;
import arrodes.task.Deadline;
import arrodes.task.Event;
import arrodes.task.TaskList;
import arrodes.task.Todo;


/**
 * Verifies the plain-text snapshots produced by {@link Storage}.
 *
 * <p>This is a dependency-free test executable because the project does not
 * currently define a unit-test framework.</p>
 */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    /** Verifies todo, deadline, event, and completed-task serialization. */
    @Test
    void savesAllTaskTypesAndStatuses() throws IOException {
        Path dataFile = temporaryDirectory.resolve("arrodes.txt");

        TaskList taskList = new TaskList(100);
        taskList.insert(new Todo("read book"));

        Deadline deadline = new Deadline(
                "return book",
                LocalDateTime.of(2026, 6, 6, 0, 0)
        );
        deadline.markAsDone();
        taskList.insert(deadline);

        taskList.insert(new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0)
        ));

        new Storage(dataFile).save(taskList);

        List<String> actual = Files.readAllLines(dataFile);

        List<String> expected = List.of(
                "T | 0 | read book | ",
                "D | 1 | return book | 2026-06-06 | ",
                "E | 0 | project meeting | 2026-08-06T14:00 | 2026-08-06T16:00 | "
        );

        assertEquals(expected, actual);
    }

    /** Verifies that tags are saved for every supported task type. */
    @Test
    void savesTagsForAllTaskTypes() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tagged.txt");
        TaskList taskList = new TaskList(100);

        Todo todo = new Todo("read book");
        todo.tagWith("personal");
        taskList.insert(todo);

        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 6, 6, 0, 0));
        deadline.tagWith("urgent");
        taskList.insert(deadline);

        Event event = new Event("project meeting", LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0));
        event.tagWith("work");
        taskList.insert(event);

        new Storage(dataFile).save(taskList);

        assertEquals(List.of(
                "T | 0 | read book | personal",
                "D | 0 | return book | 2026-06-06 | urgent",
                "E | 0 | project meeting | 2026-08-06T14:00 | 2026-08-06T16:00 | work"
        ), Files.readAllLines(dataFile));
    }

    /** Verifies that tags survive saving and loading. */
    @Test
    void taggedTasksRoundTrip() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tag-round-trip.txt");
        TaskList original = new TaskList(100);
        Todo todo = new Todo("read book");
        todo.tagWith("personal");
        original.insert(todo);

        Storage storage = new Storage(dataFile);
        storage.save(original);

        TaskList loaded = storage.load(100);

        assertEquals("personal", loaded.getTaskByIndex(0).getTag());
        assertEquals("[T][ ] read book #personal", loaded.getTaskByIndex(0).toString());
    }

    /** Verifies that tag delimiters are escaped and restored correctly. */
    @Test
    void escapedTagsRoundTrip() throws IOException {
        Path dataFile = temporaryDirectory.resolve("escaped-tag.txt");
        TaskList original = new TaskList(100);
        Todo todo = new Todo("read book");
        todo.tagWith("work | urgent \\ today");
        original.insert(todo);

        Storage storage = new Storage(dataFile);
        storage.save(original);

        assertEquals("T | 0 | read book | work \\| urgent \\\\ today",
                Files.readString(dataFile).strip());
        assertEquals("work | urgent \\ today", storage.load(100).getTaskByIndex(0).getTag());
    }

    /** Verifies that records without the required tag field are rejected. */
    @Test
    void rejectsRecordsWithoutTags() throws IOException {
        Path dataFile = temporaryDirectory.resolve("legacy.txt");
        Files.writeString(dataFile, "T | 0 | read book");

        assertThrows(ArrodesException.class, () -> new Storage(dataFile).load(100));
    }

    /** Verifies that saving an empty list removes tasks from the old snapshot. */
    @Test
    void saveReplacesPreviousSnapshot() throws IOException {
        Path dataFile = temporaryDirectory.resolve("arrodes.txt");

        TaskList taskList = new TaskList(100);
        taskList.insert(new Todo("temporary task"));

        Storage storage = new Storage(dataFile);
        storage.save(taskList);

        taskList.delete(1);
        storage.save(taskList);

        assertTrue(Files.readAllLines(dataFile).isEmpty());
    }

    /** Verifies that saved records are reconstructed as the correct task types. */
    @Test
    void loadsAllTaskTypesAndStatuses() throws IOException {
        Path dataFile = temporaryDirectory.resolve("arrodes.txt");

        Files.write(dataFile, List.of(
                "T | 0 | read book | ",
                "D | 1 | return book | 2026-06-06 | ",
                "E | 0 | project meeting | 2026-08-06T14:00 | 2026-08-06T16:00 | "
        ));

        TaskList taskList = new Storage(dataFile).load(100);

        assertEquals(3, taskList.getSize());

        assertInstanceOf(Todo.class, taskList.getTaskByIndex(0));
        assertInstanceOf(Deadline.class, taskList.getTaskByIndex(1));
        assertInstanceOf(Event.class, taskList.getTaskByIndex(2));

        assertFalse(taskList.getTaskByIndex(0).isDone());
        assertTrue(taskList.getTaskByIndex(1).isDone());
    }

    /** Verifies that a missing file is treated as an empty first run. */
    @Test
    void missingFileLoadsEmptyList() {
        Path dataFile = temporaryDirectory.resolve("missing.txt");

        TaskList taskList = new Storage(dataFile).load(100);

        assertEquals(0, taskList.getSize());
    }

    /** Verifies that malformed records are not silently accepted. */
    @Test
    void malformedRecordIsRejected() throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid.txt");

        Files.writeString(dataFile, "X | 0 | unknown task");
        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).load(100)
        );

        Files.writeString(dataFile, "T | 2 | invalid status | ");
        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).load(100)
        );

        Files.writeString(dataFile, "T | 0 | unfinished\\");
        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).load(100)
        );

        Files.writeString(dataFile, "D | 0 | missing due date | ");
        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).load(100)
        );

        Files.writeString(dataFile, "D | 0 | impossible date | 2026-02-31 | ");
        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).load(100)
        );

        Files.writeString(
                dataFile,
                "E | 0 | reversed event | 2026-09-05T10:00 | 2026-09-05T09:00 | "
        );
        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).load(100)
        );
    }

    /** Verifies that descriptions and time fields may contain storage delimiters. */
    @Test
    void escapedFieldsRoundTrip() throws IOException {
        Path dataFile = temporaryDirectory.resolve("escaped.txt");

        TaskList original = new TaskList(100);
        original.insert(new Todo("read | write \\ revise"));
        original.insert(new Deadline(
                "submit | report",
                LocalDateTime.of(2026, 8, 7, 0, 0)
        ));

        Storage storage = new Storage(dataFile);
        storage.save(original);

        TaskList loaded = storage.load(100);

        assertEquals(
                "read | write \\ revise",
                loaded.getTaskByIndex(0).getDescription()
        );

        assertEquals(
                LocalDateTime.of(2026, 8, 7, 0, 0), ((Deadline) loaded.getTaskByIndex(1)).getDueBy()
        );
    }

    /** Verifies that date and time fields are exposed as java.time values. */
    @Test
    void dateAndTimeValuesAreTyped() throws IOException {
        Path dataFile = temporaryDirectory.resolve("typed.txt");

        TaskList original = new TaskList(100);

        original.insert(new Deadline(
                "pay bill",
                LocalDateTime.of(2026, 9, 1, 0, 0)
        ));

        original.insert(new Event(
                "appointment",
                LocalDateTime.of(2026, 9, 2, 9, 30),
                LocalDateTime.of(2026, 9, 2, 10, 0)
        ));

        Storage storage = new Storage(dataFile);
        storage.save(original);

        TaskList loaded = storage.load(10);

        assertEquals(
                LocalDateTime.of(2026, 9, 1, 0, 0), ((Deadline) loaded.getTaskByIndex(0)).getDueBy()
        );

        assertEquals(
                LocalDateTime.of(2026, 9, 2, 9, 30), ((Event) loaded.getTaskByIndex(1)).getStartAt()
        );

        Files.write(dataFile, List.of(
                "D | 0 | timed deadline | 2026-09-03T17:30 | ",
                "E | 0 | date-only event | 2026-09-04 | 2026-09-05 | "
        ));

        TaskList mixedFormats = storage.load(10);

        assertEquals(
                LocalDateTime.of(2026, 9, 3, 17, 30), ((Deadline) mixedFormats.getTaskByIndex(0)).getDueBy()
        );

        assertEquals(
                LocalDateTime.of(2026, 9, 4, 0, 0), ((Event) mixedFormats.getTaskByIndex(1)).getStartAt()
        );
    }

    /** Verifies that a file with more records than the configured limit is rejected. */
    @Test
    void capacityOverflowIsRejected() throws IOException {
        Path dataFile = temporaryDirectory.resolve("full.txt");

        Files.write(dataFile, List.of(
                "T | 0 | first | ",
                "T | 0 | second | "
        ));

        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).load(1)
        );
    }

    /** Verifies that a failed save cannot replace a directory with a file. */
    @Test
    void saveFailureDoesNotReplaceDirectory() throws IOException {
        Path dataFile = temporaryDirectory.resolve("directory-target");

        Files.createDirectory(dataFile);

        assertThrows(
                ArrodesException.class, () -> new Storage(dataFile).save(new TaskList(100))
        );

        assertTrue(Files.isDirectory(dataFile));
    }
}
