package fifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import fifi.task.Deadline;
import fifi.task.Event;
import fifi.task.Task;
import fifi.task.ToDo;

/**
 * Tests loading and saving tasks using temporary files.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_emptyTaskListReturned() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(0, loadedTasks.size());
    }

    @Test
    public void loadTasks_savedTodoDeadlineAndEvent_matchingTaskTypesReturned() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Files.writeString(dataFile, """
                T | 1 | read book
                D | 0 | return book | 2019-12-02
                E | 0 | project meeting | 2019-12-02 | 2019-12-04""");
        Storage storage = new Storage(dataFile.toString());

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertLoadedTaskTypes(loadedTasks);
        assertLoadedTodo((ToDo) loadedTasks.get(0));
        assertLoadedDeadline((Deadline) loadedTasks.get(1));
        assertLoadedEvent((Event) loadedTasks.get(2));
    }

    private void assertLoadedTaskTypes(List<Task> loadedTasks) {
        assertInstanceOf(ToDo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
    }

    private void assertLoadedTodo(ToDo todo) {
        assertTrue(todo.isMarked());
        assertEquals("read book", todo.getDescription());
        assertTrue(todo.getCreatedAt().isEmpty());
        assertTrue(todo.getLastMarkedAt().isEmpty());
        assertEquals("[T][X] read book", todo.toString());
    }

    private void assertLoadedDeadline(Deadline deadline) {
        assertFalse(deadline.isMarked());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getDueDate());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    private void assertLoadedEvent(Event event) {
        assertFalse(event.isMarked());
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), event.getStart());
        assertEquals(LocalDate.of(2019, 12, 4), event.getEnd());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)",
                event.toString());
    }

    @Test
    public void loadTasks_unknownTaskCode_exceptionThrownAndLaterValidDataLoads() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Storage storage = new Storage(dataFile.toString());

        Files.writeString(dataFile, "T | 0 | read book");
        assertInstanceOf(ToDo.class, storage.loadTasks().get(0));

        Files.writeString(dataFile, "X | 0 | unsupported task");
        IOException exception = assertThrows(IOException.class, storage::loadTasks);
        assertEquals("Invalid task data at line 1: Unsupported task type: X", exception.getMessage());

        Files.writeString(dataFile, "D | 0 | return book | 2019-12-02");
        assertInstanceOf(Deadline.class, storage.loadTasks().get(0));
    }

    @Test
    public void saveTasks_newParentDirectory_dataFileCreated() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data").resolve("fifi.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList(getSampleTasks());

        storage.saveTasks(tasks);

        assertEquals(String.join(System.lineSeparator(),
                "T | 1 | read book | 2019-11-29T08:00:00 | 2019-12-01T17:00:00",
                "D | 0 | return book | 2019-12-02 | 2019-11-30T09:00:00 | -",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-04 | 2019-12-01T10:00:00 | -"),
                Files.readString(dataFile));
    }

    @Test
    public void saveThenLoadTasks_mixedTaskTypes_sameTaskDataReturned() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data").resolve("fifi.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.saveTasks(new TaskList(getSampleTasks()));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)",
                loadedTasks.get(2).toString());
        assertEquals(LocalDateTime.of(2019, 11, 29, 8, 0),
                loadedTasks.get(0).getCreatedAt().orElseThrow());
        assertEquals(LocalDateTime.of(2019, 12, 1, 17, 0),
                loadedTasks.get(0).getLastMarkedAt().orElseThrow());
        assertTrue(loadedTasks.get(1).getLastMarkedAt().isEmpty());
    }

    @Test
    public void loadTasks_invalidTimestamp_exceptionThrown() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Files.writeString(dataFile, "T | 0 | read book | yesterday | -");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile.toString()).loadTasks());

        assertEquals("Invalid task data at line 1: Invalid task description, dates, or timestamps.",
                exception.getMessage());
    }

    @Test
    public void loadTasks_malformedRecords_reportLineAndRecoverAfterCorrection() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Storage storage = new Storage(dataFile.toString());
        String[] invalidRecords = {"T", "T | 2 | work", "T | 0 | ", "T | 0 | work | -",
            "T | 0 | work | - | - | extra", "T | 0 | work|book", "T | 0 | work\u0000book",
            "D | 0 | work | 2026-02-30", "D | 0 | work | 0000-01-01",
            "E | 0 | work | 2026-09-21 | 2026-09-20",
            "T | 1 | work | 2026-09-21T09:00:00 | 2026-09-20T09:00:00",
            "T | 0 | work |  | -", "T | 0 | work | 0000-01-01T09:00:00 | -",
            "T | 0 | work | +10000-01-01T09:00:00 | -"};
        for (String invalidRecord : invalidRecords) {
            String content = "T | 0 | first task\n\n" + invalidRecord;
            Files.writeString(dataFile, content);
            IOException error = assertThrows(IOException.class, storage::loadTasks, invalidRecord);
            assertTrue(error.getMessage().startsWith("Invalid task data at line 3:"), error.getMessage());
            assertEquals(content, Files.readString(dataFile));

            Files.writeString(dataFile, "\nT | 0 | valid task\n\n");
            assertEquals("valid task", storage.loadTasks().get(0).getDescription());
        }
    }

    @Test
    public void loadTasks_blankLinesLegacyDuplicatesAndUnknownTimes_remainReadable() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Files.writeString(dataFile, "\nT | 0 | work\nT | 1 | work | - | -\n\n");
        List<Task> tasks = new Storage(dataFile.toString()).loadTasks();

        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).getCreatedAt().isEmpty());
        assertTrue(tasks.get(1).isMarked());
    }

    @Test
    public void loadTasks_moreThanLimit_rejectedWhileExactlyOneHundredLoads() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        String validContent = ("T | 0 | legacy duplicate\n").repeat(100);
        Files.writeString(dataFile, validContent);
        Storage storage = new Storage(dataFile.toString());
        assertEquals(100, storage.loadTasks().size());

        Files.writeString(dataFile, validContent + "T | 0 | overflow");
        IOException error = assertThrows(IOException.class, storage::loadTasks);
        assertTrue(error.getMessage().startsWith("Invalid task data at line 101:"));
        Files.writeString(dataFile, validContent);
        assertEquals(100, storage.loadTasks().size());
    }

    @Test
    public void saveTasks_atomicMoveUnsupported_preservesOriginalAndCleansTemporaryFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        String original = "T | 0 | original legacy task";
        Files.writeString(dataFile, original);
        Storage storage = new Storage(dataFile.toString()) {
            @Override
            protected void replaceSavedFile(Path temporaryFile) throws IOException {
                throw new AtomicMoveNotSupportedException(temporaryFile.toString(), dataFile.toString(),
                        "Simulated unsupported atomic move");
            }
        };

        assertThrows(AtomicMoveNotSupportedException.class, () -> storage.saveTasks(new TaskList(getSampleTasks())));
        assertEquals(original, Files.readString(dataFile));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(1, files.count());
        }
        new Storage(dataFile.toString()).saveTasks(new TaskList(getSampleTasks()));
        assertEquals(3, new Storage(dataFile.toString()).loadTasks().size());
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(1, files.count());
        }
    }

    private ArrayList<Task> getSampleTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo(true, "read book", LocalDateTime.of(2019, 11, 29, 8, 0),
                LocalDateTime.of(2019, 12, 1, 17, 0)));
        tasks.add(new Deadline(false, "return book", LocalDate.of(2019, 12, 2),
                LocalDateTime.of(2019, 11, 30, 9, 0), null));
        tasks.add(new Event(false, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4), LocalDateTime.of(2019, 12, 1, 10, 0), null));
        return tasks;
    }
}
