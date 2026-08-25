package matthew.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import matthew.command.AddCommand;
import matthew.command.DeleteCommand;
import matthew.command.ExitCommand;
import matthew.command.FindCommand;
import matthew.command.ListCommand;
import matthew.command.MarkCommand;
import matthew.command.OnCommand;
import matthew.command.UnmarkCommand;
import matthew.exception.MatthewException;

/** Tests recognised commands and validation performed by the parser. */
class ParserTest {
    @Test
    void parseCreatesTheRightCommands() throws MatthewException {
        assertInstanceOf(ExitCommand.class, Parser.parse(" bye "));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline submit /by 2026-08-22 1200"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2026-08-22 1200 /to 2026-08-22 1300"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2026-08-22"));
    }

    @Test
    void parseRejectsMalformedCommandsWithHelpfulErrors() {
        assertError("", "I don't recognise an empty command.");
        assertError("unknown", "I don't recognise that command.");
        assertError("bye now", "The 'bye' command does not take extra arguments.");
        assertError("list all", "The 'list' command does not take extra arguments.");
        assertError("find", "Please provide a keyword to search for, e.g. find book.");
        assertError("mark", "Please tell me which task to mark, e.g. mark 2.");
        assertError("delete two", "The task number must be a number.");
        assertError("todo", "A todo needs a description.");
        assertError("deadline work",
                "A deadline must include '/by'. Example: deadline return book /by 2019-12-02 1800");
        assertError("event meeting /from 2026-08-22 1200 /to 2026-08-22 1100",
                "The event end time cannot be before its start time.");
        assertError("on tomorrow", "Date must use yyyy-MM-dd, e.g. 2019-12-02.");
    }

    private void assertError(String input, String expectedMessage) {
        MatthewException exception = assertThrows(MatthewException.class, () -> Parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
