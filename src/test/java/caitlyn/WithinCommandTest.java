package caitlyn;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests exact within-command responses and state preservation after rejected inputs. */
class WithinCommandTest {
    private static final String FORMAT_ERROR = "I beg your pardon, master. Please use: "
            + "within task /from start /to end. Provide a description and both boundaries, "
            + "with /from followed by /to exactly once.";

    private final List<Task> tasks = new ArrayList<>();
    @TempDir
    private Path temporaryDirectory;

    private Path taskFile;
    private TaskStorage storage;

    private final List<String> messages = new ArrayList<>();
    private final Ui ui = new Ui(messages::add);

    @BeforeEach
    void setUp() throws IOException {
        taskFile = temporaryDirectory.resolve("data").resolve("duke.txt");
        storage = new TaskStorage(taskFile);
        storage.save(tasks);
    }

    @Test
    void execute_validCommand_emitsExactConfirmationAndPersists() throws Exception {
        execute("within collect certificate /from 2027-01-15 /to 2027-01-25");

        assertEquals(List.of("     Got it. I've added this task:",
                "       [W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)",
                "     Now you have 1 tasks in the list."), messages);
        assertEquals(List.of("W | 0 | collect certificate | 2027-01-15 | 2027-01-25"),
                Files.readAllLines(taskFile));
    }

    @Test
    void execute_structuralWhitespace_preservesDescriptionAndMarkerLikeWords() throws Exception {
        execute(" \twithin\t collect  /fromage | \\backup \t/from\t2027-01-15\t 0900 "
                + "\t/to  2027-01-25  17:00  ");

        assertEquals("collect  /fromage | \\backup", tasks.get(0).getDescription());
        assertEquals(List.of("W | 0 | collect  /fromage \\| \\\\backup | 2027-01-15T09:00 | 2027-01-25T17:00"),
                Files.readAllLines(taskFile));
    }

    @Test
    void execute_malformedStructure_rejectsWithoutChangingExistingData() throws Exception {
        execute("todo keep");
        execute("mark 1");
        for (String input : List.of("within", "within /from 2027-01-15 /to 2027-01-25",
                "within task /from 2027-01-15", "within task /from /to 2027-01-25",
                "within task /from 2027-01-15 /to",
                "within task /to 2027-01-25 /from 2027-01-15",
                "within task /from 2027-01-15 /from 2027-01-16 /to 2027-01-25",
                "within task /from 2027-01-15 /to 2027-01-25 /to 2027-01-26",
                "within task /from2027-01-15 /to 2027-01-25",
                "within task /FROM 2027-01-15 /to 2027-01-25",
                "within task /from tomorrow /to")) {
            assertRejected(input, FORMAT_ERROR);
        }
    }

    @Test
    void execute_invalidWindows_reportsFirstBoundaryErrorWithoutMutation() throws Exception {
        execute("todo keep");
        assertRejected("within task /from 2027-02-30 /to tomorrow",
                "I beg your pardon, master. Please provide a valid start date, "
                        + "for example: 2027-01-15 or 15/1/2027 0900.");
        for (String end : List.of("tomorrow", "2027-01-25 extra", "2027-01-25 /other value")) {
            assertRejected("within task /from 2027-01-15 /to " + end,
                    "I beg your pardon, master. Please provide a valid end date, "
                            + "for example: 2027-01-25 or 25/1/2027 1700.");
        }
        assertRejected("within task /from 2027-01-15T09:00:01 /to tomorrow",
                "I beg your pardon, master. The start time must use minute precision; "
                        + "seconds and fractional seconds must be zero.");
        assertRejected("within task /from 2027-01-15 /to 2027-01-25T17:00:00.001",
                "I beg your pardon, master. The end time must use minute precision; "
                        + "seconds and fractional seconds must be zero.");
        assertRejected("within task /from 2027-01-26 /to 2027-01-25",
                "I beg your pardon, master. The start of the window must not be after its end.");
        assertRejected("WITHIN task /from 2027-01-15 /to 2027-01-25",
                "I humbly beg your pardon, master. I do not know how to carry out that command.");
    }

    @Test
    void execute_mixedTasksAndDuplicates_reusesSearchStatusAndDeletion() throws Exception {
        execute("todo keep");
        execute("within collect certificate /from 2027-01-15 /to 2027-01-25");
        execute("within collect certificate /from 2027-01-15 /to 2027-01-25");
        execute("mark 2");
        messages.clear();
        execute("find CERTIFICATE");
        assertEquals(List.of("     Here are the matching tasks in your list:",
                "     2.[W][X] collect certificate (within: Jan 15 2027 to: Jan 25 2027)",
                "     3.[W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)"), messages);
        execute("unmark 2");
        execute("delete 2");
        assertEquals(List.of("T | 0 | keep", "W | 0 | collect certificate | 2027-01-15 | 2027-01-25"),
                Files.readAllLines(taskFile));
    }

    private void execute(String input) throws CaitlynException {
        Parser.parse(input, storage).execute(tasks, ui);
    }

    private void assertRejected(String input, String expectedMessage) throws IOException {
        byte[] originalFile = Files.readAllBytes(taskFile);
        List<String> originalTasks = tasks.stream().map(Task::toStorageString).toList();
        messages.clear();
        CaitlynException exception = assertThrows(CaitlynException.class, () -> execute(input));
        assertEquals(expectedMessage, exception.getMessage(), input);
        assertEquals(originalTasks, tasks.stream().map(Task::toStorageString).toList(), input);
        assertArrayEquals(originalFile, Files.readAllBytes(taskFile), input);
        assertTrue(messages.isEmpty(), "A rejected command must not emit a success message: " + input);
    }
}
