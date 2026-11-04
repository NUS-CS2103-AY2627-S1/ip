package soar.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import soar.command.AddCommand;
import soar.command.Command;
import soar.command.DateCommand;
import soar.command.DeleteCommand;
import soar.command.EditCommand;
import soar.command.ExitCommand;
import soar.command.FindCommand;
import soar.command.ListCommand;
import soar.command.MarkCommand;
import soar.command.UnmarkCommand;
import soar.exception.EmptyDescriptionException;
import soar.exception.InvalidTaskFormatException;
import soar.exception.InvalidTaskNumberException;
import soar.exception.SoarException;
import soar.exception.UnknownCommandException;

/**
 * Tests conversion of complete user input lines into validated commands.
 */
public class ParserTest {
    /** Verifies that every supported command shape creates the right command type. */
    @Test
    public void parse_supportedCommands_returnsMatchingCommandTypes() {
        Map<String, Class<? extends Command>> commands = Map.ofEntries(
                Map.entry("bye", ExitCommand.class),
                Map.entry("list", ListCommand.class),
                Map.entry("find book", FindCommand.class),
                Map.entry("date 2019-10-15", DateCommand.class),
                Map.entry("todo read book", AddCommand.class),
                Map.entry("deadline submit report /by 2019-10-15", AddCommand.class),
                Map.entry("event meeting /from 2pm /to 3pm", AddCommand.class),
                Map.entry("mark 1", MarkCommand.class),
                Map.entry("unmark 1", UnmarkCommand.class),
                Map.entry("delete 1", DeleteCommand.class),
                Map.entry("edit 1 /description revised /to 5pm", EditCommand.class));

        assertAll(commands.entrySet().stream()
                .map(entry -> () -> assertInstanceOf(
                        entry.getValue(),
                        Parser.parse(entry.getKey()),
                        "Unexpected command type for: " + entry.getKey())));
    }

    /** Verifies that command lookalikes, wrong casing, and forbidden arguments are rejected. */
    @Test
    public void parse_unrecognizedCommandShapes_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> Parser.parse(""));
        assertThrows(UnknownCommandException.class, () -> Parser.parse("BYE"));
        assertThrows(UnknownCommandException.class, () -> Parser.parse("bye now"));
        assertThrows(UnknownCommandException.class, () -> Parser.parse("list extra"));
        assertThrows(UnknownCommandException.class, () -> Parser.parse("todoist task"));
        assertThrows(UnknownCommandException.class, () -> Parser.parse("mark1"));
        assertThrows(UnknownCommandException.class, () -> Parser.parse(null));
    }

    /** Verifies that harmless whitespace does not prevent a valid command from running. */
    @Test
    public void parse_commandWithSurroundingWhitespace_returnsMatchingCommand() throws SoarException {
        assertInstanceOf(ListCommand.class, Parser.parse("  list  "));
    }

    /** Verifies task descriptions and scheduling fields before an add command is created. */
    @Test
    public void parse_invalidTaskDetails_throwsSpecificException() {
        assertMessageContains(EmptyDescriptionException.class, "todo", "todo");
        assertMessageContains(InvalidTaskFormatException.class, "deadline report", "'/by'");
        assertMessageContains(EmptyDescriptionException.class, "deadline /by 2019-10-15", "deadline");
        assertMessageContains(InvalidTaskFormatException.class, "deadline report /by", "empty");
        assertMessageContains(InvalidTaskFormatException.class,
                "deadline report /by 2019-02-29", "2019-02-29");
        assertMessageContains(InvalidTaskFormatException.class, "event meeting /from 2pm", "'/to'");
        assertMessageContains(EmptyDescriptionException.class, "event /from 2pm /to 3pm", "event");
        assertMessageContains(InvalidTaskFormatException.class,
                "event meeting /from /to 3pm", "incomplete");
        assertMessageContains(InvalidTaskFormatException.class,
                "event backwards /from 2026-09-20 17:00 /to 2026-09-20 16:00", "ends before");
    }

    /** Verifies missing, fractional, and overflowing task numbers are rejected during parsing. */
    @Test
    public void parse_invalidTaskNumbers_throwsInvalidTaskNumberException() {
        assertMessageContains(InvalidTaskNumberException.class, "mark", "after 'mark'");
        assertMessageContains(InvalidTaskNumberException.class, "unmark 1.5", "whole");
        assertMessageContains(InvalidTaskNumberException.class, "delete two", "whole");
        assertMessageContains(InvalidTaskNumberException.class,
                "mark 999999999999999999999", "whole");
    }

    /** Verifies edit commands reject missing, unknown, duplicate, and empty fields. */
    @Test
    public void parse_invalidEditFields_throwsSpecificException() {
        assertMessageContains(InvalidTaskNumberException.class, "edit", "after 'edit'");
        assertMessageContains(InvalidTaskFormatException.class, "edit 1", "at least one");
        assertMessageContains(InvalidTaskFormatException.class,
                "edit 1 description revised", "at least one");
        assertMessageContains(InvalidTaskFormatException.class,
                "edit 1 /when tomorrow", "don't recognize");
        assertMessageContains(InvalidTaskFormatException.class,
                "edit 1 /to 4pm /to 5pm", "more than once");
        assertMessageContains(InvalidTaskFormatException.class,
                "edit 1 /description /to 5pm", "'/description' value is empty");
    }

    /** Verifies date queries reject missing and impossible dates with useful guidance. */
    @Test
    public void parse_invalidDateQueries_throwsInvalidTaskFormatException() {
        assertMessageContains(InvalidTaskFormatException.class, "date", "after 'date'");
        assertMessageContains(InvalidTaskFormatException.class, "date 2019-02-29", "2019-02-29");
    }

    /** Verifies that a find command requires non-empty search text. */
    @Test
    public void parse_findWithoutKeyword_throwsInvalidTaskFormatException() {
        assertMessageContains(InvalidTaskFormatException.class, "find", "after 'find'");
    }

    /** Parses input and verifies both the exception type and a useful part of its message. */
    private <T extends SoarException> void assertMessageContains(
            Class<T> exceptionType, String input, String expectedMessagePart) {
        T exception = assertThrows(exceptionType, () -> Parser.parse(input));
        assertTrue(exception.getMessage().contains(expectedMessagePart));
    }
}
