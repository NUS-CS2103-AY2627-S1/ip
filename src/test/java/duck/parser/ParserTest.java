package duck.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duck.DuckException;
import duck.command.AddCommand;
import duck.command.DeleteCommand;
import duck.command.ExitCommand;
import duck.command.FindCommand;
import duck.command.HelpCommand;
import duck.command.ListCommand;
import duck.command.MarkCommand;
import duck.command.UnmarkCommand;

/**
 * Tests command recognition and argument validation performed by {@link Parser}.
 */
public class ParserTest {
    private static final String TASK_NUMBER_ERROR =
            "Please enter a valid task number. Use list to check the available task numbers.";

    private static final String TODO_DESCRIPTION_ERROR =
            "The description of a todo cannot be empty. Try: todo read a book.";

    private static final String COMMAND_EXAMPLE_DEADLINE =
            " Try: deadline submit report /by 2026-10-15.";

    private static final String COMMAND_EXAMPLE_EVENT =
            " Try: event meeting /from 2026-10-15 1400 /to 2026-10-15 1500.";

    private final Parser parser = new Parser();

    @Test
    public void parse_commandsWithoutArguments_returnsMatchingCommandTypes() throws DuckException {
        assertInstanceOf(ExitCommand.class, this.parser.parse("bye"));
        assertInstanceOf(ListCommand.class, this.parser.parse("list"));
        assertInstanceOf(HelpCommand.class, this.parser.parse("help"));
    }

    @Test
    public void parse_taskNumberCommandsWithInteger_returnsMatchingCommandTypes() throws DuckException {
        assertInstanceOf(MarkCommand.class, this.parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, this.parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, this.parser.parse("delete 3"));
    }

