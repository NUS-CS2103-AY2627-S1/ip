package dukey.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import dukey.command.CommandWord;
import dukey.command.DeadlineCommand;
import dukey.command.DeleteCommand;
import dukey.command.EventCommand;
import dukey.command.ExitCommand;
import dukey.command.FindCommand;
import dukey.command.ListCommand;
import dukey.command.MarkCommand;
import dukey.command.OnCommand;
import dukey.command.StatsCommand;
import dukey.command.TodoCommand;
import dukey.command.UnmarkCommand;
import dukey.exception.DukeyException;

public class ParserTest {

    @Test
    public void parseCommand_exactCommand_returnsCommandWord() {
        Parser parser = new Parser();

        assertEquals(CommandWord.LIST, parser.parseCommand("list"));
    }

    @Test
    public void parseCommand_commandWithArguments_returnsCommandWord() {
        Parser parser = new Parser();

        assertEquals(CommandWord.TODO, parser.parseCommand("todo read book"));
    }

    @Test
    public void parseCommand_commandWithLeadingAndTrailingSpaces_returnsCommandWord() {
        Parser parser = new Parser();

        assertEquals(CommandWord.LIST, parser.parseCommand("  list  "));
    }

    @Test
    public void parseCommand_commandPrefixOnly_returnsUnknown() {
        Parser parser = new Parser();

        assertEquals(CommandWord.UNKNOWN, parser.parseCommand("listening"));
    }

    @Test
    public void parse_knownCommands_returnsCorrectCommandType() throws DukeyException {
        Parser parser = new Parser();

        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(OnCommand.class, parser.parse("on 2099-12-06"));
        assertInstanceOf(TodoCommand.class, parser.parse("todo read book"));
        assertInstanceOf(DeadlineCommand.class, parser.parse("deadline return book /by 2099-12-06 1800"));
        assertInstanceOf(EventCommand.class,
                parser.parse("event meeting /from 2099-12-06 1400 /to 2099-12-06 1600"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(StatsCommand.class, parser.parse("stats"));
    }

    @Test
    public void parse_commandWithLeadingAndTrailingSpaces_returnsCommand() throws DukeyException {
        Parser parser = new Parser();

        assertInstanceOf(ListCommand.class, parser.parse("  list  "));
    }

    @Test
    public void parse_unknownCommand_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> parser.parse("blah"));

        assertEquals("I'm sorry, but I don't know what that means :-(", exception.getMessage());
    }

    @Test
    public void parse_invalidTaskNumber_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> parser.parse("mark two"));

        assertEquals("Please provide a valid task number.", exception.getMessage());
    }

    @Test
    public void parse_invalidOnDate_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> parser.parse("on not-a-date"));

        assertEquals("Please use date format: yyyy-MM-dd", exception.getMessage());
    }

    @Test
    public void parseOnDate_validDate_returnsLocalDate() throws DukeyException {
        Parser parser = new Parser();

        LocalDate date = parser.parseOnDate("on 2099-12-06");

        assertEquals(LocalDate.of(2099, 12, 6), date);
    }

    @Test
    public void parseTaskNumber_emptyTaskNumber_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseTaskNumber("delete", CommandWord.DELETE, "Please provide a task number to delete.");
        });

        assertEquals("Please provide a task number to delete.", exception.getMessage());
    }

    @Test
    public void parseTodoDescription_validDescription_returnsDescription() throws DukeyException {
        Parser parser = new Parser();

        assertEquals("read book", parser.parseTodoDescription("todo read book"));
    }

    @Test
    public void parseTodoDescription_emptyDescription_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseTodoDescription("todo");
        });

        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws DukeyException {
        Parser parser = new Parser();

        assertEquals("book", parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_emptyKeyword_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseFindKeyword("find");
        });

        assertEquals("Please provide a keyword to find.", exception.getMessage());
    }

    @Test
    public void parseStats_extraArguments_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseStats("stats today");
        });

        assertEquals("The stats command does not take any arguments.", exception.getMessage());
    }

    @Test
    public void parseDeadline_validDeadline_returnsDescriptionAndDateTime() throws DukeyException {
        Parser parser = new Parser();

        String[] parts = parser.parseDeadline("deadline return book /by 2099-12-06 1800");

        assertArrayEquals(new String[] {"return book", "2099-12-06 1800"}, parts);
    }

    @Test
    public void parseDeadline_keywordInsideDescription_ignoresNonKeywordToken() throws DukeyException {
        Parser parser = new Parser();

        String[] parts = parser.parseDeadline("deadline read /bylaw notes /by 2099-12-06 1800");

        assertArrayEquals(new String[] {"read /bylaw notes", "2099-12-06 1800"}, parts);
    }

    @Test
    public void parseDeadline_missingByKeyword_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseDeadline("deadline return book");
        });

        assertEquals("Please use: deadline {DESCRIPTION} /by {WHEN}", exception.getMessage());
    }

    @Test
    public void parseDeadline_emptyByValue_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseDeadline("deadline return book /by");
        });

        assertEquals("Please provide a deadline task date/time after /by.", exception.getMessage());
    }

    @Test
    public void parseDeadline_duplicateByKeyword_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseDeadline("deadline return book /by 2099-12-06 1800 /by 2099-12-07 1800");
        });

        assertEquals("Please use: deadline {DESCRIPTION} /by {WHEN}", exception.getMessage());
    }

    @Test
    public void parseEvent_validEvent_returnsDescriptionFromAndTo() throws DukeyException {
        Parser parser = new Parser();

        String[] parts = parser.parseEvent("event meeting /from 2099-12-06 1400 /to 2099-12-06 1600");

        assertArrayEquals(new String[] {"meeting", "2099-12-06 1400", "2099-12-06 1600"}, parts);
    }

    @Test
    public void parseEvent_keywordsInsideDescription_ignoresNonKeywordTokens() throws DukeyException {
        Parser parser = new Parser();

        String[] parts = parser.parseEvent("event discuss /fromage and /together /from 2099-12-06 1400 "
                + "/to 2099-12-06 1600");

        assertArrayEquals(new String[] {"discuss /fromage and /together", "2099-12-06 1400", "2099-12-06 1600"},
                parts);
    }

    @Test
    public void parseEvent_missingToKeyword_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseEvent("event meeting /from 2099-12-06 1400");
        });

        assertEquals("Please use: event {DESCRIPTION} /from {WHEN} /to {WHEN}", exception.getMessage());
    }

    @Test
    public void parseEvent_emptyFromValue_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseEvent("event meeting /from /to 2099-12-06 1600");
        });

        assertEquals("Please provide a event task date/time after /from.", exception.getMessage());
    }

    @Test
    public void parseEvent_duplicateKeyword_throwsDukeyException() {
        Parser parser = new Parser();

        DukeyException exception = assertThrows(DukeyException.class, () -> {
            parser.parseEvent("event meeting /from 2099-12-06 1400 /from 2099-12-06 1500 "
                    + "/to 2099-12-06 1600");
        });

        assertEquals("Please use: event {DESCRIPTION} /from {WHEN} /to {WHEN}", exception.getMessage());
    }
}
