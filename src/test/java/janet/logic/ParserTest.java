package janet.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import janet.exception.InvalidCommandException;
import janet.exception.InvalidTaskException;

class ParserTest {
    @Test
    void parse_allValidCommandTypes_returnsExpectedCommandClasses() {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(AddCommand.class, Parser.parse("event meeting /from 14:00 /to 16:00"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1 3"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    void parse_surroundingAndRepeatedWhitespace_parsesCommand() {
        assertInstanceOf(ListCommand.class, Parser.parse("  list  "));
        assertInstanceOf(AddCommand.class, Parser.parse("todo    read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo\tread book"));
    }

    @Test
    void parse_blankUnknownOrUnexpectedArgument_throwsSpecificException() {
        InvalidCommandException blankException = assertThrows(
                InvalidCommandException.class, () -> Parser.parse("   "));
        InvalidCommandException unknownException = assertThrows(
                InvalidCommandException.class, () -> Parser.parse("dance"));
        InvalidCommandException listException = assertThrows(
                InvalidCommandException.class, () -> Parser.parse("list extra"));
        InvalidCommandException byeException = assertThrows(
                InvalidCommandException.class, () -> Parser.parse("bye now"));

        assertEquals("Please enter a command.", blankException.getMessage());
        assertEquals("OOPS!!! I don't recognize that command.", unknownException.getMessage());
        assertEquals("Sorry, list does not take any arguments.", listException.getMessage());
        assertEquals("Sorry, bye does not take any arguments.", byeException.getMessage());
    }

    @Test
    void parse_taskWithoutRequiredDetails_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> Parser.parse("todo"));
        assertThrows(InvalidCommandException.class, () -> Parser.parse("deadline return book"));
        assertThrows(InvalidCommandException.class, () -> Parser.parse("deadline return book /by"));
        assertThrows(InvalidCommandException.class, () -> Parser.parse("event meeting /from 14:00"));
        assertThrows(InvalidCommandException.class, () -> Parser.parse("event meeting /from /to 16:00"));
        assertThrows(InvalidCommandException.class, () -> Parser.parse("find"));
    }

    @Test
    void parse_invalidDeadlineDates_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("deadline return book /by 02-12-2019"));
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("deadline return book /by 2019-02-29"));
    }

    @Test
    void parse_validEventDateTimeFormats_returnsAddCommand() {
        assertInstanceOf(AddCommand.class, Parser.parse("event day /from 2099-01-01 /to 2099-01-02"));
        assertInstanceOf(AddCommand.class, Parser.parse("event time /from 09:30 /to 10:00"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event appointment /from 2099-01-01 09:30 /to 2099-01-01 10:00"));
        assertInstanceOf(AddCommand.class, Parser.parse("event instant /from 10:00 /to 10:00"));
    }

    @Test
    void parse_invalidEventDateTime_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event invalid date /from 2026-02-30 /to 2026-03-01"));
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event invalid time /from 10:99 /to 12:00"));
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event free form /from Monday 2pm /to 4pm"));
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event mixed /from 2099-01-01 /to 10:00"));
    }

    @Test
    void parse_eventEndBeforeStart_throwsInvalidCommandException() {
        InvalidCommandException timeException = assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event reverse time /from 18:00 /to 17:00"));
        InvalidCommandException dateException = assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event reverse date /from 2099-02-02 /to 2099-02-01"));
        InvalidCommandException dateTimeException = assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event reverse date-time /from 2099-02-02 12:00 /to 2099-02-02 11:59"));

        assertEquals("Sorry, an event's end must be equal to or later than its start.",
                timeException.getMessage());
        assertEquals(timeException.getMessage(), dateException.getMessage());
        assertEquals(timeException.getMessage(), dateTimeException.getMessage());
    }

    @Test
    void parse_invalidTaskNumbers_throwsInvalidTaskException() {
        assertThrows(InvalidTaskException.class, () -> Parser.parse("mark"));
        assertThrows(InvalidTaskException.class, () -> Parser.parse("mark one"));
        assertThrows(InvalidTaskException.class, () -> Parser.parse("unmark 1.5"));
        assertThrows(InvalidTaskException.class, () -> Parser.parse("delete"));
        assertThrows(InvalidTaskException.class, () -> Parser.parse("delete 1 two"));
    }

    @Test
    void parse_taskTextContainingControlCharacter_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> Parser.parse("todo read\tbook"));
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("event meeting /from 14:00\tsharp /to 16:00"));
        assertThrows(InvalidCommandException.class, () ->
                Parser.parse("deadline return\tbook /by 2019-12-02"));
    }
}
