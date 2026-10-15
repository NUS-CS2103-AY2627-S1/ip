package stephen.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import stephen.command.AddCommand;
import stephen.command.DeleteCommand;
import stephen.command.ExitCommand;
import stephen.command.FindCommand;
import stephen.command.ListCommand;
import stephen.command.MarkCommand;
import stephen.command.ScheduleCommand;
import stephen.command.SortCommand;
import stephen.command.UnmarkCommand;
import stephen.exception.ChatbotException;
import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.TaskList;
import stephen.task.Todo;

/** Tests command parsing and validation at normal and boundary inputs. */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies that each supported input produces its matching command type. */
    @Test
    void parseSupportedCommandsReturnsMatchingCommandTypes() throws ChatbotException {
        TaskList tasks = new TaskList(java.util.List.of(new Todo("one")));

        assertInstanceOf(ExitCommand.class, parser.parse("bye", tasks));
        assertInstanceOf(ListCommand.class, parser.parse("list", tasks));
        assertInstanceOf(SortCommand.class, parser.parse("sort", tasks));
        assertInstanceOf(ScheduleCommand.class, parser.parse("schedule 2024-02-29", tasks));
        assertInstanceOf(FindCommand.class, parser.parse("find read book", tasks));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1", tasks));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1", tasks));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1", tasks));
        assertInstanceOf(AddCommand.class, parser.parse("todo read", tasks));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline submit /by 2024-02-29", tasks));
        assertInstanceOf(AddCommand.class,
                parser.parse("event trip /from 2024-02-28 /to 2024-02-29", tasks));
        assertInstanceOf(AddCommand.class, parser.parse("  TODO\tread book  ", tasks));
    }

    /** Verifies that unknown commands and unexpected arguments are rejected. */
    @Test
    void parseUnknownOrExtraArgumentsThrowsHelpfulError() {
        TaskList tasks = new TaskList(java.util.List.of());

        assertMessage("I don't recognise that command.",
                assertThrows(ChatbotException.class, () -> parser.parse("unknown", tasks)));
        assertMessage("Please enter a command. Try: list",
                assertThrows(ChatbotException.class, () -> parser.parse("   ", tasks)));
        assertMessage("The 'list' command does not accept extra details.",
                assertThrows(ChatbotException.class, () -> parser.parse("list now", tasks)));
        assertMessage("The 'sort' command does not accept extra details.",
                assertThrows(ChatbotException.class, () -> parser.parse("sort now", tasks)));
        assertMessage("The 'bye' command does not accept extra details.",
                assertThrows(ChatbotException.class, () -> parser.parse("bye now", tasks)));
    }

    /** Verifies consistent command and argument splitting at spacing boundaries. */
    @Test
    void getCommandAndArgumentsSpacingAndEmptyInputSplitConsistently() {
        assertEquals("todo", parser.getCommand("todo   read book  "));
        assertEquals("read book", parser.getArguments("todo   read book  "));
        assertEquals("todo", parser.getCommand("  todo\tread book  "));
        assertEquals("read book", parser.getArguments("  todo\tread book  "));
        assertEquals("", parser.getCommand(""));
        assertEquals("", parser.getArguments("list"));
    }

    /** Verifies that todo descriptions are required and otherwise preserved. */
    @Test
    void parseTodoEmptyDescriptionRejectedNonEmptyDescriptionPreserved()
            throws ChatbotException {
        assertEquals("T | 0 | read book", parser.parseTodo("read book").toDataString());
        assertMessage("A todo needs a description. Try: todo borrow book",
                assertThrows(ChatbotException.class, () -> parser.parseTodo("")));
    }

    /** Verifies valid deadline dates and marker-like description text. */
    @Test
    void parseDeadlineValidLeapDayAndMarkerLikeTextParsesCorrectly()
            throws ChatbotException {
        Deadline leapDay = parser.parseDeadline("submit /by 2024-02-29");
        Deadline markerText = parser.parseDeadline("read /bypass notes /by 2024-03-01");

        assertEquals("D | 0 | submit | 2024-02-29", leapDay.toDataString());
        assertEquals("D | 0 | read /bypass notes | 2024-03-01", markerText.toDataString());
    }

    /** Verifies that incomplete deadline arguments and invalid dates are rejected. */
    @Test
    void parseDeadlineMissingPartsAndInvalidDatesRejected() {
        assertThrows(ChatbotException.class, () -> parser.parseDeadline(""));
        assertThrows(ChatbotException.class, () -> parser.parseDeadline("submit"));
        assertThrows(ChatbotException.class, () -> parser.parseDeadline("/by 2024-01-01"));
        assertThrows(ChatbotException.class, () -> parser.parseDeadline("submit /by"));
        assertThrows(
                ChatbotException.class, () -> parser.parseDeadline("submit /by 2023-02-29"));
        assertMessage("A deadline accepts only one '/by' date.",
                assertThrows(ChatbotException.class, () -> parser.parseDeadline(
                        "submit /by 2024-01-01 /by 2024-01-02")));
    }

    /** Verifies that event ranges include both endpoint dates. */
    @Test
    void parseEventValidRangeParsesBothInclusiveEndpoints() throws ChatbotException {
        Event event = parser.parseEvent("conference /from 2024-02-28 /to 2024-03-01");

        assertEquals("E | 0 | conference | 2024-02-28 | 2024-03-01",
                event.toDataString());
        assertEquals(true, event.occursOn(LocalDate.of(2024, 2, 28)));
        assertEquals(true, event.occursOn(LocalDate.of(2024, 3, 1)));
    }

    /** Verifies that incomplete event arguments and invalid dates are rejected. */
    @Test
    void parseEventMissingPartsAndInvalidDatesRejected() {
        assertThrows(ChatbotException.class, () -> parser.parseEvent(""));
        assertThrows(ChatbotException.class, () -> parser.parseEvent("meeting"));
        assertThrows(
                ChatbotException.class, () -> parser.parseEvent("meeting /from 2024-01-01"));
        assertThrows(
                ChatbotException.class, () -> parser.parseEvent("meeting /from /to 2024-01-02"));
        assertThrows(
                ChatbotException.class, () -> parser.parseEvent("meeting /from 2024-01-01 /to"));
        assertThrows(
                ChatbotException.class, () -> parser.parseEvent(
                        "meeting /from 2024-02-30 /to 2024-03-01"));
        assertMessage("An event needs '/from' before '/to'.",
                assertThrows(ChatbotException.class, () -> parser.parseEvent(
                        "meeting /to 2024-03-01 /from 2024-02-29")));
        assertMessage("An event accepts only one '/from' date.",
                assertThrows(ChatbotException.class, () -> parser.parseEvent(
                        "meeting /from 2024-02-28 /from 2024-02-29 /to 2024-03-01")));
        assertMessage("An event accepts only one '/to' date.",
                assertThrows(ChatbotException.class, () -> parser.parseEvent(
                        "meeting /from 2024-02-28 /to 2024-02-29 /to 2024-03-01")));
        assertMessage("An event's end date cannot be before its start date.",
                assertThrows(ChatbotException.class, () -> parser.parseEvent(
                        "meeting /from 2024-03-01 /to 2024-02-29")));
    }

    /** Verifies conversion and validation of one-based task numbers. */
    @Test
    void parseTaskIndexFirstAndLastValidMiddleBoundariesRejected() throws ChatbotException {
        assertEquals(0, parser.parseTaskIndex("1", "mark", 3));
        assertEquals(2, parser.parseTaskIndex("3", "mark", 3));
        assertThrows(ChatbotException.class, () -> parser.parseTaskIndex("", "mark", 3));
        assertThrows(ChatbotException.class, () -> parser.parseTaskIndex("one", "mark", 3));
        assertThrows(ChatbotException.class, () -> parser.parseTaskIndex("0", "mark", 3));
        assertThrows(ChatbotException.class, () -> parser.parseTaskIndex("4", "mark", 3));
        assertThrows(ChatbotException.class, () -> parser.parseTaskIndex("1", "mark", 0));
    }

    /** Verifies strict parsing of dates supplied to the schedule command. */
    @Test
    void parseScheduleDateLeapDayAcceptedInvalidAndEmptyRejected() throws ChatbotException {
        assertEquals(LocalDate.of(2024, 2, 29), parser.parseScheduleDate("2024-02-29"));
        assertThrows(ChatbotException.class, () -> parser.parseScheduleDate(""));
        assertThrows(ChatbotException.class, () -> parser.parseScheduleDate("2023-02-29"));
        assertThrows(ChatbotException.class, () -> parser.parseScheduleDate("29-02-2024"));
    }

    /** Verifies phrase preservation and missing-keyword validation for find. */
    @Test
    void parseFindKeywordMultiWordPhrasePreservedMissingKeywordRejected()
            throws ChatbotException {
        assertEquals("read book", parser.parseFindKeyword("read book"));
        assertMessage("Please provide a search keyword. Try: find book",
                assertThrows(ChatbotException.class, () -> parser.parseFindKeyword("")));
        assertMessage("Please provide a search keyword. Try: find book",
                assertThrows(
                        ChatbotException.class, () -> parser.parse(
                                "find", new TaskList(java.util.List.of()))));
    }

    /**
     * Checks an exact user-facing validation message.
     *
     * @param expected expected message
     * @param exception exception containing the actual message
     */
    private static void assertMessage(String expected, ChatbotException exception) {
        assertEquals(expected, exception.getMessage());
    }
}
