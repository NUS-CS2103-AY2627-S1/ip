package baymax.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import baymax.exception.BaymaxException;
import baymax.exception.EmptyByException;
import baymax.exception.EmptyDescriptionException;
import baymax.exception.EmptyFromException;
import baymax.exception.EmptyToException;
import baymax.exception.InvalidCommandException;

/**
 * Tests command parsing behavior that can be checked without running the
 * interactive application.
 */
public class ParserTest {

    @Test
    public void parseDate_unsupportedYears_rejectsAllDateFields() {
        for (String date : new String[]{"0000-01-01", "-0001-01-01", "+10000-01-01"}) {
            for (String command : new String[]{"deadline work /by " + date,
                "event work /from " + date + " /to 2026-01-01",
                "event work /from 2026-01-01 /to " + date
            }) {
                BaymaxException exception = assertThrows(BaymaxException.class, () -> {
                    if (command.startsWith("deadline")) {
                        Parser.parseDeadline(command);
                    } else {
                        Parser.parseEvent(command);
                    }
                });
                assertEquals(" Sorry, date years must be between 0001 and 9999.", exception.getMessage());
            }
        }
    }

    @Test
    public void parseDate_supportedBoundaryYears_preservesDates() {
        assertEquals(LocalDate.of(1, 1, 1), Parser.parseDeadline("deadline first /by 0001-01-01").dueDate());
        Parser.EventDetails event = Parser.parseEvent("event range /from 0001-01-01 /to 9999-12-31");
        assertEquals(LocalDate.of(1, 1, 1), event.startDate());
        assertEquals(LocalDate.of(9999, 12, 31), event.endDate());
    }

    /**
     * Verifies that each supported command text maps to the correct command type.
     */
    @Test
    public void getCommandType_supportedCommands_returnsMatchingCommandTypes() {
        assertEquals(Parser.CommandType.BYE, Parser.getCommandType("bye"));
        assertEquals(Parser.CommandType.LIST, Parser.getCommandType("list"));
        assertEquals(Parser.CommandType.MARK, Parser.getCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, Parser.getCommandType("unmark 1"));
        assertEquals(Parser.CommandType.DELETE, Parser.getCommandType("delete"));
        assertEquals(Parser.CommandType.DELETE, Parser.getCommandType("delete 1"));
        assertEquals(Parser.CommandType.FIND, Parser.getCommandType("find book"));
        assertEquals(Parser.CommandType.TODO, Parser.getCommandType("todo read book"));
        assertEquals(Parser.CommandType.DEADLINE, Parser.getCommandType(
                "deadline submit report /by 2019-12-02"));
        assertEquals(Parser.CommandType.EVENT, Parser.getCommandType(
                "event team meeting /from 2019-12-02 /to 2019-12-04"));
    }

