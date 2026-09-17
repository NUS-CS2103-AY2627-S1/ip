package crystal.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import crystal.command.AddCommand;
import crystal.command.Command;
import crystal.command.DeleteCommand;
import crystal.command.ExitCommand;
import crystal.command.FindCommand;
import crystal.command.HelpCommand;
import crystal.command.ListCommand;
import crystal.command.MarkCommand;
import crystal.command.UnmarkCommand;
import crystal.exception.CrystalException;
import crystal.storage.Storage;
import crystal.task.Task;
import crystal.task.TaskList;
import crystal.task.Todo;
import crystal.ui.Ui;

/**
 * Tests command recognition, argument extraction, and parser error messages.
 */
public class ParserTest {
    @TempDir
    private Path tempDirectory;

    /** Verifies that every supported keyword creates the corresponding command type. */
    @Test
    public void parse_supportedCommandKeywords_returnsMatchingCommandTypes()
            throws CrystalException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline submit report /by 2Dec26"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event workshop /from 6am /to 630pm"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("within collect certificate /from 15Jan27 /to 25Jan27"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ListCommand.class, Parser.parse("list /on 2Dec26"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    /** Verifies extraction and normalization of todo, deadline, and event details. */
    @Test
    public void parse_addCommands_executeWithExtractedAndNormalizedTaskDetails()
            throws CrystalException {
        assertParsedTaskData("todo read book", "T | 0 | read book");
        assertParsedTaskData("deadline submit report /by 2Dec26 630pm",
                "D | 0 | submit report | 02 Dec 2026 1830");
        assertParsedTaskData("event workshop /from Monday 6am /to 6.30pm",
                "E | 0 | workshop | Monday 0600 | 1830");
        assertParsedTaskData("within collect certificate /from 15Jan27 /to 25 January 2027",
                "W | 0 | collect certificate | 15 Jan 2027 | 25 Jan 2027");
        assertParsedTaskData(
                "within collect certificate /from after the ceremony /to before closing",
                "W | 0 | collect certificate | after the ceremony | before closing");
        assertParsedTaskData("within reversed period /from 25Jan27 /to 15Jan27",
                "W | 0 | reversed period | 25 Jan 2027 | 15 Jan 2027");
        assertParsedTaskData("within invalid calendar date /from 31Feb27 /to someday",
                "W | 0 | invalid calendar date | 31Feb27 | someday");
    }

    /** Verifies conversion of user-facing task numbers into zero-based indexes. */
    @Test
    public void parse_mutationCommands_convertsOneBasedTaskNumberToZeroBasedIndex()
            throws CrystalException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        Ui ui = new Ui();
        Storage storage = new Storage(tempDirectory.resolve("mutations.txt"));

        Parser.parse("mark 2").execute(tasks, ui, storage);
        assertFalse(first.isDone());
        assertTrue(second.isDone());

        Parser.parse("unmark 2").execute(tasks, ui, storage);
        assertFalse(second.isDone());

        Parser.parse("delete 1").execute(tasks, ui, storage);
        assertEquals(List.of(second), tasks.getTasks());
    }

    /** Verifies that empty input receives a specific actionable error. */
    @Test
    public void parse_emptyCommands_throwEmptyCommandError() {
        assertParseErrors("I cant reply to nothing...", null, "", "   ");
    }

    /** Verifies that accidental outer spaces receive a specific actionable error. */
    @Test
    public void parse_commandsWithOuterWhitespace_throwWhitespaceError() {
        String expected = "Please remove spaces from the start or end of your command!";

        assertParseErrors(expected,
                " list", "list ", "list /on    ", "todo read book ",
                " deadline report /by Friday");
    }

    /** Verifies that unknown input and non-exact standalone commands receive the unknown error. */
    @Test
    public void parse_unknownOrNonExactStandaloneCommands_throwUnknownCommandError() {
        assertParseErrors("I don't know what that means :-(",
                "read book", "help me", "bye now");
    }

    /** Verifies todo usage errors for missing and blank descriptions. */
    @Test
    public void parse_malformedTodoCommands_throwTodoUsageError() {
        String expected = "A todo must have a description!";

        assertParseErrors(expected, "todo", "todo  read book");
    }

    /** Verifies deadline usage errors for missing descriptions and deadline values. */
    @Test
    public void parse_malformedDeadlineCommands_throwDeadlineUsageError() {
        String expected = "A deadline must have a description and a /by time!";

        assertParseErrors(expected,
                "deadline", "deadline submit report", "deadline /by 2Dec26",
                "deadline submit report /by   Friday",
                "deadline submit report /by Friday /by Saturday");
    }

    /** Verifies event usage errors for missing descriptions, starts, and ends. */
    @Test
    public void parse_malformedEventCommands_throwEventUsageError() {
        String expected = "An event must have a description, a /from time and a /to time!";

        assertParseErrors(expected,
                "event", "event workshop", "event /from 6am /to 7am",
                "event workshop /from /to 7am", "event workshop /from   6am /to 7am",
                "event workshop /from 6am /from 7am /to 8am",
                "event workshop /from 6am /to 7am /to 8am");
    }

    /** Verifies errors for missing and repeated within-period fields. */
    @Test
    public void parse_malformedWithinPeriodCommands_throwUsageError() {
        String usageMessage = "A within-period task must have a description, "
                + "a /from value and a /to value!";
        assertParseErrors(usageMessage,
                "within", "within collect certificate",
                "within /from 15Jan27 /to 25Jan27",
                "within collect certificate /from /to 25Jan27",
                "within collect certificate /from   15Jan27 /to 25Jan27",
                "within collect /from 15Jan27 /from 16Jan27 /to 25Jan27",
                "within collect /from 15Jan27 /to 25Jan27 /to 26Jan27");
    }

    /** Verifies specific errors for malformed list and list-on-date commands. */
    @Test
    public void parse_malformedListCommands_throwSpecificListErrors() {
        assertParseErrors("To view your task list, simply enter 'list'!",
                "list tomorrow");
        assertParseErrors("To list tasks on a date, enter 'list /on [date]'!",
                "list /on");
        assertParseErrors("I couldn't understand that date!",
                "list /on Monday", "list /on 31/02/26");
    }

    /** Verifies action-specific errors for malformed task mutation commands. */
    @Test
    public void parse_malformedFindCommands_throwFindUsageError() {
        String expected = "To find tasks, enter 'find [keyword]'!";

        assertParseErrors(expected, "find", "finder");
    }

    /** Verifies rejection of the delimiter reserved for saved task fields. */
    @Test
    public void parse_taskFieldsContainingStorageDelimiter_throwStorageSafetyError() {
        String expected = "Task details cannot contain ' | '!";

        assertParseErrors(expected,
                "todo read | book",
                "deadline submit report /by before | Friday",
                "event workshop /from Monday | morning /to Friday",
                "within collect /from Monday /to before | Friday");
    }

    @Test
    public void parse_malformedMutationCommands_throwActionSpecificErrors() {
        assertParseErrors("You have to mark a task number!",
                "mark", "mark abc", "mark 1 2");
        assertParseErrors("You have to unmark a task number!",
                "unmark", "unmark 1.5");
        assertParseErrors("You have to delete a task number!",
                "delete", "delete one");
    }

    /** Verifies that task numbers outside Java's integer range fail cleanly. */
    @Test
    public void parse_mutationCommandsWithIntegerOverflow_throwActionSpecificErrors() {
        assertParseErrors("You have to mark a task number!",
                "mark 2147483648", "mark -2147483649");
        assertParseErrors("You have to unmark a task number!",
                "unmark 999999999999999999999999");
        assertParseErrors("You have to delete a task number!",
                "delete -999999999999999999999999");
    }

    /**
     * Parses and executes an add command, then compares its persisted representation.
     *
     * @param input complete add command.
     * @param expectedData expected data representation of the parsed task.
     * @throws CrystalException if parsing or execution unexpectedly fails.
     */
    private void assertParsedTaskData(String input, String expectedData) throws CrystalException {
        Command command = Parser.parse(input);
        TaskList tasks = new TaskList();
        command.execute(tasks, new Ui(),
                new Storage(tempDirectory.resolve("parsed-task.txt")));

        assertEquals(1, tasks.getTaskCount());
        assertEquals(expectedData, tasks.getTask(0).toDataString());
    }

    /**
     * Verifies that each input fails with the same exact internal error message.
     *
     * @param expectedMessage expected exception message.
     * @param inputs malformed commands.
     */
    private static void assertParseErrors(String expectedMessage, String... inputs) {
        for (String input : inputs) {
            assertParseError(input, expectedMessage);
        }
    }

    /**
     * Verifies that parsing fails with Crystal's exact internal error message.
     *
     * @param input malformed command.
     * @param expectedMessage expected exception message.
     */
    private static void assertParseError(String input, String expectedMessage) {
        CrystalException exception = assertThrows(
                CrystalException.class, () -> Parser.parse(input), input);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
