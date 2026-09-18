package altair.storage;

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

import altair.AltairException;
import altair.task.Deadline;
import altair.task.Event;
import altair.task.Task;
import altair.task.Todo;

/**
 * Unit tests for {@link Storage#save(List)} and {@link Storage#load()}.
 *
 * <p>These are the highest-value methods to cover in the project: file
 * persistence is critical business logic (a bug here silently loses or
 * corrupts the user's task list), and {@code load} drives the most complex
 * branching in the codebase &mdash; the private line parser that validates the
 * type marker, the done/not-done flag, the field count, and the stored date
 * format, reporting the offending line number on failure.</p>
 *
 * <p>Each test writes to a JUnit-managed temporary directory
 * ({@link TempDir}), so nothing touches the real {@code ./data/} folder and
 * every test starts from a clean slate.</p>
 */
public class StorageTest {

    /** A fresh temporary directory is created and deleted per test method. */
    @TempDir
    private Path tempDir;

    /** Creates a {@link Storage} backed by a file inside the temporary directory. */
    private Storage storageBackedBy(String fileName) {
        return new Storage(tempDir.resolve(fileName).toString());
    }

    /** Writes the given lines (each followed by a newline) to a file in the temporary directory. */
    private Path writeSaveFile(String fileName, String... lines) throws IOException {
        Path path = tempDir.resolve(fileName);
        Files.write(path, List.of(lines));
        return path;
    }

    /** Serializes a task list the same way {@code Storage} stores it, for comparison. */
    private static List<String> asFileLines(List<Task> tasks) {
        return tasks.stream().map(Task::toFileString).toList();
    }

    // ----- save -----

    @Test
    public void save_emptyList_writesAnEmptyFile() throws Exception {
        Path path = tempDir.resolve("altair.txt");
        storageBackedBy("altair.txt").save(List.of());

        assertTrue(Files.exists(path));
        assertEquals(List.of(), Files.readAllLines(path));
    }

    @Test
    public void save_missingParentDirectory_isCreated() throws Exception {
        // The save file lives in a "data" sub-folder that does not exist yet.
        Storage storage = storageBackedBy("data/altair.txt");
        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(tempDir.resolve("data/altair.txt")));
    }

    @Test
    public void save_multipleTaskTypes_writesOneSerializedLinePerTask() throws Exception {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        List<Task> tasks = List.of(
                new Todo("read book"),
                deadline,
                new Event("orientation camp", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 20)));

        storageBackedBy("altair.txt").save(tasks);

        assertEquals(
                List.of(
                        "T | 0 | read book",
                        "D | 1 | return book | 2019-10-15",
                        "E | 0 | orientation camp | 2019-10-15 - 2019-10-20"),
                Files.readAllLines(tempDir.resolve("altair.txt")));
    }

    @Test
    public void save_calledAgain_replacesThePreviousContents() throws Exception {
        Storage storage = storageBackedBy("altair.txt");
        storage.save(List.of(new Todo("first"), new Todo("second")));

        storage.save(List.of(new Todo("only one now")));

        assertEquals(List.of("T | 0 | only one now"),
                Files.readAllLines(tempDir.resolve("altair.txt")));
    }

    // ----- load: absent, empty, and blank input -----

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws Exception {
        List<Task> tasks = storageBackedBy("does-not-exist.txt").load();
        assertEquals(List.of(), tasks);
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws Exception {
        writeSaveFile("altair.txt");
        assertEquals(List.of(), storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_blankLines_areSkipped() throws Exception {
        writeSaveFile("altair.txt", "", "   ", "T | 0 | read book", "");
        assertEquals(1, storageBackedBy("altair.txt").load().size());
    }

    // ----- load: each task type is restored correctly -----

    @Test
    public void load_todoLine_restoresTodoWithDescription() throws Exception {
        writeSaveFile("altair.txt", "T | 0 | read book");

        List<Task> tasks = storageBackedBy("altair.txt").load();

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void load_doneStatusFlag_restoresTaskAsDone() throws Exception {
        writeSaveFile("altair.txt", "T | 1 | read book");

        List<Task> tasks = storageBackedBy("altair.txt").load();

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void load_deadlineLine_restoresDeadlineWithParsedDate() throws Exception {
        writeSaveFile("altair.txt", "D | 0 | return book | 2019-10-15");

        List<Task> tasks = storageBackedBy("altair.txt").load();

        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(0).toString());
    }

    @Test
    public void load_eventLine_restoresEventWithBothParsedDates() throws Exception {
        writeSaveFile("altair.txt", "E | 0 | orientation camp | 2019-10-15 - 2019-10-20");

        List<Task> tasks = storageBackedBy("altair.txt").load();

        assertEquals("[E][ ] orientation camp (from: Oct 15 2019 to: Oct 20 2019)",
                tasks.get(0).toString());
    }

    // ----- load: malformed input is rejected with a line-specific error -----

    @Test
    public void load_lineWithTooFewFields_throwsAltairException() throws Exception {
        writeSaveFile("altair.txt", "garbage");
        assertThrows(AltairException.class, () -> storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_unknownTypeMarker_throwsAltairException() throws Exception {
        writeSaveFile("altair.txt", "X | 0 | mystery task");
        assertThrows(AltairException.class, () -> storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_invalidStatusValue_throwsAltairException() throws Exception {
        writeSaveFile("altair.txt", "T | 2 | read book");
        assertThrows(AltairException.class, () -> storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_emptyDescription_throwsAltairException() throws Exception {
        writeSaveFile("altair.txt", "T | 0 | ");
        assertThrows(AltairException.class, () -> storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_deadlineWithoutDateField_throwsAltairException() throws Exception {
        writeSaveFile("altair.txt", "D | 0 | return book");
        assertThrows(AltairException.class, () -> storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_deadlineWithUnparseableDate_throwsAltairException() throws Exception {
        writeSaveFile("altair.txt", "D | 0 | return book | 15-10-2019");
        assertThrows(AltairException.class, () -> storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_eventWithOnlyOneDate_throwsAltairException() throws Exception {
        writeSaveFile("altair.txt", "E | 0 | orientation camp | 2019-10-15");
        assertThrows(AltairException.class, () -> storageBackedBy("altair.txt").load());
    }

    @Test
    public void load_malformedLine_reportsTheOffendingLineNumber() throws Exception {
        writeSaveFile("altair.txt",
                "T | 0 | valid one",
                "T | 0 | valid two",
                "T | bad | broken three");

        AltairException thrown = assertThrows(AltairException.class,
                () -> storageBackedBy("altair.txt").load());

        assertTrue(thrown.getMessage().contains("line 3"),
                "expected the error to name line 3 but was: " + thrown.getMessage());
    }

    // ----- round trip -----

    @Test
    public void saveThenLoad_roundTripsEveryTaskTypeAndStatus() throws Exception {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event event = new Event("orientation camp",
                LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 20));
        event.markAsDone();
        List<Task> original = List.of(todo, deadline, event);

        Storage storage = storageBackedBy("altair.txt");
        storage.save(original);
        List<Task> reloaded = storage.load();

        // Comparing the serialized forms checks description, type, status, and dates together.
        assertEquals(asFileLines(original), asFileLines(reloaded));
    }
}