    @Test
    public void parse_validAddCommands_returnsAddCommands() throws DuckException {
        assertInstanceOf(AddCommand.class, this.parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline return book /by 2026-08-30"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline submit report /by Mon"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline buy groceries /by fRi"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event meeting /from Monday 2pm /to 4pm"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event meeting /from 2026-10-15 1400 /to 2026-10-15 1500"));
    }

    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws DuckException {
        assertInstanceOf(FindCommand.class, this.parser.parse("find book"));
        assertInstanceOf(FindCommand.class, this.parser.parse("find project meeting"));
    }

    @Test
    public void parse_findWithoutKeyword_throwsDescriptionError() {
        assertParseError("find", "The keyword for a find command cannot be empty.");
    }

    @Test
    public void parse_unknownCommandsAndPrefixCollisions_throwsUnknownCommandError() {
        String expectedMessage =
                "I didn't understand that command. Try help to see what I can do.";

        assertParseError("", expectedMessage);
        assertParseError("unknown", expectedMessage);
        assertParseError("todoish read book", expectedMessage);
        assertParseError("marking 1", expectedMessage);
        assertParseError("finder book", expectedMessage);
        assertParseError("bye later", expectedMessage);
        assertParseError("list now", expectedMessage);
    }

    @Test
    public void parse_missingOrInvalidTaskNumbers_throwsTaskNumberError() {
        assertParseError("mark", TASK_NUMBER_ERROR);
        assertParseError("unmark abc", TASK_NUMBER_ERROR);
        assertParseError("delete", TASK_NUMBER_ERROR);
        assertParseError("delete 1 2", TASK_NUMBER_ERROR);
        assertParseError("delete 2147483648", TASK_NUMBER_ERROR);
    }

    @Test
    public void parse_emptyTodoDescription_throwsDescriptionError() {
        assertParseError("todo", TODO_DESCRIPTION_ERROR);
    }

    @Test
    public void parse_invalidDeadlineSyntax_throwsRelevantError() {
        String descriptionError = "The description of a deadline cannot be empty."
                + COMMAND_EXAMPLE_DEADLINE;
        String dateError = "The deadline command needs a non-empty /by date."
                + COMMAND_EXAMPLE_DEADLINE;

        assertParseError("deadline", descriptionError);
        assertParseError("deadline /by 2026-08-30",
                descriptionError);
        assertParseError("deadline return book", dateError);
        assertParseError("deadline return book /by", dateError);
    }

    @Test
    public void parse_invalidDeadlineDates_throwsDateFormatError() {
        String expectedMessage = "Please enter a valid deadline date in yyyy-MM-dd format or as "
                + "Mon, Tue, Wed, Thu, Fri, Sat, or Sun.";

        assertParseError("deadline wrong format /by 30-08-2026", expectedMessage);
        assertParseError("deadline impossible /by 2026-02-30", expectedMessage);
        assertParseError("deadline abbreviated /by 2026-8-3", expectedMessage);
        assertParseError("deadline year zero /by 0000-01-01", expectedMessage);
        assertParseError("deadline unsupported time /by 2026-08-30 1800", expectedMessage);
        assertParseError("deadline full weekday /by Monday", expectedMessage);
        assertParseError("deadline informal weekday /by Tues", expectedMessage);
        assertParseError("deadline relative phrase /by next Mon", expectedMessage);
    }

    @Test
    public void parse_validDeadlineBoundaryDates_returnsAddCommands() throws DuckException {
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline leap day /by 2024-02-29"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline earliest /by 0001-01-01"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline latest /by 9999-12-31"));
    }

    @Test
    public void parse_invalidEventSyntax_throwsRelevantError() {
        assertParseError("event", "The description of an event cannot be empty.");
        assertParseError("event /from Monday /to Tuesday",
                "The description of an event cannot be empty.");
        assertParseError("event meeting", "The event command needs a /from and /to time.");
        assertParseError("event meeting /from Monday",
                "The event command needs a /from and /to time.");
        assertParseError("event meeting /from  /to 4pm",
                "The event command needs a non-empty /from and /to time.");
        assertParseError("event meeting /from Monday /to",
                "The event command needs a /from and /to time.");
    }

    @Test
    public void parse_structuredEventEndBeforeStart_throwsRangeError() {
        String expectedMessage = "The event end cannot be earlier than its start."
                + COMMAND_EXAMPLE_EVENT;

        assertParseError(
                "event meeting /from 2026-10-15 1500 /to 2026-10-15 1400",
                expectedMessage);
        assertParseError(
                "event trip /from 2026-10-16 0900 /to 2026-10-15 1700",
                expectedMessage);
    }

    @Test
    public void parse_shorthandEventEndBeforeStart_throwsRangeError() {
        String expectedMessage = "The event end cannot be earlier than its start."
                + COMMAND_EXAMPLE_EVENT;

        assertParseError("event meeting /from Mon 5pm /to 4pm", expectedMessage);
        assertParseError("event meeting /from Fri 10:30PM /to Fri 9:45pm", expectedMessage);
    }

    @Test
    public void parse_structuredEventBoundaryTimes_returnsAddCommands() throws DuckException {
        assertInstanceOf(AddCommand.class, this.parser.parse(
                "event instant /from 2026-10-15 1400 /to 2026-10-15 1400"));
        assertInstanceOf(AddCommand.class, this.parser.parse(
                "event overnight /from 2026-10-15 2300 /to 2026-10-16 0100"));
        assertInstanceOf(AddCommand.class, this.parser.parse(
                "event leap day /from 2024-02-29 2300 /to 2024-03-01 0000"));
        assertInstanceOf(AddCommand.class, this.parser.parse(
                "event earliest /from 0001-01-01 0000 /to 0001-01-01 0001"));
        assertInstanceOf(AddCommand.class, this.parser.parse(
                "event latest /from 9999-12-31 2358 /to 9999-12-31 2359"));
    }

    @Test
    public void parse_invalidStructuredEventTimes_throwsDateTimeFormatError() {
        String expectedMessage = "Please enter valid event times in yyyy-MM-dd HHmm format."
                + COMMAND_EXAMPLE_EVENT;

        assertParseError(
                "event meeting /from 2026-02-30 1400 /to 2026-02-30 1500",
                expectedMessage);
        assertParseError(
                "event meeting /from 2026-10-15 2460 /to 2026-10-15 2500",
                expectedMessage);
        assertParseError(
                "event meeting /from 0000-10-15 1400 /to 0000-10-15 1500",
                expectedMessage);
    }

    @Test
    public void parse_freeFormEventTimes_remainSupported() throws DuckException {
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event meeting /from Monday 2pm /to 4pm"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event workshop /from afternoon /to evening"));
    }

    @Test
    public void parse_validShorthandEventTimes_returnsAddCommands() throws DuckException {
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event meeting /from Mon 2pm /to 4pm"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event meeting /from Fri 9:45AM /to Fri 10:30am"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event instant /from Sun 12pm /to 12pm"));
    }

    @Test
    public void parse_invalidShorthandEventTimes_throwsTimeFormatError() {
        String expectedMessage = "Please enter valid event times such as Mon 2pm and 4pm."
                + COMMAND_EXAMPLE_EVENT;

        assertParseError("event meeting /from Mon 13pm /to 4pm", expectedMessage);
        assertParseError("event meeting /from Tue 9:60am /to 10am", expectedMessage);
    }

    @Test
    public void parse_mixedStructuredAndFreeFormEventTimes_remainSupported() throws DuckException {
        assertInstanceOf(AddCommand.class, this.parser.parse(
                "event planning /from 2026-10-15 1400 /to after lunch"));
        assertInstanceOf(AddCommand.class, this.parser.parse(
                "event review /from Monday morning /to 2026-10-15 1500"));
    }

    /** Verifies that parsing fails with the exact user-facing error message. */
    private void assertParseError(String input, String expectedMessage) {
        DuckException exception = assertThrows(DuckException.class, () -> this.parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
