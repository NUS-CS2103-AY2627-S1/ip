package choo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import choo.exception.ChooException;

/**
 * Checks conversion of raw user input into structured commands.
 */
public class ParserTest {
    @Test
    void parse_simpleCommands_returnsMatchingTypes() throws ChooException {
        assertEquals(CommandType.BYE, Parser.parse("  bye  ").getType());
        assertEquals(CommandType.LIST, Parser.parse("list").getType());
        assertEquals(CommandType.SORT, Parser.parse(" sort ").getType());
    }

    @Test
    void parse_taskNumberCommand_returnsOneBasedPosition() throws ChooException {
        ParsedCommand markCommand = Parser.parse("mark 12");
        assertEquals(CommandType.MARK, markCommand.getType());
        assertEquals(12, markCommand.getTaskNumber());
        assertEquals(CommandType.UNMARK, Parser.parse("unmark 2").getType());
        assertEquals(CommandType.DELETE, Parser.parse("delete 3").getType());
    }

    @Test
    void parse_todoCommand_returnsTodoTask() throws ChooException {
        ParsedCommand todoCommand = Parser.parse("todo read book");
        assertEquals(CommandType.ADD, todoCommand.getType());
        assertEquals("[T][ ] read book", todoCommand.getTask().toString());
    }

    @Test
    void parse_deadlineCommand_returnsFormattedDeadline() throws ChooException {
        ParsedCommand deadlineCommand = Parser.parse("deadline submit /by 2019-12-02 1800");
        assertEquals("[D][ ] submit (by: Dec 2 2019, 6:00PM)",
                deadlineCommand.getTask().toString());

        ParsedCommand delimiterLikeTextCommand = Parser.parse(
                "deadline review /bypass route /by 2019-12-02");
        assertEquals("[D][ ] review /bypass route (by: Dec 2 2019)",
                delimiterLikeTextCommand.getTask().toString());
    }

    @Test
    void parse_eventCommand_returnsEventWithRange() throws ChooException {
        ParsedCommand eventCommand = Parser.parse("event meeting /from Mon /to Tue");
        assertEquals("[E][ ] meeting (from: Mon to: Tue)", eventCommand.getTask().toString());
    }

    @Test
    void parse_findCommand_returnsKeyword() throws ChooException {
        ParsedCommand findCommand = Parser.parse("  find project book  ");

        assertEquals(CommandType.FIND, findCommand.getType());
        assertEquals("project book", findCommand.getKeyword());
    }

    @Test
    void parse_invalidCommands_throwsSpecificErrors() {
        assertError(null, "A command cannot be empty.");
        assertError("", "A command cannot be empty.");
        assertError("   ", "A command cannot be empty.");
        assertError("list now", "The list command does not accept extra details.");
        assertError("sort later", "The sort command does not accept extra details.");
        assertError("bye please", "The bye command does not accept extra details.");
        assertError("mark two", "Enter a whole-number task position after mark.");
        assertError("todo", "A todo needs a description.");
        assertError("deadline", "A deadline needs a description.");
        assertError("deadline report", "A deadline needs a /by date or time.");
        assertError("deadline /by 2019-12-02", "A deadline needs a description.");
        assertError("deadline report /by", "A deadline needs a /by date or time.");
        assertError("deadline report /by 2019-12-02 /by 2019-12-03",
                "A deadline needs exactly one /by value.");
        assertError("deadline report /by Friday",
                "Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.");
        assertError("event meeting /from Mon", "An event needs both /from and /to values.");
        assertError("event", "An event needs a description.");
        assertError("event meeting /from /to Tue", "An event needs both /from and /to values.");
        assertError("event meeting /from Mon /to", "An event needs both /from and /to values.");
        assertError("event meeting /from Mon /from Tue /to Wed",
                "An event needs exactly one /from followed by exactly one /to value.");
        assertError("event meeting /from Mon /to Tue /to Wed",
                "An event needs exactly one /from followed by exactly one /to value.");
        assertError("event meeting /to Tue /from Mon",
                "An event needs exactly one /from followed by exactly one /to value.");
        assertError("find", "A find command needs a keyword.");
        assertError("mystery", "I don't recognize that command.");
    }

    private static void assertError(String input, String expectedMessage) {
        ChooException exception = assertThrows(ChooException.class, () -> Parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
