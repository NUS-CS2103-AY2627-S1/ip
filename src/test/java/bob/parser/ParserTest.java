package bob.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.command.AddCommand;
import bob.command.ByeCommand;
import bob.command.DeleteCommand;
import bob.command.FindCommand;
import bob.command.ListCommand;
import bob.command.MarkCommand;
import bob.command.SearchCommand;
import bob.command.UnmarkCommand;
import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.TaskList;

/** Tests conversion of user input into executable commands. */
class ParserTest {
    private final Parser parser = new Parser();

    @TempDir
    private java.nio.file.Path tempDirectory;

    @Test
    void parse_commandsWithoutArguments_returnsMatchingCommand() {
        assertAll(
                () -> assertInstanceOf(ByeCommand.class, parser.parse("bye")),
                () -> assertInstanceOf(ListCommand.class, parser.parse("list")));
    }

    @Test
    void parse_taskMutationCommandsWithPositiveIndex_returnsMatchingCommand() {
        assertAll(
                () -> assertInstanceOf(MarkCommand.class, parser.parse("mark 1")),
                () -> assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 2")),
                () -> assertInstanceOf(DeleteCommand.class, parser.parse("delete 3")));
    }

    @Test
    void parse_findWithKeyword_returnsFindCommand() throws BobException {
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    void parse_searchWithQuery_returnsSearchCommand() throws BobException {
        assertInstanceOf(SearchCommand.class, parser.parse("search boo read"));
    }

    @Test
    void parse_addCommandsWithValidDetails_returnsAddCommand() {
        assertAll(
                () -> assertInstanceOf(AddCommand.class, parser.parse("todo read a book")),
                () -> assertInstanceOf(AddCommand.class,
                        parser.parse("deadline submit report /by 2/12/2019 1800")),
                () -> assertInstanceOf(AddCommand.class,
                        parser.parse("event project meeting /from 2/12/2019 1800 /to 2/12/2019 1900")));
    }

    @Test
    void parse_addCommands_preservesDescriptionsAndDateTimes() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        String todoResponse = parser.parse("todo read a book").execute(tasks, storage);
        String deadlineResponse = parser.parse("deadline submit report /by 2/12/2019 1800")
                .execute(tasks, storage);
        String eventResponse = parser.parse("event project meeting /from 2/12/2019 1800 /to 3/12/2019")
                .execute(tasks, storage);

        assertEquals("read a book", tasks.get(0).getDescription());
        assertEquals("submit report", tasks.get(1).getDescription());
        assertEquals("project meeting", tasks.get(2).getDescription());
        assertTrue(deadlineResponse.contains("Dec 02 2019 18:00"));
        assertTrue(eventResponse.contains("Dec 03 2019"));
        assertTrue(todoResponse.contains("[T][ ] read a book"));
    }

    @Test
    void parse_mixedCaseAndSurroundingWhitespace_returnsMatchingCommand() throws BobException {
        assertInstanceOf(AddCommand.class, parser.parse("  ToDo read a book  "));
    }

    @Test
    void parse_nullOrBlankInput_bobExceptionThrown() {
        assertAll(
                () -> assertThrows(BobException.class, () -> parser.parse(null)),
                () -> assertThrows(BobException.class, () -> parser.parse("")),
                () -> assertThrows(BobException.class, () -> parser.parse("   ")));
    }

