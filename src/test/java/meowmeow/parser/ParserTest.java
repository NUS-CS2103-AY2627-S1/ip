package meowmeow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meowmeow.MeowmeowException;
import meowmeow.command.AddCommand;
import meowmeow.command.Command;
import meowmeow.command.DeleteCommand;
import meowmeow.command.ExitCommand;
import meowmeow.command.FindCommand;
import meowmeow.command.ListCommand;
import meowmeow.command.MarkCommand;
import meowmeow.command.PriorityCommand;
import meowmeow.storage.Storage;
import meowmeow.task.Task;
import meowmeow.task.TaskList;
import meowmeow.task.TaskPriority;
import meowmeow.task.TaskStatus;
import meowmeow.task.Todo;
import meowmeow.ui.MessageStyle;
import meowmeow.ui.Ui;

/**
 * Tests for {@link Parser#parse(String)} - the one public entry point that
 * turns a raw input line into a {@link Command}.
 *
 * <p>{@code parse} is core, critical logic: every command the user types
 * flows through it, and it owns all the "I don't understand that" checks.
 * Its private helpers ({@code parseDeadline}, {@code parseEvent},
 * {@code parseTaskNumber}, {@code parsePriority}, the end-first
 * case-insensitive {@code /by} {@code /from} {@code /to} search) are covered
 * here through {@code parse} rather than tested directly - the "/p" and
 * "/priority" flag scan itself is tested directly in
 * {@link PriorityFlagTest}.
 *
 * <p>The concrete {@link Command} subclasses expose no getters, so a parsed
 * command is checked two ways: its runtime type (was the right kind of
 * command built?) and, where arguments matter, by running it against a real
 * {@link TaskList} and inspecting the result.
 */
public class ParserTest {

