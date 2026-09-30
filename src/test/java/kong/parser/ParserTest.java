package kong.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import kong.command.DeadlineCommand;
import kong.command.DeleteCommand;
import kong.command.EventCommand;
import kong.command.ExitCommand;
import kong.command.FindCommand;
import kong.command.ListCommand;
import kong.command.MarkCommand;
import kong.command.OnDateCommand;
import kong.command.TodoCommand;
import kong.command.UnknownCommand;
import kong.command.UnmarkCommand;
import kong.exception.KongException;

class ParserTest {
    @Test
    void parseReturnsCorrespondingCommandTypeForValidCommands() throws KongException {
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(EventCommand.class, Parser.parse("event conference /from 2019-10-14 /to 2019-10-16"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(OnDateCommand.class, Parser.parse("on 2019-10-15"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(UnknownCommand.class, Parser.parse("something else"));
    }

    @Test
    void parseReturnsCorrespondingCommandTypeForDifferentCasing() throws KongException {
        assertInstanceOf(TodoCommand.class, Parser.parse("ToDo read book"));
        assertInstanceOf(ExitCommand.class, Parser.parse("BYE"));
    }

    @Test
    void parseThrowsHelpfulExceptionForBlankInput() {
        KongException exception = assertThrows(KongException.class, () -> Parser.parse("   "));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    void parse_nullInput_throwsHelpfulException() {
        KongException exception = assertThrows(KongException.class, () -> Parser.parse(null));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    void parseThrowsHelpfulExceptionForMissingRequiredArguments() {
        assertParseError("todo", "Invalid command. A todo command needs to be in the following format: "
                + "todo <description>");
        assertParseError("deadline return book", "Invalid command. A deadline command needs to be in the following "
                + "format: deadline <description> /by <date>");
        assertParseError("event conference /from 2019-10-14", "Invalid command. An event command needs to be in the "
                + "following format: event <description> /from <date> /to <date>");
        assertParseError("on", "Invalid command. An on command needs to be in the following format: on <date>");
        assertParseError("find",
                "Invalid command. A find command needs to be in the following format: find <keyword>");
    }

    @Test
    void parseThrowsHelpfulExceptionForInvalidDates() {
        String expectedMessage = "Invalid date. Please use the format yyyy-MM-dd, for example 2019-10-15.";

        assertParseError("deadline return book /by tomorrow", expectedMessage);
        assertParseError("event conference /from tomorrow /to 2019-10-16", expectedMessage);
        assertParseError("on tomorrow", expectedMessage);
    }

    @Test
    void parseThrowsHelpfulExceptionForNonExistentDates() {
        assertParseError("deadline return book /by 2019-02-30",
                "Invalid date. The date '2019-02-30' does not exist on the calendar.");
        assertParseError("deadline return book /by 2019-02-29",
                "Invalid date. The date '2019-02-29' does not exist on the calendar.");
        assertParseError("event party /from 2019-04-31 /to 2019-05-01",
                "Invalid date. The date '2019-04-31' does not exist on the calendar.");
    }

    @Test
    void parseThrowsHelpfulExceptionForDuplicateParameters() {
        assertParseError("deadline return book /by 2019-10-15 /by 2019-10-16",
                "Invalid command. The /by parameter cannot be specified multiple times.");
        assertParseError("event party /from 2019-10-15 /to 2019-10-16 /from 2019-10-17",
                "Invalid command. The /from parameter cannot be specified multiple times.");
        assertParseError("event party /from 2019-10-15 /to 2019-10-16 /to 2019-10-17",
                "Invalid command. The /to parameter cannot be specified multiple times.");
    }

    @Test
    void parseThrowsHelpfulExceptionForEventStartDateAfterEndDate() {
        assertParseError("event party /from 2019-10-16 /to 2019-10-15",
                "Invalid command. The event start date cannot be after the end date.");
    }

    @Test
    void parseThrowsHelpfulExceptionForSpecialCharactersInDescription() {
        assertParseError("todo read | book", "Task description cannot contain the '|' character.");
        assertParseError("deadline return | book /by 2019-10-15",
                "Task description cannot contain the '|' character.");
        assertParseError("event party | dance /from 2019-10-15 /to 2019-10-16",
                "Task description cannot contain the '|' character.");
        assertParseError("todo read\nbook", "Task description cannot contain newline characters.");
    }

    @Test
    void parseThrowsHelpfulExceptionForExtraneousArguments() {
        assertParseError("list extra", "Invalid command. The list command does not take any arguments.");
        assertParseError("bye now", "Invalid command. The bye command does not take any arguments.");
    }

    @Test
    void parseAcceptsEventParametersInBothOrders() throws KongException {
        assertInstanceOf(EventCommand.class, Parser.parse("event party /to 2019-10-16 /from 2019-10-15"));
    }

    @Test
    void parseRejectsCarriageReturnInDescription() {
        assertParseError("todo read\rbook", "Task description cannot contain newline characters.");
    }

    @Test
    void parseHandlesExcessWhitespace() throws KongException {
        assertInstanceOf(TodoCommand.class, Parser.parse("   todo    read   book   "));
    }

    @Test
    void parseRejectsEmptyFlagValues() {
        assertParseError("deadline return book /by   ",
                "Invalid command. A deadline command needs to be in the following format: "
                        + "deadline <description> /by <date>");
        assertParseError("event party /from   /to 2019-10-16",
                "Invalid command. An event command needs to be in the following format: "
                        + "event <description> /from <date> /to <date>");
    }

    @Test
    void constructorInstantiatesParser() {
        Parser parser = new Parser();
        assertEquals(Parser.class, parser.getClass());
    }

    private static void assertParseError(String input, String expectedMessage) {
        KongException exception = assertThrows(KongException.class, () -> Parser.parse(input));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
