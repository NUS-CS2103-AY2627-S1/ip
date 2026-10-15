package squirtlebot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import squirtlebot.command.AddDeadlineCommand;
import squirtlebot.command.AddToDoCommand;
import squirtlebot.command.MarkCommand;
import squirtlebot.exception.CommandException;

/**
 * Tests command, description, and token parsing performed by {@link Parser}.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseCommand_validCommands_returnsMatchingCommand() {
        assertEquals(new AddToDoCommand(new String[]{"todo", "read", "book"}),
                parser.parseCommand("todo read book"));
        assertEquals(new AddDeadlineCommand(new String[]{"deadline", "submit", "report", "/by", "01-10-2026"}),
                parser.parseCommand("deadline submit report /by 01-10-2026"));
        assertEquals(new MarkCommand(new String[]{"mark", "2"}), parser.parseCommand("mark 2"));
    }

    @Test
    public void parseCommand_unknownCommand_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> parser.parseCommand("remind read book"));

        assertEquals("Invalid command", exception.getMessage());
    }

    @Test
    public void parseCommand_nullInput_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> parser.parseCommand(null));

        assertEquals("Please enter a command :(", exception.getMessage());
    }

    @Test
    public void parseCommand_emptyInput_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> parser.parseCommand(""));

        assertEquals("Please enter a command :(", exception.getMessage());
    }

    @Test
    public void parseCommand_irregularWhitespace_returnsMatchingCommand() {
        assertEquals(new MarkCommand(new String[]{"mark", "2"}),
                parser.parseCommand("  mark\t  2  "));
    }

    @Test
    public void parseDescription_inputWithParameters_returnsTextBeforeFirstParameter() {
        String[] inputTokens = "deadline submit project report /by 13-09-2026".split(" ");

        assertEquals("submit project report", parser.parseDescription(inputTokens));
    }

    @Test
    public void parseTokens_multipleParameters_returnsOnlyExpectedTokenValue() {
        String[] inputTokens = "event workshop /from 13-09-2026 10:00:00 /to 13-09-2026 12:00:00".split(" ");

        assertEquals("13-09-2026 10:00:00", parser.parseTokens(inputTokens, "/from"));
        assertEquals("13-09-2026 12:00:00", parser.parseTokens(inputTokens, "/to"));
    }

    @Test
    public void parseTokens_missingExpectedToken_returnsEmptyString() {
        String[] inputTokens = "deadline submit report /by 13-09-2026".split(" ");

        assertEquals("", parser.parseTokens(inputTokens, "/from"));
    }
}
