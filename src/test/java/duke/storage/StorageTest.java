package duke.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duke.exception.GaryException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void constructor_missingPath_throwsGaryException() {
        GaryException nullPathException = assertThrows(GaryException.class, () -> new Storage(null));
        GaryException blankPathException = assertThrows(GaryException.class, () -> new Storage("   "));

        assertEquals("The task storage path is missing.", nullPathException.getMessage());
        assertEquals("The task storage path is missing.", blankPathException.getMessage());
    }

    @Test
    public void constructor_invalidPath_throwsGaryException() {
        GaryException exception = assertThrows(GaryException.class, () -> new Storage("invalid\0path"));

        assertEquals("The task storage path is invalid.", exception.getMessage());
    }

    @Test
    public void load_missingFile_returnsEmptyList() {
        Path missing = tempDir.resolve("missing.txt");
        Storage storage = new Storage(missing.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals(0, tasks.size());
    }

    @Test
    public void saveThenLoad_roundTrip_preservesTasksAndDoneStatus() {
        Path file = tempDir.resolve("duke.txt");
        Storage storage = new Storage(file.toString());

        ArrayList<Task> toSave = new ArrayList<>();
        ToDo todo = new ToDo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 25));
        Event event = new Event("project meeting", LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));
        deadline.markAsDone();

        toSave.add(todo);
        toSave.add(deadline);
        toSave.add(event);

        storage.save(toSave);

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toDataString());
        assertEquals("D | 1 | return book | 2026-08-25", loaded.get(1).toDataString());
        assertEquals("E | 0 | project meeting | 2026-08-25 | 2026-08-26", loaded.get(2).toDataString());
    }

    @Test
    public void load_blankLines_ignoresBlankLines() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, """

                T | 0 | read book

                D | 1 | return book | 2026-08-25
                E | 0 | project meeting | 2026-08-25 | 2026-08-26
                """);
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toDataString());
        assertEquals("D | 1 | return book | 2026-08-25", loaded.get(1).toDataString());
        assertEquals("E | 0 | project meeting | 2026-08-25 | 2026-08-26", loaded.get(2).toDataString());
    }

    @Test
    public void load_windowsLineEndings_loadsEveryTask() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "T | 0 | read book\r\nD | 1 | return book | 2026-08-25\r\n");
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toDataString());
        assertEquals("D | 1 | return book | 2026-08-25", loaded.get(1).toDataString());
    }

    @Test
    public void save_createsParentDirectories() {
        Path nested = tempDir.resolve("nested").resolve("more").resolve("duke.txt");
        Storage storage = new Storage(nested.toString());

        ArrayList<Task> toSave = new ArrayList<>();
        toSave.add(new ToDo("read book"));

        storage.save(toSave);

        assertEquals(true, Files.exists(nested));
    }

    @Test
    public void save_existingFile_replacesOldContentsAndRemovesTemporaryFile() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "old contents\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("new task"));

        storage.save(tasks);

        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(file));
        try (Stream<Path> files = Files.list(tempDir)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith("gary-")));
        }
    }

    @Test
    public void save_emptyList_createsEmptyFile() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Storage storage = new Storage(file.toString());

        storage.save(new ArrayList<>());

        assertEquals("", Files.readString(file));
    }

    @Test
    public void save_parentPathIsFile_throwsGaryException() throws Exception {
        Path blockingParent = tempDir.resolve("not-a-directory");
        Files.writeString(blockingParent, "content");
        Storage storage = new Storage(blockingParent.resolve("duke.txt").toString());

        GaryException exception = assertThrows(GaryException.class, () -> storage.save(new ArrayList<>()));

        assertEquals("Could not save tasks. Check file permissions and available disk space.", exception.getMessage());
    }

    @Test
    public void save_nullTaskList_throwsAssertionError() {
        Storage storage = new Storage(tempDir.resolve("duke.txt").toString());

        assertThrows(AssertionError.class, () -> storage.save(null));
    }

    @Test
    public void save_taskListContainingNull_throwsAssertionError() {
        Storage storage = new Storage(tempDir.resolve("duke.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(null);

        assertThrows(AssertionError.class, () -> storage.save(tasks));
    }

    @Test
    public void load_malformedLine_throwsGaryExceptionWithLineNumber() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, """
                T | 0 | read book
                not a valid line
                """);
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 2: the task type is unknown.", exception.getMessage());
    }

    @Test
    public void load_invalidDate_throwsGaryException() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, """
                D | 0 | return book | 2026-02-30
                """);
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 1: a date is invalid.", exception.getMessage());
    }

    @Test
    public void load_invalidStatus_throwsGaryException() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "T | done | read book\n");
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 1: the completion status must be 0 or 1.",
                exception.getMessage());
    }

    @Test
    public void load_incorrectFieldCount_throwsGaryException() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "T | 0 | read book | unexpected\n");
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 1: the number of fields is incorrect.",
                exception.getMessage());
    }

    @Test
    public void load_emptyDescription_throwsGaryException() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "T | 0 |    \n");
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 1: the description is empty.", exception.getMessage());
    }

    @Test
    public void load_repeatedDescriptionWhitespace_normalizesDescription() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "T | 0 | read    the\tbook\n");
        Storage storage = new Storage(file.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals("T | 0 | read the book", tasks.get(0).toDataString());
    }

    @Test
    public void load_descriptionLongerThanLimit_throwsGaryException() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "T | 0 | " + "a".repeat(201) + "\n");
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 1: the description is too long.", exception.getMessage());
    }

    @Test
    public void load_duplicateTasks_throwsGaryException() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, """
                T | 0 | read book
                T | 1 | READ BOOK
                """);
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 2: the task is duplicated.", exception.getMessage());
    }

    @Test
    public void load_eventEndingBeforeStart_throwsGaryException() throws Exception {
        Path file = tempDir.resolve("duke.txt");
        Files.writeString(file, "E | 0 | meeting | 2026-08-26 | 2026-08-25\n");
        Storage storage = new Storage(file.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Task file data is invalid on line 1: the event must end after it starts.",
                exception.getMessage());
    }

    @Test
    public void load_pathIsDirectory_throwsGaryException() {
        Storage storage = new Storage(tempDir.toString());

        GaryException exception = assertThrows(GaryException.class, storage::load);

        assertEquals("Could not read the task file. Check its location and permissions.", exception.getMessage());
    }
}
