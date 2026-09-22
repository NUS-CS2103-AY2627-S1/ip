package longfrog.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import longfrog.command.AddCommand;
import longfrog.command.DateCommand;
import longfrog.command.DeleteCommand;
import longfrog.command.ExitCommand;
import longfrog.command.FindCommand;
import longfrog.command.ListCommand;
import longfrog.command.MarkCommand;
import longfrog.command.UnmarkCommand;
import longfrog.exception.LongfrogException;
import longfrog.task.TaskList;

class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser(new TaskList());
    }

    @Test
    void parse_validCommands_returnsExpectedCommandTypes() throws LongfrogException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline submit report /by 2/12/2019 1800"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event team meeting /from 2/12/2019 1400 /to 2/12/2019 1600"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(DateCommand.class, parser.parse("date 2/12/2019"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    void parse_commandKeywordWithDifferentCase_returnsExpectedCommand() throws LongfrogException {
        assertInstanceOf(AddCommand.class, parser.parse("TODO Read Book"));
    }

    @Test
    void parse_inputWithSurroundingWhitespace_returnsExpectedCommand() throws LongfrogException {
        assertInstanceOf(ListCommand.class, parser.parse("  list  "));
    }

    @Test
    void parse_commandWithUnexpectedArguments_throwsExceptionWithUsage() {
        assertParseError("list extra", "Syntax error. Expected: list");
        assertParseError("bye later", "Syntax error. Expected: bye");
        assertParseError("mark 1 extra", "Syntax error. Expected: mark INDEX");
        assertParseError("unmark 1 extra", "Syntax error. Expected: unmark INDEX");
        assertParseError("delete 1 extra", "Syntax error. Expected: delete INDEX");
    }

    @Test
    void parse_blankInput_throwsExceptionWithHelpfulMessage() {
        assertParseError("   ", "Input buffer is empty. Please enter a command, ribbit.");
    }

    @Test
    void parse_nullInput_violatesParserInvariant() {
        assertThrows(AssertionError.class, () -> parser.parse(null));
    }

    @Test
    void parse_unknownCommand_throwsExceptionWithHelpfulMessage() {
        assertParseError("dance", "Unknown command token. My parser cannot compute that, ribbit.");
    }

    @Test
    void parse_todoWithoutTask_throwsExceptionWithUsage() {
        assertParseError("todo", "Syntax error. Expected: todo TASK");
    }

    @Test
    void parse_taskDescriptionContainingStorageDelimiter_throwsException() {
        String expectedMessage = "Task descriptions cannot contain “|” because it is reserved for storage.";

        assertParseError("todo compare A | B", expectedMessage);
        assertParseError("deadline compare A | B /by 2/12/2019 1800", expectedMessage);
        assertParseError("event compare A | B /from 2/12/2019 1400 /to 2/12/2019 1600", expectedMessage);
    }

    @Test
    void parse_deadlineWithMissingOrInvalidDate_throwsExceptionWithHelpfulMessage() {
        assertParseError("deadline /by 2/12/2019 1800",
                "Syntax error. Expected: deadline TASK /by d/M/yyyy HHmm");
        assertParseError("deadline submit report", "Syntax error. Expected: deadline TASK /by d/M/yyyy HHmm");
        assertParseError("deadline submit report /by tomorrow",
                "Temporal parsing failed. Expected: d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
        assertParseError("deadline submit report /by 31/2/2025 1800",
                "Temporal parsing failed. Expected: d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
    }

    @Test
    void parse_eventWithMissingOrInvalidTimes_throwsExceptionWithHelpfulMessage() {
        assertParseError("event /from 2/12/2019 1400 /to 2/12/2019 1600",
                "Syntax error. Expected: event TASK /from d/M/yyyy HHmm /to d/M/yyyy HHmm");
        assertParseError("event meeting /from 2/12/2019 1400",
                "Syntax error. Expected: event TASK /from d/M/yyyy HHmm /to d/M/yyyy HHmm");
        assertParseError("event meeting /from tomorrow /to 2/12/2019 1600",
                "Temporal parsing failed. Expected: d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
        assertParseError("event meeting /from 2/12/2019 1800 /to 2/12/2019 1400",
                "This event must end after it begins. Check the /from and /to times.");
        assertParseError("event meeting /from 2/12/2019 1800 /to 2/12/2019 1800",
                "This event must end after it begins. Check the /from and /to times.");
    }

    @Test
    void parse_dateWithMissingOrInvalidDate_throwsExceptionWithHelpfulMessage() {
        assertParseError("date", "Syntax error. Expected: date d/M/yyyy (e.g., date 2/12/2019)");
        assertParseError("date tomorrow", "Temporal parsing failed. Expected: d/M/yyyy (e.g., 2/12/2019)");
        assertParseError("date 29/2/2025", "Temporal parsing failed. Expected: d/M/yyyy (e.g., 2/12/2019)");
    }

    @Test
    void parse_findWithoutKeyword_throwsExceptionWithUsage() {
        assertParseError("find", "Syntax error. Expected: find KEYWORD");
    }

    @Test
    void parse_taskCommandsWithInvalidIndex_throwsExceptionWithHelpfulMessage() {
        assertParseError("mark", "Index argument missing. Try: mark 1");
        assertParseError("unmark 0", "Index underflow: task numbers start at 1.");
        assertParseError("delete -1", "Index underflow: task numbers start at 1.");
        assertParseError("mark first", "Type mismatch: task number must be an integer.");
        assertParseError("mark 999999999999999999999", "Type mismatch: task number must be an integer.");
    }

    private void assertParseError(String input, String expectedMessage) {
        LongfrogException exception = assertThrows(LongfrogException.class, () -> parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
