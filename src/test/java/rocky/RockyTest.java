package rocky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Rocky#getResponse}, which parses and runs every command,
 * and for loading and saving the task list.
 *
 * <p>Rocky keeps its task list in static fields and saves it to data/duke.txt
 * relative to the working directory. The Gradle test task runs in
 * build/test-run/, so these tests never touch the real save file.
 */
public class RockyTest {
    private static final Path SAVE_FILE = Path.of("data", "duke.txt");
    private static final String EMPTY_LIST_REPLY = "Your list is empty. Add something!";
    private static final String TODO_ADDED_REPLY = "New task. Do not worry. I remember it for you.";

    /** Stops the tests if they would overwrite the real save file in the project folder. */
    @BeforeAll
    public static void checkNotRunningInProjectFolder() {
        assertFalse(Files.exists(Path.of("build.gradle")),
                "Run these tests with ./gradlew test, so they don't overwrite the real data/duke.txt");
    }

    /** Empties Rocky's shared task list, so every test starts from a blank list. */
    @BeforeEach
    public void clearTaskList() {
        String[] listLines = Rocky.getResponse("list").split("\n");
        // Every line after the heading is one task.
        for (int i = 1; i < listLines.length; i++) {
            Rocky.getResponse("delete 1");
        }
        assertEquals(EMPTY_LIST_REPLY, Rocky.getResponse("list"));
    }

    @Test
    public void getResponse_todo_taskAddedAndCounted() {
        assertEquals(TODO_ADDED_REPLY + "\n  [T][ ] read book\nNow you have 1 task in the list.",
                Rocky.getResponse("todo read book"));
        assertEquals(TODO_ADDED_REPLY + "\n  [T][ ] return book\nNow you have 2 tasks in the list.",
                Rocky.getResponse("todo return book"));
    }

    @Test
    public void getResponse_extraWhitespace_ignored() {
        assertEquals(TODO_ADDED_REPLY + "\n  [T][ ] read book\nNow you have 1 task in the list.",
                Rocky.getResponse("   todo read book   "));
    }

    @Test
    public void getResponse_todoWithoutDescription_errorShown() {
        assertEquals("A todo needs a description. Try again?", Rocky.getResponse("todo"));
        assertEquals("A todo needs a description. Try again?", Rocky.getResponse("todo    "));
        assertEquals(EMPTY_LIST_REPLY, Rocky.getResponse("list"));
    }

    @Test
    public void getResponse_deadline_taskAddedWithFormattedDate() {
        assertEquals("Task with deadline. I track time carefully.\n  [D][ ] return book (by: Dec 01 2019)"
                + "\nNow you have 1 task in the list.",
                Rocky.getResponse("deadline return book /by 2019-12-01"));
    }

    @Test
    public void getResponse_deadlineMissingDescriptionOrDate_errorShown() {
        String expected = "A deadline needs a due date. Format: deadline <task> /by <when>";

        assertEquals(expected, Rocky.getResponse("deadline return book"));
        assertEquals(expected, Rocky.getResponse("deadline return book /by"));
        assertEquals(expected, Rocky.getResponse("deadline /by 2019-12-01"));
        assertEquals(EMPTY_LIST_REPLY, Rocky.getResponse("list"));
    }

    @Test
    public void getResponse_deadlineWithInvalidDate_errorShown() {
        assertEquals("Please give the date as yyyy-mm-dd, e.g. 2019-10-15.",
                Rocky.getResponse("deadline return book /by tomorrow"));
    }

    @Test
    public void getResponse_event_taskAddedWithFormattedDates() {
        assertEquals("Task spans two times. Start and end. I mark both."
                + "\n  [E][ ] meeting (from: Dec 02 2019 to: Dec 03 2019)"
                + "\nNow you have 1 task in the list.",
                Rocky.getResponse("event meeting /from 2019-12-02 /to 2019-12-03"));
    }

    @Test
    public void getResponse_eventMissingStartOrEnd_errorShown() {
        String missingStart = "An event needs a start time. Format: event <task> /from <start> /to <end>";
        String missingEnd = "An event needs an end time. Format: event <task> /from <start> /to <end>";

        assertEquals(missingStart, Rocky.getResponse("event meeting"));
        assertEquals(missingStart, Rocky.getResponse("event meeting /to 2019-12-03"));
        assertEquals(missingEnd, Rocky.getResponse("event meeting /from 2019-12-02"));
        assertEquals(missingEnd, Rocky.getResponse("event meeting /from 2019-12-02 /to"));
        assertEquals(EMPTY_LIST_REPLY, Rocky.getResponse("list"));
    }

    @Test
    public void getResponse_eventWithInvalidDate_errorShown() {
        assertEquals("Please give event dates as yyyy-mm-dd, e.g. 2019-10-15.",
                Rocky.getResponse("event meeting /from 2019-12-02 /to someday"));
    }

    @Test
    public void getResponse_listWithTasks_numberedFromOne() {
        Rocky.getResponse("todo read book");
        Rocky.getResponse("deadline return book /by 2019-12-01");

        assertEquals("Here is everything. Rocky show full list now\n1.[T][ ] read book"
                + "\n2.[D][ ] return book (by: Dec 01 2019)", Rocky.getResponse("list"));
    }