    @Test
    void parse_unknownCommand_bobExceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse("lookup book"));
    }

    @Test
    void parse_findWithoutKeyword_bobExceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse("find"));
    }

    @Test
    void parse_searchWithoutQuery_bobExceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse("search"));
    }

    @Test
    void parse_commandThatForbidsArgumentsWithArgument_bobExceptionThrown() {
        assertAll(
                () -> assertThrows(BobException.class, () -> parser.parse("bye now")),
                () -> assertThrows(BobException.class, () -> parser.parse("list all")));
    }

    @Test
    void parse_taskMutationCommandWithInvalidIndex_bobExceptionThrown() {
        assertAll(
                () -> assertThrows(BobException.class, () -> parser.parse("mark")),
                () -> assertThrows(BobException.class, () -> parser.parse("unmark zero")),
                () -> assertThrows(BobException.class, () -> parser.parse("delete 0")),
                () -> assertThrows(BobException.class, () -> parser.parse("delete -1")));
    }

    @Test
    void parse_taskMutationCommandWithNonWholeNumber_bobExceptionThrown() {
        BobException exception = assertThrows(BobException.class, () -> parser.parse("mark 1.5"));

        assertEquals("'mark' needs a whole-number task index, such as 1.", exception.getMessage());
    }

    @Test
    void parse_taskMutationCommandWithOverflowingIndex_bobExceptionThrown() {
        BobException exception = assertThrows(BobException.class,
                () -> parser.parse("delete 999999999999999999999"));

        assertEquals("'delete' needs a whole-number task index, such as 1.", exception.getMessage());
    }

    @Test
    void parse_todoWithInvalidDescription_bobExceptionThrown() {
        assertAll(
                () -> assertThrows(BobException.class, () -> parser.parse("todo")),
                () -> assertThrows(BobException.class, () -> parser.parse("todo buy milk | bread")));
    }

    @Test
    void parse_deadlineWithMissingOrInvalidDetails_exceptionThrown() {
        assertAll(
                () -> assertThrows(BobException.class, () -> parser.parse("deadline /by 2/12/2019")),
                () -> assertThrows(BobException.class,
                        () -> parser.parse("deadline submit report /by invalid date")),
                () -> assertThrows(BobException.class,
                        () -> parser.parse("deadline submit | report /by 2/12/2019")));
    }

    @Test
    void parse_deadlineWithoutDate_exceptionSuggestsSupportedDateFormat() {
        BobException exception = assertThrows(BobException.class,
                () -> parser.parse("deadline submit report"));

        assertEquals(
                "A deadline needs a due date. Try: deadline submit report /by 2/12/2019 1800",
                exception.getMessage());
    }

    @Test
    void parse_eventWithMissingOrInvalidDetails_exceptionThrown() {
        assertAll(
                () -> assertThrows(BobException.class,
                        () -> parser.parse("event /from 2/12/2019 1800 /to 2/12/2019 1900")),
                () -> assertThrows(BobException.class,
                        () -> parser.parse("event project meeting /from invalid /to 2/12/2019 1900")),
                () -> assertThrows(BobException.class,
                        () -> parser.parse(
                                "event project | meeting /from 2/12/2019 1800 /to 2/12/2019 1900")));
    }

    @Test
    void parse_eventWithInvalidEndDate_bobExceptionThrown() {
        BobException exception = assertThrows(BobException.class,
                () -> parser.parse("event project meeting /from 2/12/2019 1800 /to invalid"));

        assertEquals("Use d/M/yyyy or d/M/yyyy HHmm, such as 2/12/2019 or 2/12/2019 1800.",
                exception.getMessage());
    }

    @Test
    void parse_eventWithoutDateRange_exceptionSuggestsSupportedDateFormat() {
        BobException exception = assertThrows(BobException.class,
                () -> parser.parse("event project meeting"));

        assertEquals(
                "An event needs a time range. Try: "
                        + "event meeting /from 2/12/2019 1800 /to 2/12/2019 1900",
                exception.getMessage());
    }

    @Test
    void parse_eventWithoutEndDate_exceptionSuggestsSupportedDateFormat() {
        BobException exception = assertThrows(BobException.class,
                () -> parser.parse("event project meeting /from 2/12/2019 1800"));

        assertEquals(
                "An event needs an end time after '/to'. Try: "
                        + "event meeting /from 2/12/2019 1800 /to 2/12/2019 1900",
                exception.getMessage());
    }
    @Test
    void parse_invalidEventRange_bobExceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse(
                "event meeting /from 2/12/2019 1900 /to 2/12/2019 1800"));
    }

    @Test
    void parse_repeatedDateParameter_bobExceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse(
                "event meeting /from 2/12/2019 1800 /from 2/12/2019 1900 /to 2/12/2019 2000"));
    }

    @Test
    void parse_impossibleCalendarDate_bobExceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse("deadline submit report /by 30/2/2024"));
    }
}
