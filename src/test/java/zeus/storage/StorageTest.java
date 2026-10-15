package zeus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zeus.exception.ZeusException;
import zeus.task.Deadline;
import zeus.task.Event;
import zeus.task.Task;
import zeus.task.TaskList;
import zeus.task.Todo;

/** Tests persistence, recovery, and escaping behavior in {@link Storage}. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoad_allTaskTypes_roundTripsTaskData() throws IOException, ZeusException {
        Path dataFile = temporaryDirectory.resolve("nested/data/zeus.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("compare A | B \\ C");
        Deadline deadline = new Deadline("return book", LocalDate.parse("2026-09-06"));
        deadline.markAsDone();
        Event event = new Event("meeting", LocalDate.parse("2026-09-07"),
                LocalDate.parse("2026-09-08"));

        storage.save(new TaskList(List.of(todo, deadline, event)));

        List<String> expectedLines = List.of(
                "T | 0 | compare A \\| B \\\\ C",
                "D | 1 | return book | 2026-09-06",
                "E | 0 | meeting | 2026-09-07 | 2026-09-08");
        assertEquals(expectedLines, Files.readAllLines(dataFile));

        List<String> warnings = new ArrayList<>();
        List<String> loadedData = storage.load(warnings).stream()
                .map(Task::toDataString)
                .toList();
        assertTrue(warnings.isEmpty());
        assertEquals(expectedLines, loadedData);
    }

    @Test
    public void load_missingFile_returnsEmptyListWithoutWarning() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());
        List<String> warnings = new ArrayList<>();

        List<Task> loadedTasks = storage.load(warnings);

        assertTrue(loadedTasks.isEmpty());
        assertTrue(warnings.isEmpty());
    }

    @Test
    public void load_malformedRecords_skipsInvalidLinesAndReportsReasons() throws IOException {
        Path dataFile = temporaryDirectory.resolve("zeus.txt");
        Files.write(dataFile, List.of(
                "T | 1 | valid todo",
                "",
                "X | 0 | unknown type",
                "T | 2 | bad status",
                "D | 0 | missing date",
                "D | 0 | impossible date | 2025-02-29",
                "E | 0 | backwards | 2026-09-08 | 2026-09-07",
                "T | 0 | bad \\q escape",
                "T | 0 | trailing \\"));
        Storage storage = new Storage(dataFile.toString());
        List<String> warnings = new ArrayList<>();

        List<Task> loadedTasks = storage.load(warnings);

        assertEquals(List.of("T | 1 | valid todo"),
                loadedTasks.stream().map(Task::toDataString).toList());
        assertEquals(List.of(
                "Saved data line 3 was ignored: Unknown task type 'X'.",
                "Saved data line 4 was ignored: Completion status must be 0 or 1, not '2'.",
                "Saved data line 5 was ignored: Task type 'D' needs 4 fields, but this "
                        + "record has 3.",
                "Saved data line 6 was ignored: The deadline date must use yyyy-MM-dd, "
                        + "for example 2019-10-15.",
                "Saved data line 7 was ignored: The event end date cannot be before its "
                        + "start date.",
                "Saved data line 8 was ignored: Invalid escape sequence '\\q'.",
                "Saved data line 9 was ignored: The record ends with an incomplete escape "
                        + "sequence."), warnings);
    }

    @Test
    public void load_pathIsDirectory_returnsEmptyListWithWarning() throws IOException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("tasks"));
        Storage storage = new Storage(directory.toString());
        List<String> warnings = new ArrayList<>();

        List<Task> loadedTasks = storage.load(warnings);

        assertTrue(loadedTasks.isEmpty());
        assertEquals(List.of(directory
                + " is not a readable task file. Starting with an empty list."), warnings);
    }

    @Test
    public void save_pathIsDirectory_exceptionThrown() throws IOException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("tasks"));
        Storage storage = new Storage(directory.toString());

        ZeusException exception = assertThrows(ZeusException.class, () ->
                storage.save(new TaskList()));

        assertEquals("I couldn't save your tasks to " + directory + ".",
                exception.getMessage());
    }
}