    @Test
    public void getResponse_markThenUnmark_doneStateChanges() {
        Rocky.getResponse("todo read book");

        assertEquals("Task complete! Good good good. I record success.\n  [T][x] read book",
                Rocky.getResponse("mark 1"));
        assertEquals("Here is everything. Rocky show full list now\n1.[T][x] read book", Rocky.getResponse("list"));
        assertEquals("Task not complete. I undo the mark.\n  [T][ ] read book",
                Rocky.getResponse("unmark 1"));
    }

    @Test
    public void getResponse_markWithMissingOrNonNumericIndex_errorShown() {
        Rocky.getResponse("todo read book");

        assertEquals("Which task? Give me a number, like: mark 2", Rocky.getResponse("mark"));
        assertEquals("\"two\" isn't a number I can work with.", Rocky.getResponse("mark two"));
        assertEquals("Which task? Give me a number, like: unmark 2", Rocky.getResponse("unmark"));
    }

    @Test
    public void getResponse_indexOutOfRange_errorShown() {
        Rocky.getResponse("todo read book");

        assertEquals("You only have 1 task(s), so there's no #0.", Rocky.getResponse("mark 0"));
        assertEquals("You only have 1 task(s), so there's no #2.", Rocky.getResponse("unmark 2"));
        assertEquals("You only have 1 task(s), so there's no #-3.", Rocky.getResponse("delete -3"));
    }

    @Test
    public void getResponse_indexOnEmptyList_errorShown() {
        assertEquals("Your list is empty, so there's nothing to mark.", Rocky.getResponse("mark 1"));
        assertEquals("Your list is empty, so there's nothing to delete.", Rocky.getResponse("delete 1"));
    }

    @Test
    public void getResponse_delete_taskRemovedAndRenumbered() {
        Rocky.getResponse("todo read book");
        Rocky.getResponse("todo return book");

        assertEquals("Task removed. Gone.\n  [T][ ] read book\nNow you have 1 task in the list.",
                Rocky.getResponse("delete 1"));
        assertEquals("Here is everything. Rocky show full list now\n1.[T][ ] return book", Rocky.getResponse("list"));
    }

    @Test
    public void getResponse_findWithMatches_caseInsensitiveMatchesNumbered() {
        Rocky.getResponse("todo read book");
        Rocky.getResponse("todo buy milk");
        Rocky.getResponse("deadline return book /by 2019-12-01");

        assertEquals("Searching... I compare each task to your word. Matches only.\n1.[T][ ] read book"
                + "\n2.[D][ ] return book (by: Dec 01 2019)", Rocky.getResponse("find BOOK"));
    }

    @Test
    public void getResponse_findWithoutMatches_noMatchesMessage() {
        Rocky.getResponse("todo read book");

        assertEquals("No matching tasks found.", Rocky.getResponse("find milk"));
    }

    @Test
    public void getResponse_findWithoutKeyword_errorShown() {
        assertEquals("What should I search for? Try again, e.g.: find book", Rocky.getResponse("find"));
        assertEquals("What should I search for? Try again, e.g.: find book", Rocky.getResponse("find   "));
    }

    @Test
    public void getResponse_unknownCommand_errorShown() {
        assertEquals("Hmm, \"blah\" isn't a command I know.", Rocky.getResponse("blah"));
        assertEquals("Hmm, \"Todo\" isn't a command I know.", Rocky.getResponse("Todo read book"));
        assertEquals("Hmm, \"\" isn't a command I know.", Rocky.getResponse(""));
    }

    @Test
    public void getResponse_bye_farewellShown() {
        assertEquals("Goodbye, friend. I power down now. Talk later.", Rocky.getResponse("bye"));
    }

    @Test
    public void isExitCommand_variousInputs_onlyByeMatches() {
        assertTrue(Rocky.isExitCommand("bye"));
        assertTrue(Rocky.isExitCommand("  bye  "));
        assertFalse(Rocky.isExitCommand("Bye"));
        assertFalse(Rocky.isExitCommand("bye now"));
        assertFalse(Rocky.isExitCommand(""));
    }

    @Test
    public void getResponse_changes_savedInSaveFileFormat() throws IOException {
        Rocky.getResponse("todo read book");
        Rocky.getResponse("deadline return book /by 2019-12-01");
        Rocky.getResponse("event meeting /from 2019-12-02 /to 2019-12-03");
        Rocky.getResponse("mark 2");

        assertEquals(List.of("T | 0 | read book", "D | 1 | return book | 2019-12-01",
                "E | 0 | meeting | 2019-12-02 | 2019-12-03"), Files.readAllLines(SAVE_FILE));
    }

    @Test
    public void initialize_saveFileWithMalformedLines_validTasksLoaded() throws IOException {
        // initialize() only loads once per run, so this is the only test that may call it.
        Files.createDirectories(SAVE_FILE.getParent());
        Files.write(SAVE_FILE, List.of(
                "T | 1 | read book",
                "X | 0 | unknown type",
                "D | 0 | missing its date",
                "D | 0 | bad date | tomorrow",
                "",
                "E | 0 | meeting | 2019-12-02 | 2019-12-03"));

        Rocky.initialize();
        String expected = "Here is everything. Rocky show full list now\n1.[T][x] read book"
                + "\n2.[E][ ] meeting (from: Dec 02 2019 to: Dec 03 2019)";
        assertEquals(expected, Rocky.getResponse("list"));

        // Calling it again must not load the same tasks a second time.
        Rocky.initialize();
        assertEquals(expected, Rocky.getResponse("list"));
    }
}
