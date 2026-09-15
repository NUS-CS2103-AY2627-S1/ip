package luigibot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import luigibot.command.AddCommand;
import luigibot.command.DeleteCommand;
import luigibot.command.ExitCommand;
import luigibot.command.FindDateCommand;
import luigibot.command.FindKeywordCommand;
import luigibot.command.ListCommand;
import luigibot.command.MarkCommand;
import luigibot.command.UnmarkCommand;

/**
 * Tests command recognition and validation in {@link Parser}.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_validCommands_correctCommandTypesReturned() {
        assertInstanceOf(AddCommand.class, this.parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline return book /by 2026-08-27 1800"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event meeting /from 2026-08-27 1400 /to 2026-08-27 1600"));
        assertInstanceOf(MarkCommand.class, this.parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, this.parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, this.parser.parse("delete 1"));
        assertInstanceOf(ListCommand.class, this.parser.parse("list"));
        assertInstanceOf(FindDateCommand.class, this.parser.parse("on 2026-08-27"));
        assertInstanceOf(FindKeywordCommand.class, this.parser.parse("find book"));
        assertInstanceOf(ExitCommand.class, this.parser.parse("bye"));
    }

    @Test
    public void parse_commandWithOuterWhitespace_correctCommandTypeReturned() {
        assertInstanceOf(ListCommand.class, this.parser.parse("  list  "));
        assertInstanceOf(AddCommand.class, this.parser.parse("  todo read book  "));
    }

    @Test
    public void parse_blankCommand_emptyCommandErrorThrown() {
        assertParseError("   ", "Mamma mia! You didn't-a enter a command.");
    }

    @Test
    public void parse_unknownOrExtraArguments_unknownCommandErrorThrown() {
        assertParseError("dance", "Oh no! Luigi doesn't-a recognize that command.");
        assertParseError("list now", "Oh no! Luigi doesn't-a recognize that command.");
        assertParseError("bye now", "Oh no! Luigi doesn't-a recognize that command.");
    }

    @Test
    public void parse_invalidTaskNumbers_wholeNumberErrorThrown() {
        String expectedMessage = "Mamma mia! Please-a enter a whole task number.";

        assertParseError("mark one", expectedMessage);
        assertParseError("unmark 1.5", expectedMessage);
        assertParseError("delete two", expectedMessage);
    }

    @Test
    public void parse_deleteWithoutNumber_taskNotFoundErrorThrown() {
        assertParseError("delete", "Oh no! Luigi can't-a find that task number.");
    }

    @Test
    public void parse_invalidFindDates_dateErrorThrown() {
        assertParseError("on", "Mamma mia! Luigi needs-a date. Use: on yyyy-MM-dd.");
        assertParseError("on 2026-02-30", "Mamma mia! Use-a yyyy-MM-dd for the date.");
        assertParseError("on tomorrow", "Mamma mia! Use-a yyyy-MM-dd for the date.");
    }

    @Test
    public void parse_findKeyword_keywordCommandReturned() {
        assertInstanceOf(FindKeywordCommand.class, this.parser.parse("find BOOK"));
    }

    @Test
    public void parse_findWithoutKeyword_findDetailsErrorThrown() {
        assertParseError("find", "Mamma mia! Luigi needs-a a keyword to find.");
        assertParseError("find   ", "Mamma mia! Luigi needs-a a keyword to find.");
    }

    @Test
    public void parse_todoWithoutDescription_emptyDescriptionErrorThrown() {
        assertParseError("todo", "Mamma mia! The task description can't-a be empty.");
    }

    @Test
    public void parse_descriptionContainingPipe_invalidDescriptionErrorThrown() {
        String expectedMessage = "Mamma mia! Task descriptions can't-a contain the | symbol.";

        assertParseError("todo read | book", expectedMessage);
        assertParseError("deadline return | book /by 2026-08-27 1800", expectedMessage);
        assertParseError("event team | meeting /from 2026-08-27 1400 /to 2026-08-27 1600",
                expectedMessage);
    }

    @Test
    public void parse_deadlineWithoutValidMarker_deadlineDetailsErrorThrown() {
        String expectedMessage = "Oh no! Luigi needs-a know the deadline! Use /by.";

        assertParseError("deadline return book", expectedMessage);
        assertParseError("deadline return/by 2026-08-27 1800", expectedMessage);
        assertParseError("deadline return book /by", expectedMessage);
    }

    @Test
    public void parse_deadlineWithoutDescription_emptyDescriptionErrorThrown() {
        assertParseError("deadline /by 2026-08-27 1800",
                "Mamma mia! The task description can't-a be empty.");
    }

    @Test
    public void parse_deadlineWithInvalidDateTime_deadlineDateErrorThrown() {
        String expectedMessage =
                "Mamma mia! Use-a yyyy-MM-dd HHmm for the deadline date and time.";

        assertParseError("deadline return book /by 2026-02-30 1800", expectedMessage);
        assertParseError("deadline return book /by 2026-08-27 2500", expectedMessage);
    }

    @Test
    public void parse_eventWithoutValidMarkers_eventDetailsErrorThrown() {
        String expectedMessage = "Mamma mia! Use: event DESCRIPTION /from START /to END.";

        assertParseError("event meeting", expectedMessage);
        assertParseError("event meeting /from 2026-08-27 1400", expectedMessage);
        assertParseError("event meeting /to 2026-08-27 1600 /from 2026-08-27 1400",
                expectedMessage);
        assertParseError("event meeting /from /to 2026-08-27 1600", expectedMessage);
    }

    @Test
    public void parse_eventWithoutDescription_emptyDescriptionErrorThrown() {
        assertParseError("event /from 2026-08-27 1400 /to 2026-08-27 1600",
                "Mamma mia! The task description can't-a be empty.");
    }

    @Test
    public void parse_eventWithInvalidDateTime_eventDateErrorThrown() {
        String expectedMessage = "Mamma mia! Use-a yyyy-MM-dd HHmm for both Event times.";

        assertParseError("event meeting /from 2026-02-30 1400 /to 2026-08-27 1600",
                expectedMessage);
        assertParseError("event meeting /from 2026-08-27 1400 /to 2026-08-27 2500",
                expectedMessage);
    }

    @Test
    public void parse_eventDoesNotEndAfterStart_eventRangeErrorThrown() {
        String expectedMessage = "Mamma mia! The Event must-a end after it starts.";

        assertParseError("event meeting /from 2026-08-27 1400 /to 2026-08-27 1400",
                expectedMessage);
        assertParseError("event meeting /from 2026-08-27 1600 /to 2026-08-27 1400",
                expectedMessage);
    }

    private void assertParseError(String input, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                this.parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
