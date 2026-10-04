package unicorn.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import unicorn.task.DeadlineTask;
import unicorn.task.EventTask;
import unicorn.task.Task;
import unicorn.task.TodoTask;

/**
 * Checks that task storage saves and reloads all supported task types.
 */
public class StorageTest {
    /**
     * Verifies that all task types can be saved, restored, and validated.
     *
     * @param testDirectory temporary directory supplied by JUnit for this test
     * @throws IOException if the temporary files cannot be read or written
     */
    @Test
    void storageRoundTripAndErrorsWork(@TempDir Path testDirectory) throws IOException {
        Path dataFile = testDirectory.resolve("unicorn.txt");
        if (!Storage.load(dataFile).isEmpty()) {
            throw new AssertionError("A first run should have no tasks.");
        }

        TodoTask completedTodo = new TodoTask("read | book");
        completedTodo.markAsDone();
        List<Task> tasks = List.of(
                completedTodo,
                new DeadlineTask("return book", LocalDateTime.of(2019, 12, 2, 18, 0)),
                new EventTask("project meeting", "Aug 6th", "2-4pm"));

        Storage.save(dataFile, tasks);

        List<String> expectedFileLines = List.of(
                "T | 1 | read \\| book",
                "D | 0 | return book | 2019-12-02T18:00:00",
                "E | 0 | project meeting | Aug 6th | 2-4pm");
        List<String> actualFileLines = Files.readAllLines(dataFile);
        assertEquals(expectedFileLines, actualFileLines, "Saved task data did not match.");

        List<String> expectedTaskLines = List.of(
                "[T] [X] read | book",
                "[D] [ ] return book (by: Dec 02 2019 6:00 PM)",
                "[E] [ ] project meeting (from: Aug 6th to: 2-4pm)");
        List<String> loadedTaskLines = Storage.load(dataFile).stream()
                .map(Task::toString)
                .toList();
        assertEquals(expectedTaskLines, loadedTaskLines, "Loaded task data did not match.");

        Path invalidDataFile = testDirectory.resolve("invalid.txt");
        Files.writeString(invalidDataFile, "D | 2 | return book | June 6th");
        assertInvalidTaskData(invalidDataFile);

        Path invalidDirectory = testDirectory.resolve("not-a-directory");
        Files.writeString(invalidDirectory, "This is a file, not a directory.");
        assertSaveFails(invalidDirectory.resolve("unicorn.txt"), tasks);
    }

    /**
     * Verifies that escaped fields survive loading and blank data lines are ignored.
     *
     * @param testDirectory temporary directory supplied by JUnit for this test
     * @throws IOException if the test data file cannot be written
     */
    @Test
    void load_escapedFieldsAndBlankLines_taskDetailsAreRestored(@TempDir Path testDirectory) throws IOException {
        Path dataFile = testDirectory.resolve("escaped.txt");
        Files.write(dataFile, List.of(
                "T | 0 | path \\\\ server\\|share",
                "",
                "E | 1 | planning\\|review | room\\\\two | 10\\|00"));

        List<Task> loadedTasks = Storage.load(dataFile);

        assertEquals(List.of(
                "[T] [ ] path \\ server|share",
                "[E] [X] planning|review (from: room\\two to: 10|00)"),
                loadedTasks.stream().map(Task::toString).toList(),
                "Escaped task details should be restored exactly.");
    }

    /**
     * Verifies that blank required fields and duplicate tasks are treated as corrupted data.
     *
     * @param testDirectory temporary directory supplied by JUnit for this test
     * @throws IOException if the test data files cannot be written
     */
    @Test
    void load_blankFieldsOrDuplicateTasks_invalidTaskDataRejected(@TempDir Path testDirectory) throws IOException {
        Path blankEventFieldFile = testDirectory.resolve("blank-event-field.txt");
        Files.writeString(blankEventFieldFile, "E | 0 | meeting |    | 3pm");
        assertInvalidTaskData(blankEventFieldFile);

        Path duplicateTaskFile = testDirectory.resolve("duplicate-task.txt");
        Files.write(duplicateTaskFile, List.of(
                "T | 0 | Read   Book",
                "T | 1 | read book"));
        assertInvalidTaskData(duplicateTaskFile);

        Path reversedEventFile = testDirectory.resolve("reversed-event.txt");
        Files.writeString(reversedEventFile,
                "E | 0 | meeting | 2026-09-30 1100 | 2026-09-30 1000");
        assertInvalidTaskData(reversedEventFile);
    }

    /**
     * Checks that malformed stored data is rejected with a clear exception.
     *
     * @param dataFile file containing invalid task data
     * @throws IOException if the test cannot read the test file
     */
    private static void assertInvalidTaskData(Path dataFile) throws IOException {
        assertThrows(IllegalArgumentException.class, () -> Storage.load(dataFile),
                "Invalid task data should not be loaded.");
    }

    /**
     * Checks that an unwritable storage location produces an I/O error.
     *
     * @param dataFile location that cannot be used as a file
     * @param tasks tasks to attempt to save
     */
    private static void assertSaveFails(Path dataFile, List<Task> tasks) {
        assertThrows(IOException.class, () -> Storage.save(dataFile, tasks),
                "A file cannot also be used as a parent directory.");
    }
}
