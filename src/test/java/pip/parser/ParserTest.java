package pip.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pip.command.Command;
import pip.exception.ParserException;
import pip.storage.Storage;
import pip.task.Deadline;
import pip.task.Event;
import pip.task.Task;
import pip.task.TaskList;
import pip.task.Todo;
import pip.ui.Ui;

/**
 * Tests {@link Parser#parse(String)}.
 *
 * <p>Commands are exercised end-to-end (parse, then execute against a real
 * {@link TaskList}) rather than by inspecting the returned {@link Command}
 * directly, since Command subclasses intentionally expose no getters.
 * This also verifies that Parser and Command divide validation correctly:
 * some checks (e.g. malformed index) fail at parse time, while others
 * (e.g. out-of-range index) only fail once the command is executed.
 */
class ParserTest {
    /** Woodland aliases retain the behavior and validation of their canonical commands. */
    @Test
    void parse_woodlandAliases_executeAndValidateLikeOriginalCommands() throws Exception {
        TaskList tasks = new TaskList();
        Parser.parse("gather collect acorns").execute(tasks, ui, storage);
        assertEquals("collect acorns", tasks.get(0).getName());
        assertTrue(Parser.parse("stash") instanceof pip.command.ListCommand);
        assertTrue(Parser.parse("sniff acorns") instanceof pip.command.FindCommand);
        assertTrue(Parser.parse("scamper").isExit());
        assertThrows(ParserException.class, () -> Parser.parse("gather"));
        assertThrows(ParserException.class, () -> Parser.parse("sniff"));
        assertThrows(ParserException.class, () -> Parser.parse("stash extra"));
        assertThrows(ParserException.class, () -> Parser.parse("scamper extra"));
    }


    @TempDir
    Path tempDir;

