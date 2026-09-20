package fifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import fifi.command.AddDeadlineCommand;
import fifi.command.AddEventCommand;
import fifi.command.AddTodoCommand;
import fifi.command.Command;
import fifi.command.DeleteCommand;
import fifi.command.ExitCommand;
import fifi.command.FindCommand;
import fifi.command.ListCommand;
import fifi.command.MarkCommand;
import fifi.command.ShowCommand;
import fifi.command.StatisticsCommand;
import fifi.command.UnmarkCommand;
import fifi.exception.InvalidCommandException;
import fifi.exception.InvalidDescriptionException;

/**
 * Tests the conversion of raw user input into commands and dates.
 */
public class ParserTest {

    // ---------- parse: valid commands ----------

    @Test
    public void parse_byeCommand_exitCommandReturned() throws Exception {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_listCommand_listCommandReturned() throws Exception {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_markCommand_markCommandReturned() throws Exception {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmarkCommand_unmarkCommandReturned() throws Exception {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
    }

    @Test
    public void parse_deleteCommand_deleteCommandReturned() throws Exception {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 3"));
    }

    @Test
    public void parse_taskNumberCommandsWithInvalidNumbers_helpfulExceptionAndRecovery() throws Exception {
        String[] commands = {"mark", "unmark", "delete"};
        String[] invalidNumbers = {"", "   ", "three", "1 read book", "1.5", "2147483648", "-2147483649",
            "0", "-1", "-2147483648", "+1", "١"};
        for (String command : commands) {
            for (String invalidNumber : invalidNumbers) {
                Class<? extends Command> expectedType = Parser.parse(command + " 1").getClass();
                InvalidDescriptionException exception = assertThrows(
                        InvalidDescriptionException.class, () -> Parser.parse(command + " " + invalidNumber));
                assertEquals("Oops! Please enter a task number after " + command + ", e.g. " + command + " 1.",
                        exception.getMessage());
                assertInstanceOf(expectedType, Parser.parse(command + "   1"));
            }
            assertThrows(InvalidDescriptionException.class, () -> Parser.parse(command));
        }
    }

    @Test
    public void parse_todoWithDescription_addTodoCommandReturned() throws Exception {
        assertInstanceOf(AddTodoCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddTodoCommand.class, Parser.parse("todo todo deadline event"));
    }

    @Test
    public void parse_deadlineWithDescriptionAndDate_addDeadlineCommandReturned() throws Exception {
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline return book /by 2025-10-15"));
        assertInstanceOf(AddDeadlineCommand.class,
                Parser.parse("deadline deadline event planning /by 2025-10-15"));
    }

    @Test
    public void parse_eventWithDescriptionAndDates_addEventCommandReturned() throws Exception {
        assertInstanceOf(AddEventCommand.class,
                Parser.parse("event career fair /from 2025-10-01 /to 2025-10-03"));
        assertInstanceOf(AddEventCommand.class,
                Parser.parse("event event deadline review /from 2025-10-01 /to 2025-10-03"));
    }

    @Test
    public void parse_showWithDate_showCommandReturned() throws Exception {
        assertInstanceOf(ShowCommand.class, Parser.parse("show 2025-10-15"));
    }

    @Test
    public void parse_findWithKeyword_findCommandReturned() throws Exception {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_statsCommands_statisticsCommandReturned() throws Exception {
        assertInstanceOf(StatisticsCommand.class, Parser.parse("stats"));
        assertInstanceOf(StatisticsCommand.class, Parser.parse("stats all"));
        assertInstanceOf(StatisticsCommand.class, Parser.parse("stats since 2026-09-02"));
    }

    @Test
    public void parse_statsWithInvalidArguments_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("stats week"));
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("stats since"));
        assertThrows(DateTimeException.class, () -> Parser.parse("stats since 02-09-2026"));
    }

    // ---------- parse: unrecognised commands ----------

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(InvalidCommandException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(InvalidCommandException.class, () -> Parser.parse(""));
    }

    // ---------- parse: todo ----------

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_todoWithBlankDescription_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("todo    "));
    }

    // ---------- parse: deadline ----------

    @Test
    public void parse_deadlineWithoutByKeyword_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    public void parse_deadlineWithBlankDate_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("deadline return book /by   "));
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("deadline /by 2025-10-15"));
    }

    @Test
    public void parse_deadlineWithInvalidDateFormat_exceptionThrown() {
        assertThrows(DateTimeException.class, () -> Parser.parse("deadline return book /by 15-10-2025"));
    }

    // ---------- parse: event ----------

    @Test
    public void parse_eventWithMissingFields_preservesErrorPriorityAndAcceptsNextCommand() throws Exception {
        String[][] invalidEvents = {
            {"event", "Oops! You did not provide an end date for the event"},
            {"event meeting /from /to   ", "Oops! You did not provide an end date for the event"},
            {"event meeting /to 2025-10-03", "Oops! You did not provide a start date for the event"},
            {"event meeting /from   /to 2025-10-03", "Oops! You did not provide a start date for the event"},
            {"event meeting /from/to 2025-10-03", "Oops! Invalid event parameter. "
                    + "Use separate /by, /from, or /to tokens as appropriate."},
            {"event meeting /to 2025-10-03 /from 2025-10-01",
                "Oops! Put /from before /to in an event command."},
            {"event /from invalid /to invalid", "Oops! You cannot have an empty event description"}
        };
        for (String[] invalidEvent : invalidEvents) {
            assertInstanceOf(AddEventCommand.class,
                    Parser.parse("event meeting /from 2025-10-01 /to 2025-10-03"));
            InvalidDescriptionException exception = assertThrows(InvalidDescriptionException.class, () ->
                    Parser.parse(invalidEvent[0]));
            assertEquals(invalidEvent[1], exception.getMessage());
            assertInstanceOf(AddEventCommand.class,
                    Parser.parse("event meeting /from 2025-10-03 /to 2025-10-03"));
        }
    }

    @Test
    public void parse_eventWithInvalidCalendarDate_dateExceptionAndNextCommandAccepted() throws Exception {
        assertThrows(DateTimeException.class, () ->
                Parser.parse("event meeting /from 2025-02-30 /to 2025-03-01"));
        assertInstanceOf(AddEventCommand.class,
                Parser.parse("event meeting /from 2024-02-29 /to 2024-03-01"));
        assertThrows(DateTimeException.class, () ->
                Parser.parse("event meeting /from 2025-02-28 /to 2025-02-30"));
    }

    @Test
    public void parse_eventWithoutToKeyword_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("event career fair /from 2025-10-01"));
    }

    @Test
    public void parse_eventWithoutFromKeyword_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("event career fair /to 2025-10-03"));
    }

    @Test
    public void parse_eventWithFromAfterTo_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () ->
                Parser.parse("event career fair /to 2025-10-03 /from 2025-10-01"));
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () ->
                Parser.parse("event /from 2025-10-01 /to 2025-10-03"));
    }

    @Test
    public void parse_eventWithInvalidDateFormat_exceptionThrown() {
        assertThrows(DateTimeException.class, () ->
                Parser.parse("event career fair /from 01-10-2025 /to 03-10-2025"));
    }

    // ---------- parse: show ----------

    @Test
    public void parse_showWithoutDate_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("show"));
    }

    @Test
    public void parse_showWithInvalidDateFormat_exceptionThrown() {
        assertThrows(DateTimeException.class, () -> Parser.parse("show 15-10-2025"));
    }

    // ---------- parse: find ----------

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("find"));
    }

    @Test
    public void parse_findWithBlankKeyword_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("find    "));
    }

    // ---------- parseDate ----------

    @Test
    public void parseDate_validDate_dateReturned() {
        assertEquals(LocalDate.of(2025, 10, 15), Parser.parseDate("2025-10-15"));
    }

    @Test
    public void parseDate_wrongFormat_exceptionThrown() {
        assertThrows(DateTimeException.class, () -> Parser.parseDate("15-10-2025"));
    }

    @Test
    public void parseDate_nonExistentDate_exceptionThrown() {
        assertThrows(DateTimeException.class, () -> Parser.parseDate("2025-02-30"));
    }

    // ---------- parseShowDate ----------

    @Test
    public void parseShowDate_validInput_dateReturned() throws Exception {
        assertEquals(LocalDate.of(2025, 10, 15), Parser.parseShowDate("show 2025-10-15"));
    }

    @Test
    public void parseShowDate_missingDate_exceptionThrown() {
        assertThrows(InvalidDescriptionException.class, () -> Parser.parseShowDate("show"));
    }

    // ---------- date formatting ----------

    @Test
    public void parse_surroundingWhitespaceAndTabs_validCommandsKeepDescriptions() throws Exception {
        assertInstanceOf(ListCommand.class, Parser.parse(" \tlist\t "));
        assertInstanceOf(AddTodoCommand.class, Parser.parse("\ttodo\tread  书!\t"));
        assertInstanceOf(AddEventCommand.class,
                Parser.parse(" event\tmeeting\t/from\t2026-09-20\t/to\t2026-09-20 "));
        assertEquals(LocalDate.of(2026, 9, 20), Parser.parseShowDate("\tshow\t2026-09-20 "));
        assertThrows(InvalidDescriptionException.class, () -> Parser.parseShowDate("list 2026-09-20"));
        assertInstanceOf(ExitCommand.class, Parser.parse(" bye "));
    }

    @Test
    public void parse_argumentFreeCommandsWithExtraText_rejectedWithoutExiting() throws Exception {
        for (String command : new String[]{"list", "bye"}) {
            InvalidDescriptionException error = assertThrows(InvalidDescriptionException.class, () ->
                    Parser.parse(command + " unexpected"));
            assertEquals("Oops! Use " + command + " without any arguments.", error.getMessage());
            assertInstanceOf(ListCommand.class, Parser.parse("list"));
        }
    }

    @Test
    public void parse_duplicateOrMalformedParameters_rejectedAndNextCommandAccepted() throws Exception {
        String[] invalidCommands = {
            "deadline work /by 2026-09-20 /by 2026-09-21",
            "deadline work /by2026-09-20",
            "deadline work /from 2026-09-20 /by 2026-09-21",
            "event work /from 2026-09-20 /from 2026-09-21 /to 2026-09-22",
            "event work /from 2026-09-20 /to 2026-09-21 /to 2026-09-22",
            "event work /from2026-09-20 /to 2026-09-21"
        };
        for (String invalidCommand : invalidCommands) {
            assertThrows(InvalidDescriptionException.class, () -> Parser.parse(invalidCommand), invalidCommand);
            assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline work /by 2026-09-20"));
        }
    }

    @Test
    public void parse_reversedEventDates_rejectedAndSameDayEventAccepted() throws Exception {
        InvalidDescriptionException error = assertThrows(InvalidDescriptionException.class, () ->
                Parser.parse("event work /from 2026-09-21 /to 2026-09-20"));
        assertEquals("Oops! An event's end date must be on or after its start date.", error.getMessage());
        assertInstanceOf(AddEventCommand.class,
                Parser.parse("event work /from 2026-09-20 /to 2026-09-20"));
    }

    @Test
    public void parse_unsafeDescriptionCharacters_rejectedWhileOrdinaryTextAccepted() throws Exception {
        String[] commands = {"todo read | book", "todo read\nbook", "todo read\u0000book",
            "deadline read | book /by 2026-09-20", "event read | book /from 2026-09-20 /to 2026-09-21"};
        for (String command : commands) {
            assertThrows(InvalidDescriptionException.class, () -> Parser.parse(command));
            assertInstanceOf(AddTodoCommand.class, Parser.parse("todo read 书! @home #1 / notes"));
        }
    }

    @Test
    public void parseDate_unsupportedYears_rejectedWhileBoundaryAndLeapDatesAccepted() {
        for (String date : new String[]{"0000-01-01", "-0001-01-01", "+10000-01-01", "2026-2-01"}) {
            assertThrows(DateTimeException.class, () -> Parser.parseDate(date));
            assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("2024-02-29"));
        }
        assertEquals(LocalDate.of(1, 1, 1), Parser.parseDate("0001-01-01"));
        assertEquals(LocalDate.of(9999, 12, 31), Parser.parseDate("9999-12-31"));
    }

    @Test
    public void formatDateForStorage_validDate_isoStringReturned() {
        assertEquals("2025-10-15", Parser.formatDateForStorage(LocalDate.of(2025, 10, 15)));
    }

    @Test
    public void formatDateForDisplay_validDate_displayStringReturned() {
        assertEquals("Oct 15 2025", Parser.formatDateForDisplay(LocalDate.of(2025, 10, 15)));
    }

    @Test
    public void formatDateForStorage_parsedBack_originalDateReturned() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        assertEquals(date, Parser.parseDate(Parser.formatDateForStorage(date)));
    }
}
