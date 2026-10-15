package edith.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edith.task.Deadline;
import edith.task.Event;
import edith.task.Task;
import edith.task.Todo;

/** Tests saving and restoring tasks without modifying Edith's real data file. */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws IOException {
        assertTrue(Storage.loadTasks(tempDirectory.resolve("missing.txt")).isEmpty());
    }

    @Test
    public void saveAndLoadTasks_mixedTasks_preservesOrderDetailsAndStatus() throws IOException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit work", LocalDateTime.of(2026, 9, 6, 0, 0), false);
        Event event = new Event("meeting", LocalDateTime.of(2026, 9, 6, 14, 0), true,
                LocalDateTime.of(2026, 9, 7, 0, 0), false);
        Path dataFile = tempDirectory.resolve("nested/data.txt");

        Storage.saveTasks(List.of(todo, deadline, event), dataFile);
        List<Task> restoredTasks = Storage.loadTasks(dataFile);

        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()),
                restoredTasks.stream().map(Task::toString).toList());
    }

    @Test
    public void saveTasks_multipleTasks_writesHeadingAndOneBasedNumbers() throws IOException {
        Path dataFile = tempDirectory.resolve("data.txt");

        Storage.saveTasks(List.of(new Todo("first"), new Todo("second")), dataFile);

        assertEquals(List.of("Here are the tasks in your list:", "1.[T][ ] first", "2.[T][ ] second"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void saveAndLoadTasks_taggedTasks_preservesMetadataAndLegacyTaskLines() throws IOException {
        Todo todo = new Todo("read book");
        todo.addTag("#Fun");
        todo.addTag("#school");
        Deadline deadline = new Deadline("submit work", LocalDateTime.of(2026, 9, 20, 0, 0), false);
        deadline.addTag("#school");
        Path dataFile = tempDirectory.resolve("data.txt");

        Storage.saveTasks(List.of(todo, deadline, new Todo("buy milk")), dataFile);
        List<Task> restoredTasks = Storage.loadTasks(dataFile);

        assertEquals(List.of("Here are the tasks in your list:",
                "1.[T][ ] read book", "@tags #Fun #school",
                "2.[D][ ] submit work (by: Sep 20 2026)", "@tags #school",
                "3.[T][ ] buy milk"), Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertEquals(List.of(todo.toString(), deadline.toString(), "[T][ ] buy milk"),
                restoredTasks.stream().map(Task::toString).toList());
    }

    @Test
    public void loadTasks_legacyDescriptionWithTagLikeText_keepsDescriptionUntagged() throws IOException {
        Path dataFile = writeData("Here are the tasks in your list:\n1.[T][ ] read [tags: #fun]\n");

        Task task = Storage.loadTasks(dataFile).get(0);

        assertEquals("[T][ ] read [tags: #fun]", task.toString());
        assertTrue(task.getTags().isEmpty());
    }

    @Test
    public void loadTasks_orphanDuplicateOrMalformedTagMetadata_exceptionThrown() throws IOException {
        assertInvalidSavedTask("@tags #fun");
        assertInvalidSavedTask("1.[T][ ] read book\n@tags");
        assertInvalidSavedTask("1.[T][ ] read book\n@tags #fun #Fun");
        assertInvalidSavedTask("1.[T][ ] read book\n@tags #fun\n@tags #school");
        assertInvalidSavedTask("1.[T][ ] read book\n\n@tags #fun");
    }

    @Test
    public void loadTasks_headingAndBlankLines_ignoresNonTaskLines() throws IOException {
        Path dataFile = writeData("Here are the tasks in your list:\n\n1.[T][ ] read book\n");

        List<Task> tasks = Storage.loadTasks(dataFile);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void loadTasks_malformedTaskStructure_exceptionThrown() throws IOException {
        assertInvalidSavedTask("not numbered");
        assertInvalidSavedTask("1.T][ ] missing bracket");
        assertInvalidSavedTask("1.[Z][ ] unknown type");
        assertInvalidSavedTask("1.[T][?] invalid status");
    }

    @Test
    public void loadTasks_malformedDeadline_exceptionThrown() throws IOException {
        assertInvalidSavedTask("1.[D][ ] submit work");
        assertInvalidSavedTask("1.[D][ ] submit work (by: tomorrow)");
    }

    @Test
    public void loadTasks_malformedEvent_exceptionThrown() throws IOException {
        assertInvalidSavedTask("1.[E][ ] meeting (from: Sep 06 2026)");
        assertInvalidSavedTask("1.[E][ ] meeting (from: tomorrow to: Sep 07 2026)");
        assertInvalidSavedTask("1.[E][ ] meeting (from: Sep 07 2026 to: Sep 06 2026)");
        assertInvalidSavedTask("1.[E][ ] meeting (from: Sep 06 2026 to: Sep 06 2026)");
    }

    @Test
    public void loadTasks_duplicateOrEmptyTasks_exceptionThrown() throws IOException {
        assertInvalidSavedTask("1.[T][ ] read book\n2.[T][X] read book");
        assertInvalidSavedTask("1.[T][ ] ");
        assertInvalidSavedTask("1.[D][ ]  (by: Sep 06 2026)");
        assertInvalidSavedTask("2.[T][ ] first");
        assertInvalidSavedTask("1.[T][ ] first\n3.[T][ ] second");
    }

    /** Writes one complete temporary data file. */
    private Path writeData(String contents) throws IOException {
        Path dataFile = tempDirectory.resolve("data.txt");
        Files.writeString(dataFile, contents, StandardCharsets.UTF_8);
        return dataFile;
    }

    /** Verifies that one malformed saved line is rejected. */
    private void assertInvalidSavedTask(String savedLine) throws IOException {
        Path dataFile = writeData(savedLine);
        assertThrows(IOException.class, () -> Storage.loadTasks(dataFile));
    }
}