    private Ui ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        ui = new Ui();
        storage = new Storage(tempDir.resolve("pip_test.txt").toString());
    }

    // ---------- Basic input validation ----------

    @Test
    void parse_nullInput_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse(null));
    }

    @Test
    void parse_blankInput_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("   "));
    }

    @Test
    void parse_unknownCommandWord_throwsParserException() {
        ParserException ex = assertThrows(ParserException.class, () -> Parser.parse("frobnicate stuff"));
        assertTrue(ex.getMessage().contains("Unknown task type"));
    }

    @Test
    void parse_shortAliases_areAcceptedForAllCommands() {
        assertDoesNotThrow(() -> Parser.parse("b"));
        assertDoesNotThrow(() -> Parser.parse("l"));
        assertDoesNotThrow(() -> Parser.parse("m 1"));
        assertDoesNotThrow(() -> Parser.parse("um 1"));
        assertDoesNotThrow(() -> Parser.parse("d 1"));
        assertDoesNotThrow(() -> Parser.parse("f book"));
        assertDoesNotThrow(() -> Parser.parse("t buy milk"));
        assertDoesNotThrow(() -> Parser.parse("dl submit report /by 2024-03-15"));
        assertDoesNotThrow(() -> Parser.parse("e team meeting /from 2024-05-01 /to 2024-05-10"));
    }

    // ---------- bye / list: commands that take no arguments ----------

    @Test
    void parse_bye_returnsExitCommand() throws Exception {
        Command c = Parser.parse("bye");
        assertTrue(c.isExit());
    }

    @Test
    void parse_byeWithTrailingArgs_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("bye now"));
    }

    @Test
    void parse_list_returnsNonExitingCommand() throws Exception {
        Command c = Parser.parse("list");
        assertFalse(c.isExit());
        // Should not throw when executed against an empty list.
        c.execute(new TaskList(), ui, storage);
    }

    @Test
    void parse_listWithTrailingArgs_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("list today"));
    }

    // ---------- todo ----------

    @Test
    void parse_todoWithEmptyDescription_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("todo"));
        assertThrows(ParserException.class, () -> Parser.parse("todo   "));
    }

    @Test
    void parse_validTodo_addsTodoTaskToList() throws Exception {
        Command c = Parser.parse("todo read book");
        TaskList tasks = new TaskList();

        c.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0) instanceof Todo);
        assertEquals("read book", tasks.get(0).getName());
    }

    // ---------- deadline ----------

    @Test
    void parse_deadlineMissingByKeyword_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("deadline submit report"));
    }

    @Test
    void parse_deadlineMissingDescriptionOrDate_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("deadline /by 2024-01-01"));
        assertThrows(ParserException.class, () -> Parser.parse("deadline submit report /by "));
    }

    @Test
    void parse_deadlineInvalidDateFormat_throwsParserException() {
        ParserException ex = assertThrows(ParserException.class,
                () -> Parser.parse("deadline submit report /by tomorrow"));
        assertTrue(ex.getMessage().contains("valid date"));
    }

    @Test
    void parse_validDeadline_addsDeadlineTaskWithCorrectDate() throws Exception {
        Command c = Parser.parse("deadline submit report /by 2024-03-15");
        TaskList tasks = new TaskList();

        c.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertTrue(task instanceof Deadline);
        assertEquals("submit report", task.getName());
        assertEquals("2024-03-15", ((Deadline) task).getDeadline().toString());
    }

    // ---------- event ----------

    @Test
    void parse_eventMissingFromOrTo_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("event trip"));
        assertThrows(ParserException.class, () -> Parser.parse("event trip /from 2024-01-01"));
    }

    @Test
    void parse_eventEndDateBeforeStartDate_throwsParserException() {
        ParserException ex = assertThrows(ParserException.class,
                () -> Parser.parse("event trip /from 2024-05-10 /to 2024-05-01"));
        assertTrue(ex.getMessage().contains("must not be before"));
    }

    @Test
    void parse_validEvent_addsEventTaskWithCorrectDates() throws Exception {
        Command c = Parser.parse("event trip /from 2024-05-01 /to 2024-05-10");
        TaskList tasks = new TaskList();

        c.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertTrue(task instanceof Event);
        assertEquals("2024-05-01", ((Event) task).getStart().toString());
        assertEquals("2024-05-10", ((Event) task).getEnd().toString());
    }

    @Test
    void parse_eventArgumentsInEitherOrder_addsEquivalentEventTasks() throws Exception {
        Command reordered = Parser.parse("event /to 2024-05-10 team trip /from 2024-05-01");
        Command interleaved = Parser.parse("event team /to 2024-05-10 trip /from 2024-05-01");

        TaskList tasks = new TaskList();
        reordered.execute(tasks, ui, storage);
        interleaved.execute(tasks, ui, storage);

        assertEquals(2, tasks.size());
        assertEquals("team trip", tasks.get(0).getName());
        assertEquals("team trip", tasks.get(1).getName());
        assertEquals(((Event) tasks.get(0)).getStart(), ((Event) tasks.get(1)).getStart());
        assertEquals(((Event) tasks.get(0)).getEnd(), ((Event) tasks.get(1)).getEnd());
    }

    @Test
    void parse_eventDuplicateOrMissingDateMarker_throwsParserException() {
        assertThrows(ParserException.class,
                () -> Parser.parse("event trip /from 2024-05-01 /from 2024-05-02 /to 2024-05-10"));
        assertThrows(ParserException.class, () -> Parser.parse("event trip /from /to 2024-05-10"));
    }

    // ---------- mark / unmark ----------

    @Test
    void parse_markNonNumericIndex_throwsParserExceptionAtParseTime() {
        assertThrows(ParserException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    void parse_markZeroOrNegativeIndex_throwsParserExceptionAtParseTime() {
        assertThrows(ParserException.class, () -> Parser.parse("mark 0"));
        assertThrows(ParserException.class, () -> Parser.parse("mark -1"));
    }

    @Test
    void parse_markOutOfRangeIndex_isAcceptedAtParseTimeButFailsAtExecuteTime() throws Exception {
        // A syntactically valid but semantically invalid index is only
        // rejected once the command is executed against the actual list.
        Command c = Parser.parse("mark 5");
        TaskList emptyTasks = new TaskList();
        assertThrows(ParserException.class, () -> c.execute(emptyTasks, ui, storage));
    }

    @Test
    void parse_validMarkAndUnmark_togglesTaskCompletion() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("water plants"));

        Parser.parse("mark 1").execute(tasks, ui, storage);
        assertTrue(tasks.get(0).isCompleted());

        Parser.parse("unmark 1").execute(tasks, ui, storage);
        assertFalse(tasks.get(0).isCompleted());
    }

    // ---------- delete ----------

    @Test
    void parse_deleteNonNumericIndex_throwsParserExceptionAtParseTime() {
        assertThrows(ParserException.class, () -> Parser.parse("delete xyz"));
    }

    @Test
    void parse_validDelete_removesCorrectTaskAndShrinksList() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        Parser.parse("delete 1").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getName());
    }

    // ---------- find ----------

    @Test
    void parse_findWithEmptyKeyword_throwsParserException() {
        assertThrows(ParserException.class, () -> Parser.parse("find"));
        assertThrows(ParserException.class, () -> Parser.parse("find   "));
    }

    @Test
    void parse_validFind_showsOnlyMatchingTasksWithoutModifyingList() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write report"));

        Parser.parse("find book").execute(tasks, ui, storage);

        // find() is read-only: the original list must be untouched.
        assertEquals(2, tasks.size());
    }
}