    private static final long SEED = 42L;

    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        ui = new Ui(MessageStyle.PLAIN, new Random(SEED));
        // A real Storage pointed at a throwaway folder, so commands that
        // save (add/mark/delete) don't touch the project's data file.
        storage = new Storage(ui, tempDir.toString(), "tasks.txt");
    }

    /** Parses {@code input} and runs the resulting command over {@code initialTasks}. */
    private TaskList parseAndRun(String input, Task... initialTasks) throws MeowmeowException {
        TaskList tasks = new TaskList(List.of(initialTasks));
        Parser.parse(input).execute(tasks, ui, storage);
        return tasks;
    }

    // ---- Command recognition: the right Command subclass is built ----

    @Test
    public void parse_bye_returnsExitCommand() throws MeowmeowException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_listNoDate_returnsListCommand() throws MeowmeowException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_listWithDate_returnsListCommand() throws MeowmeowException {
        assertInstanceOf(ListCommand.class, Parser.parse("list 2/12/2019"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws MeowmeowException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("find"));
    }

    @Test
    public void parse_todo_returnsAddCommand() throws MeowmeowException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo borrow book"));
    }

    @Test
    public void parse_deadline_returnsAddCommand() throws MeowmeowException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2/12/2019"));
    }

    @Test
    public void parse_event_returnsAddCommand() throws MeowmeowException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event camp /from 2/12/2019 /to 3/12/2019"));
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws MeowmeowException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmark_returnsMarkCommand() throws MeowmeowException {
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws MeowmeowException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_priority_returnsPriorityCommand() throws MeowmeowException {
        assertInstanceOf(PriorityCommand.class, Parser.parse("priority 1 high"));
    }

    @Test
    public void parse_keywordCaseInsensitive_stillRecognised() throws MeowmeowException {
        assertInstanceOf(ExitCommand.class, Parser.parse("BYE"));
        TaskList tasks = parseAndRun("TODO borrow book");
        assertEquals("[T][ ] borrow book", tasks.get(1).toString());
    }

    // ---- todo ----

    @Test
    public void parse_todo_addsTodoWithGivenDescription() throws MeowmeowException {
        TaskList tasks = parseAndRun("todo borrow book");

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] borrow book", tasks.get(1).toString());
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_todoWithPriorityFlagLast_priorityAppliedAndStrippedFromDescription() throws MeowmeowException {
        TaskList tasks = parseAndRun("todo borrow book /p high");

        assertEquals("[T][ ] borrow book (priority: HIGH)", tasks.get(1).toString());
    }

    @Test
    public void parse_todoWithPriorityFlagFirst_descriptionIsWhatFollows() throws MeowmeowException {
        TaskList tasks = parseAndRun("todo /p high borrow book");

        assertEquals("[T][ ] borrow book (priority: HIGH)", tasks.get(1).toString());
    }

    @Test
    public void parse_todoWithLongPriorityMarker_alsoAccepted() throws MeowmeowException {
        TaskList tasks = parseAndRun("todo borrow book /priority low");

        assertEquals("[T][ ] borrow book (priority: LOW)", tasks.get(1).toString());
    }

    @Test
    public void parse_todoWithUnknownPriorityLevel_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("todo borrow book /p urgent"));
    }

    @Test
    public void parse_todoWithTwoPriorityFlags_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("todo borrow book /p high /priority low"));
    }

    @Test
    public void parse_todoDescriptionContainsPipe_exceptionThrown() {
        // "|" is the save-file field separator; letting it through would
        // silently corrupt the description on the next load.
        assertThrows(MeowmeowException.class, () -> Parser.parse("todo review PR | approve"));
    }

    // ---- deadline ----

    @Test
    public void parse_deadline_addsDeadlineWithParsedDate() throws MeowmeowException {
        TaskList tasks = parseAndRun("deadline return book /by 2/12/2019 1800");

        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 pm)", tasks.get(1).toString());
    }

    @Test
    public void parse_deadlineByMarkerCaseInsensitive_accepted() throws MeowmeowException {
        TaskList tasks = parseAndRun("deadline return book /BY 2/12/2019");

        assertEquals("[D][ ] return book (by: Dec 2 2019)", tasks.get(1).toString());
    }

    @Test
    public void parse_deadlineDescriptionContainsBy_rightmostMarkerWins() throws MeowmeowException {
        // The "/by" inside the description must not be mistaken for the flag.
        TaskList tasks = parseAndRun("deadline note: pay /by card /by 2/12/2019");

        assertEquals("[D][ ] note: pay /by card (by: Dec 2 2019)", tasks.get(1).toString());
    }

    @Test
    public void parse_deadlineWithoutByMarker_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("deadline /by 2/12/2019"));
    }

    @Test
    public void parse_deadlineWithoutDate_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("deadline return book /by"));
    }

    @Test
    public void parse_deadlineWithUnparseableDate_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("deadline return book /by someday"));
    }

    @Test
    public void parse_deadlineWithPriorityFlagBeforeByMarker_bothApplied() throws MeowmeowException {
        TaskList tasks = parseAndRun("deadline return book /p high /by 2/12/2019");

        assertEquals("[D][ ] return book (by: Dec 2 2019) (priority: HIGH)", tasks.get(1).toString());
    }

    @Test
    public void parse_deadlineWithPriorityFlagAfterByMarker_bothApplied() throws MeowmeowException {
        TaskList tasks = parseAndRun("deadline return book /by 2/12/2019 /p high");

        assertEquals("[D][ ] return book (by: Dec 2 2019) (priority: HIGH)", tasks.get(1).toString());
    }

    @Test
    public void parse_deadlineDescriptionContainsPipe_exceptionThrown() {
        // "|" is the save-file field separator; letting it through would
        // silently corrupt the description on the next load.
        assertThrows(MeowmeowException.class, () -> Parser.parse("deadline pay bill | rent /by 2/12/2019"));
    }

    // ---- event ----

    @Test
    public void parse_event_addsEventWithParsedEndpoints() throws MeowmeowException {
        TaskList tasks = parseAndRun("event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");

        assertEquals("[E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)",
                tasks.get(1).toString());
    }

    @Test
    public void parse_eventEndBeforeStart_exceptionThrown() {
        assertThrows(MeowmeowException.class, () ->
                Parser.parse("event meeting /from 2/12/2019 1600 /to 2/12/2019 1400"));
    }

    @Test
    public void parse_eventMissingToMarker_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("event meeting /from 2/12/2019"));
    }

    @Test
    public void parse_eventMissingFromMarker_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("event meeting /to 2/12/2019"));
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        assertThrows(MeowmeowException.class, () ->
                Parser.parse("event /from 2/12/2019 /to 3/12/2019"));
    }

    @Test
    public void parse_eventWithUnparseableEndpoint_exceptionThrown() {
        assertThrows(MeowmeowException.class, () ->
                Parser.parse("event meeting /from 2/12/2019 /to whenever"));
    }

    @Test
    public void parse_eventWithPriorityFlag_applied() throws MeowmeowException {
        TaskList tasks = parseAndRun("event camp /from 2/12/2019 /to 3/12/2019 /p medium");

        assertEquals("[E][ ] camp (from: Dec 2 2019 to: Dec 3 2019) (priority: MEDIUM)", tasks.get(1).toString());
    }

    @Test
    public void parse_eventDescriptionContainsPipe_exceptionThrown() {
        // "|" is the save-file field separator; letting it through would
        // silently corrupt the description on the next load.
        assertThrows(MeowmeowException.class, () ->
                Parser.parse("event standup | planning /from 2/12/2019 /to 3/12/2019"));
    }

    // ---- mark / unmark / delete: task number parsing and effect ----

    @Test
    public void parse_mark_switchesNamedTaskToDone() throws MeowmeowException {
        TaskList tasks = parseAndRun("mark 1", new Todo("a"));

        assertEquals(TaskStatus.DONE, tasks.get(1).getStatus());
    }

    @Test
    public void parse_unmark_switchesNamedTaskToNotDone() throws MeowmeowException {
        Task done = new Todo("a");
        done.setStatus(TaskStatus.DONE);

        TaskList tasks = parseAndRun("unmark 1", done);

        assertEquals(TaskStatus.NOT_DONE, tasks.get(1).getStatus());
    }

    @Test
    public void parse_delete_removesNamedTask() throws MeowmeowException {
        TaskList tasks = parseAndRun("delete 1", new Todo("first"), new Todo("second"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] second", tasks.get(1).toString());
    }

    @Test
    public void parse_markWithoutNumber_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_markWithNonNumber_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("mark two"));
    }

    @Test
    public void parse_deleteWithoutNumber_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("delete"));
    }

    @Test
    public void parse_markOutOfRangeNumber_exceptionThrownWhenRun() {
        // Parser doesn't range-check; the error surfaces when the command runs.
        assertThrows(MeowmeowException.class, () -> parseAndRun("mark 5", new Todo("a")));
    }

    // ---- priority ----

    @Test
    public void parse_priority_setsNamedTasksPriority() throws MeowmeowException {
        TaskList tasks = parseAndRun("priority 1 high", new Todo("a"));

        assertEquals(TaskPriority.HIGH, tasks.get(1).getPriority());
    }

    @Test
    public void parse_priorityNone_clearsNamedTasksPriority() throws MeowmeowException {
        Task highPriority = new Todo("a");
        highPriority.setPriority(TaskPriority.HIGH);

        TaskList tasks = parseAndRun("priority 1 none", highPriority);

        assertEquals(TaskPriority.NONE, tasks.get(1).getPriority());
    }

    @Test
    public void parse_priorityShorthandLevel_accepted() throws MeowmeowException {
        TaskList tasks = parseAndRun("priority 1 l", new Todo("a"));

        assertEquals(TaskPriority.LOW, tasks.get(1).getPriority());
    }

    @Test
    public void parse_priorityWithoutNumber_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("priority"));
    }

    @Test
    public void parse_priorityWithoutLevel_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("priority 1"));
    }

    @Test
    public void parse_priorityWithNonNumber_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("priority two high"));
    }

    @Test
    public void parse_priorityWithUnknownLevel_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("priority 1 urgent"));
    }

    @Test
    public void parse_priorityOutOfRangeNumber_exceptionThrownWhenRun() {
        // Parser doesn't range-check; the error surfaces when the command runs.
        assertThrows(MeowmeowException.class, () -> parseAndRun("priority 5 high", new Todo("a")));
    }

    // ---- unknown / empty input ----

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("sing a song"));
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse(""));
    }

    @Test
    public void parse_listWithUnparseableDate_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> Parser.parse("list someday"));
    }
}