    /**
     * Verifies that unknown command text is rejected.
     */
    @Test
    public void getCommandType_unknownCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.getCommandType("remind me"));
    }

    /**
     * Verifies that bare commands reach their specific missing-argument validation.
     */
    @Test
    public void getCommandType_bareTodo_returnsTodoType() {
        assertEquals(Parser.CommandType.TODO, Parser.getCommandType("todo"));
    }

    /**
     * Verifies that mark commands convert one-based task numbers to zero-based indexes.
     */
    @Test
    public void parseTaskIndex_markCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("mark 1", Parser.CommandType.MARK));
        assertEquals(11, Parser.parseTaskIndex("mark 12", Parser.CommandType.MARK));
    }

    /**
     * Verifies that unmark commands convert one-based task numbers to zero-based indexes.
     */
    @Test
    public void parseTaskIndex_unmarkCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("unmark 1", Parser.CommandType.UNMARK));
        assertEquals(11, Parser.parseTaskIndex("unmark 12", Parser.CommandType.UNMARK));
    }

    /**
     * Verifies that delete commands convert one-based task numbers to zero-based indexes.
     */
    @Test
    public void parseTaskIndex_deleteCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("delete 1", Parser.CommandType.DELETE));
        assertEquals(11, Parser.parseTaskIndex("delete 12", Parser.CommandType.DELETE));
    }

    /**
     * Verifies that extra whitespace around task numbers is ignored.
     */
    @Test
    public void parseTaskIndex_commandWithExtraSpaces_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("mark   1   ", Parser.CommandType.MARK));
        assertEquals(11, Parser.parseTaskIndex("unmark   12   ", Parser.CommandType.UNMARK));
        assertEquals(4, Parser.parseTaskIndex("delete   5   ", Parser.CommandType.DELETE));
    }

    /**
     * Verifies that a missing task number produces a helpful parser error.
     */
    @Test
    public void parseTaskIndex_missingTaskNumber_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseTaskIndex("delete", Parser.CommandType.DELETE));

        assertEquals(" Sorry, please provide a valid task number.",
                exception.getMessage());
    }

    /**
     * Verifies that non-numeric task numbers are rejected.
     */
    @Test
    public void parseTaskIndex_nonNumericTaskNumber_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseTaskIndex("mark abc", Parser.CommandType.MARK));

        assertEquals(" Sorry, please provide a valid task number.",
                exception.getMessage());
    }

    /**
     * Verifies that task-number parsing rejects command types without task indexes.
     */
    @Test
    public void parseTaskIndex_unsupportedCommandType_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.parseTaskIndex("todo read book", Parser.CommandType.TODO));
    }

    /**
     * Verifies that a valid todo command returns its description.
     */
    @Test
    public void parseTodoDescription_validCommand_returnsDescription() {
        assertEquals("read book", Parser.parseTodoDescription("todo read book"));
    }

    /**
     * Verifies that todo descriptions are trimmed.
     */
    @Test
    public void parseTodoDescription_extraSpaces_returnsTrimmedDescription() {
        assertEquals("read book", Parser.parseTodoDescription("todo   read book   "));
    }

    /**
     * Verifies that empty todo descriptions are rejected.
     */
    @Test
    public void parseTodoDescription_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseTodoDescription("todo"));
    }

    /**
     * Verifies that a valid deadline command returns its description and date.
     */
    @Test
    public void parseFindKeyword_validCommand_returnsKeyword() {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_extraSpaces_returnsTrimmedKeyword() {
        assertEquals("book", Parser.parseFindKeyword("find   book   "));
    }

    @Test
    public void parseFindKeyword_emptyKeyword_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseDeadline_validCommand_returnsDeadlineDetails() {
        Parser.DeadlineDetails details = Parser.parseDeadline(
                "deadline submit report /by 2019-12-02");

        assertEquals("submit report", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.dueDate());
    }

    /**
     * Verifies that deadline descriptions and dates are trimmed.
     */
    @Test
    public void parseDeadline_extraSpaces_returnsTrimmedDeadlineDetails() {
        Parser.DeadlineDetails details = Parser.parseDeadline(
                "deadline   submit report   /by   2019-12-02   ");

        assertEquals("submit report", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.dueDate());
    }

    /**
     * Verifies that deadline commands require the by marker.
     */
    @Test
    public void parseDeadline_missingByMarker_throwsEmptyByException() {
        assertThrows(EmptyByException.class, () ->
                Parser.parseDeadline("deadline submit report"));
    }

    /**
     * Verifies that deadline commands require a date after the by marker.
     */
    @Test
    public void parseDeadline_emptyByDate_throwsEmptyByException() {
        assertThrows(EmptyByException.class, () ->
                Parser.parseDeadline("deadline submit report /by"));
    }

    /**
     * Verifies that deadline commands require a description before the by marker.
     */
    @Test
    public void parseDeadline_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseDeadline("deadline /by 2019-12-02"));
    }

    /**
     * Verifies that deadline dates must use the supported date format.
     */
    @Test
    public void parseDeadline_invalidDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseDeadline("deadline submit report /by 02-12-2019"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    /**
     * Verifies that a valid event command returns its description and dates.
     */
    @Test
    public void parseEvent_validCommand_returnsEventDetails() {
        Parser.EventDetails details = Parser.parseEvent(
                "event team meeting /from 2019-12-02 /to 2019-12-04");

        assertEquals("team meeting", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.startDate());
        assertEquals(LocalDate.of(2019, 12, 4), details.endDate());
    }

    /**
     * Verifies that event descriptions and dates are trimmed.
     */
    @Test
    public void parseEvent_extraSpaces_returnsTrimmedEventDetails() {
        Parser.EventDetails details = Parser.parseEvent(
                "event   team meeting   /from   2019-12-02   /to   2019-12-04   ");

        assertEquals("team meeting", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.startDate());
        assertEquals(LocalDate.of(2019, 12, 4), details.endDate());
    }

    /**
     * Verifies the current error shown when an event command omits the from marker.
     */
    @Test
    public void parseEvent_missingFromMarker_throwsEmptyFromException() {
        assertThrows(EmptyFromException.class, () ->
                Parser.parseEvent("event team meeting /to 2019-12-04"));
    }

    /**
     * Verifies that event commands require a description before the from marker.
     */
    @Test
    public void parseEvent_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseEvent("event /from 2019-12-02 /to 2019-12-04"));
    }

    /**
     * Verifies that event commands require a start date.
     */
    @Test
    public void parseEvent_emptyFromDate_throwsEmptyFromException() {
        assertThrows(EmptyFromException.class, () ->
                Parser.parseEvent("event team meeting /from /to 2019-12-04"));
    }

    /**
     * Verifies the current error shown when an event command omits the to marker.
     */
    @Test
    public void parseEvent_missingToMarker_throwsEmptyToException() {
        assertThrows(EmptyToException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02"));
    }

    /**
     * Verifies that event commands require an end date.
     */
    @Test
    public void parseEvent_emptyToDate_throwsEmptyToException() {
        assertThrows(EmptyToException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02 /to"));
    }

    /**
     * Verifies that event start dates must use the supported date format.
     */
    @Test
    public void parseEvent_invalidFromDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseEvent("event team meeting /from 02-12-2019 /to 2019-12-04"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    /**
     * Verifies that event end dates must use the supported date format.
     */
    @Test
    public void parseEvent_invalidToDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02 /to 04-12-2019"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_invalidNumbers_throwsBaymaxException() {
        for (String number : new String[]{"", "0", "-1", "-2147483648", "2147483648", "+1", "1 2", "1.5"}) {
            assertThrows(BaymaxException.class, () ->
                    Parser.parseTaskIndex("mark " + number, Parser.CommandType.MARK), number);
        }
    }

    @Test
    public void parseDeadline_invalidCalendarDates_throwsBaymaxException() {
        for (String date : new String[]{"2025-02-29", "2024-02-30", "2024-04-31", "2024-13-01"}) {
            assertThrows(BaymaxException.class, () -> Parser.parseDeadline("deadline work /by " + date));
        }
        assertEquals(LocalDate.of(2024, 2, 29),
                Parser.parseDeadline("deadline work /by 2024-02-29").dueDate());
    }

    @Test
    public void parseEvent_invalidRange_throwsBaymaxException() {
        for (String end : new String[]{"2024-01-01", "2024-01-02"}) {
            BaymaxException exception = assertThrows(BaymaxException.class, () ->
                    Parser.parseEvent("event work /from 2024-01-02 /to " + end));
            assertEquals(" Sorry, an event must end after its start date.", exception.getMessage());
        }
    }

    @Test
    public void parseDeadline_malformedMarkers_throwsBaymaxException() {
        for (String details : new String[]{
            "work /by 2024-01-01 /by 2024-01-02", "work /bye 2024-01-01",
            "work/by 2024-01-01", "work /by2024-01-01", "work /from 2024-01-01"
        }) {
            assertThrows(BaymaxException.class, () -> Parser.parseDeadline("deadline " + details));
        }
    }

    @Test
    public void parseEvent_repeatedOrReversedMarkers_throwsBaymaxException() {
        for (String details : new String[]{
            "work /from 2024-01-01 /from 2024-01-02 /to 2024-01-03",
            "work /from 2024-01-01 /to 2024-01-03 /to 2024-01-04",
            "work /to 2024-01-03 /from 2024-01-01"
        }) {
            assertThrows(BaymaxException.class, () -> Parser.parseEvent("event " + details));
        }
    }
}
