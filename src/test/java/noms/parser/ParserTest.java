package noms.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import noms.command.AddCommand;
import noms.command.Command;
import noms.command.CommandType;
import noms.command.DeleteCommand;
import noms.command.ExitCommand;
import noms.command.FindCommand;
import noms.command.ListCommand;
import noms.command.MarkCommand;
import noms.command.OnCommand;
import noms.command.SnoozeCommand;
import noms.command.UnmarkCommand;
import noms.exception.EmptyCommandException;
import noms.exception.EmptyDescriptionException;
import noms.exception.EmptyKeywordException;
import noms.exception.InvalidDateException;
import noms.exception.InvalidDeadlineException;
import noms.exception.InvalidEventException;
import noms.exception.InvalidEventDateRangeException;
import noms.exception.InvalidSnoozeException;
import noms.exception.InvalidTaskNumberException;
import noms.exception.NomsException;
import noms.exception.UnexpectedArgumentException;
import noms.exception.UnknownCommandException;
import noms.task.Deadline;
import noms.task.Event;
import noms.task.Task;
import noms.task.ToDo;

/**
 * Tests {@link Parser}, the class that interprets raw command lines. These
 * methods carry the bulk of Noms' input-handling logic, so they are the
 * highest-value target for testing: a bug here silently mishandles user
 * commands. The cases cover command recognition, task parsing, task-number
 * validation, and date extraction, along with the top-level dispatch.
 */
public class ParserTest {

    // --- getCommandType ---

    @Test
    public void getCommandType_knownCommandAnyCase_returnsType() throws EmptyCommandException {
        assertEquals(CommandType.LIST, Parser.getCommandType("list"));
        assertEquals(CommandType.TODO, Parser.getCommandType("TODO read book"));
        assertEquals(CommandType.BYE, Parser.getCommandType("  bye  "));
        assertEquals(CommandType.SNOOZE, Parser.getCommandType("SnOoZe 1 /by 2026-09-20"));
    }

    @Test
    public void getCommandType_unknownCommand_returnsNull() throws EmptyCommandException {
        assertNull(Parser.getCommandType("blah do something"));
    }

    @Test
    public void getCommandType_blankInput_throwsEmptyCommandException() {
        assertThrows(EmptyCommandException.class, () -> Parser.getCommandType(""));
        assertThrows(EmptyCommandException.class, () -> Parser.getCommandType("    "));
    }

    // --- parseTask: valid input ---

    @Test
    public void parseTask_todo_returnsToDoWithDescription() throws NomsException {
        Task task = Parser.parseTask("todo read book");
        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void parseTask_deadline_returnsDeadlineWithDate() throws NomsException {
        Task task = Parser.parseTask("deadline return book /by 2019-06-06");
        assertInstanceOf(Deadline.class, task);
        assertEquals("return book", task.getDescription());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", task.toString());
    }

    @Test
    public void parseTask_event_returnsEventWithRange() throws NomsException {
        Task task = Parser.parseTask("event project meeting /from 2019-08-06 /to 2019-08-07");
        assertInstanceOf(Event.class, task);
        assertEquals("project meeting", task.getDescription());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", task.toString());
    }

    @Test
    public void parseTask_surroundingWhitespace_preservesTaskDetails() throws NomsException {
        Task todo = Parser.parseTask("  todo read book  ");
        Task deadline = Parser.parseTask(
                "  deadline return book /by 2019-06-06  ");
        Task event = Parser.parseTask(
                "  event meeting /from 2019-08-06 /to 2019-08-07  ");

        assertEquals("read book", todo.getDescription());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadline.toString());
        assertEquals("[E][ ] meeting (from: Aug 06 2019 to: Aug 07 2019)",
                event.toString());
    }

    @Test
    public void parseTask_flexibleStructuralWhitespace_returnsTasks() throws NomsException {
        Task deadline = Parser.parseTask(
                "deadline\t\treturn book   /by\t2019-06-06");
        Task event = Parser.parseTask(
                "event   meeting\t/from   2019-08-06\t/to\t\t2019-08-07");

        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadline.toString());
        assertEquals("[E][ ] meeting (from: Aug 06 2019 to: Aug 07 2019)",
                event.toString());
    }

    // --- parseTask: invalid input ---

    @Test
    public void parseTask_todoWithoutDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parseTask("todo"));
        assertThrows(EmptyDescriptionException.class, () -> Parser.parseTask("todo    "));
    }

    @Test
    public void parseTask_deadlineWordOnly_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parseTask("deadline"));
    }

    @Test
    public void parseTask_deadlineWithoutBySection_throwsInvalidDeadlineException() {
        assertThrows(InvalidDeadlineException.class,
                () -> Parser.parseTask("deadline return book"));
    }

    @Test
    public void parseTask_deadlineWithEmptyDate_throwsInvalidDeadlineException() {
        assertThrows(InvalidDeadlineException.class,
                () -> Parser.parseTask("deadline return book /by "));
    }

    @Test
    public void parseTask_deadlineWithUnparseableDate_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class,
                () -> Parser.parseTask("deadline return book /by Sunday"));
    }

    @Test
    public void parseTask_deadlineWithEmptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class,
                () -> Parser.parseTask("deadline   /by 2019-12-01"));
    }

    @Test
    public void parseTask_eventWordOnly_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> Parser.parseTask("event"));
    }

    @Test
    public void parseTask_eventMissingToSection_throwsInvalidEventException() {
        assertThrows(InvalidEventException.class,
                () -> Parser.parseTask("event meeting /from 2019-08-06"));
    }

    @Test
    public void parseTask_eventWithUnparseableDate_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class,
                () -> Parser.parseTask("event meeting /from 2019-08-06 /to nextweek"));
    }

    @Test
    public void parseTask_eventWithoutFromSection_throwsInvalidEventException() {
        assertThrows(InvalidEventException.class,
                () -> Parser.parseTask("event meeting /to 2019-08-07"));
    }

    @Test
    public void parseTask_eventWithEmptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class,
                () -> Parser.parseTask("event   /from 2019-08-06 /to 2019-08-07"));
    }

    @Test
    public void parseTask_eventWithEmptyStartDate_throwsInvalidEventException() {
        assertThrows(InvalidEventException.class,
                () -> Parser.parseTask("event meeting /from   /to 2019-08-07"));
    }

    @Test
    public void parseTask_eventWithEmptyEndDate_throwsInvalidEventException() {
        assertThrows(InvalidEventException.class,
                () -> Parser.parseTask("event meeting /from 2019-08-06 /to   "));
    }

    @Test
    public void parseTask_eventEndsBeforeStart_throwsInvalidEventDateRangeException() {
        assertThrows(InvalidEventDateRangeException.class,
                () -> Parser.parseTask("event trip /from 2026-10-05 /to 2026-10-01"));
    }

    @Test
    public void parseTask_eventStartsAndEndsSameDay_throwsInvalidEventDateRangeException() {
        assertThrows(InvalidEventDateRangeException.class,
                () -> Parser.parseTask("event trip /from 2026-10-05 /to 2026-10-05"));
    }

    @Test
    public void parseTask_unknownCommand_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> Parser.parseTask("cook dinner"));
    }

    @Test
    public void parseTask_nonTaskCommand_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> Parser.parseTask("list"));
    }

    // --- parseTaskNumber ---

    @Test
    public void parseTaskNumber_validInRange_returnsNumber() throws InvalidTaskNumberException {
        assertEquals(2, Parser.parseTaskNumber("mark 2", "mark", 3));
    }

    @Test
    public void parseTaskNumber_emptyList_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark 1", "mark", 0));
    }

    @Test
    public void parseTaskNumber_missingNumber_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark", "mark", 1));
    }

    @Test
    public void parseTaskNumber_extraArguments_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark 1 2", "mark", 2));
    }

    @Test
    public void parseTaskNumber_nonNumeric_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark abc", "mark", 1));
    }

    @Test
    public void parseTaskNumber_belowRange_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark 0", "mark", 1));
    }

    @Test
    public void parseTaskNumber_aboveRange_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark 99", "mark", 1));
    }

    @Test
    public void parseTaskNumber_overflowsInt_throwsInvalidTaskNumberException() {
        // Digits that exceed Integer.MAX_VALUE trip NumberFormatException,
        // which the method reports as "too large" rather than crashing.
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseTaskNumber("mark 999999999999", "mark", 1));
    }

    // --- snooze parsing ---

    @Test
    public void parseSnoozeTaskNumber_validCommand_returnsNumber() throws NomsException {
        assertEquals(2, Parser.parseSnoozeTaskNumber(
                "snooze 2 /from 2026-09-20", 3));
    }

    @Test
    public void parseSnoozeTaskNumber_missingNumber_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseSnoozeTaskNumber("snooze", 1));
    }

    @Test
    public void parseSnoozeTaskNumber_emptyList_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class,
                () -> Parser.parseSnoozeTaskNumber("snooze 1 /by 2026-09-20", 0));
    }

    @Test
    public void parseDeadlineSnoozeDate_validCommand_returnsDate() throws NomsException {
        assertEquals(LocalDate.of(2026, 9, 20),
                Parser.parseDeadlineSnoozeDate("SnOoZe 1 /by 2026-09-20"));
    }

    @Test
    public void parseDeadlineSnoozeDate_uppercaseMarker_throwsInvalidSnoozeException() {
        assertThrows(InvalidSnoozeException.class,
                () -> Parser.parseDeadlineSnoozeDate("snooze 1 /BY 2026-09-20"));
    }

    @Test
    public void parseDeadlineSnoozeDate_extraText_throwsInvalidSnoozeException() {
        assertThrows(InvalidSnoozeException.class,
                () -> Parser.parseDeadlineSnoozeDate("snooze 1 /by 2026-09-20 extra"));
    }

    @Test
    public void parseEventSnoozeDates_fromOnly_returnsEmptyEndDate() throws NomsException {
        Parser.EventSnoozeDates dates = Parser.parseEventSnoozeDates(
                "snooze 1 /from 2026-09-20");

        assertEquals(LocalDate.of(2026, 9, 20), dates.startDate());
        assertTrue(dates.endDate().isEmpty());
    }

    @Test
    public void parseEventSnoozeDates_fromAndTo_returnsBothDates() throws NomsException {
        Parser.EventSnoozeDates dates = Parser.parseEventSnoozeDates(
                "snooze 1 /from 2026-09-20 /to 2026-09-22");

        assertEquals(LocalDate.of(2026, 9, 20), dates.startDate());
        assertEquals(LocalDate.of(2026, 9, 22), dates.endDate().orElseThrow());
    }

    @Test
    public void parseEventSnoozeDates_invalidDate_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class,
                () -> Parser.parseEventSnoozeDates("snooze 1 /from tomorrow"));
    }

    @Test
    public void parseEventSnoozeDates_invalidSyntax_throwsInvalidSnoozeException() {
        assertThrows(InvalidSnoozeException.class,
                () -> Parser.parseEventSnoozeDates("snooze 1 /to 2026-09-22"));
    }

    @Test
    public void parseEventSnoozeDates_invalidRange_throwsInvalidEventDateRangeException() {
        assertThrows(InvalidEventDateRangeException.class,
                () -> Parser.parseEventSnoozeDates(
                        "snooze 1 /from 2026-10-05 /to 2026-10-01"));
    }

    // --- parseOnDate ---

    @Test
    public void parseOnDate_validDate_returnsDate() throws NomsException {
        assertEquals(LocalDate.of(2019, 12, 1), Parser.parseOnDate("on 2019-12-01"));
    }

    @Test
    public void parseOnDate_missingDate_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> Parser.parseOnDate("on"));
    }

    @Test
    public void parseOnDate_unparseableDate_throwsInvalidDateException() {
        assertThrows(InvalidDateException.class, () -> Parser.parseOnDate("on Sunday"));
    }

    // --- parseKeyword ---

    @Test
    public void parseKeyword_singleWord_returnsKeyword() throws NomsException {
        assertEquals("book", Parser.parseKeyword("find book"));
    }

    @Test
    public void parseKeyword_multiWord_returnsWholeRemainder() throws NomsException {
        // Everything after "find" is the keyword, so spaces are preserved.
        assertEquals("read book", Parser.parseKeyword("find read book"));
    }

    @Test
    public void parseKeyword_missingKeyword_throwsEmptyKeywordException() {
        assertThrows(EmptyKeywordException.class, () -> Parser.parseKeyword("find"));
        assertThrows(EmptyKeywordException.class, () -> Parser.parseKeyword("find   "));
    }

    // --- parse: top-level dispatch to the right Command ---

    @Test
    public void parse_recognizedCommands_returnMatchingCommandType() throws NomsException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(SnoozeCommand.class, Parser.parse("snooze 1 /by 2026-09-20"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-01"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_topLevelDeadline_returnsAddCommand() throws NomsException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline submit report /by 2026-09-20"));
    }

    @Test
    public void parse_topLevelEvent_returnsAddCommand() throws NomsException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event conference /from 2026-09-20 /to 2026-09-22"));
    }

    @Test
    public void parse_unknownCommand_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void parse_blankCommand_throwsEmptyCommandException() {
        assertThrows(EmptyCommandException.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_listWithArguments_throwsUnexpectedArgumentException() {
        assertThrows(UnexpectedArgumentException.class,
                () -> Parser.parse("list extra"));
    }

    @Test
    public void parse_byeWithArguments_throwsUnexpectedArgumentException() {
        assertThrows(UnexpectedArgumentException.class,
                () -> Parser.parse("bye now"));
    }

    @Test
    public void parse_parameterlessCommandsWithSurroundingWhitespace_returnCommands()
            throws NomsException {
        assertInstanceOf(ListCommand.class, Parser.parse("  LIST  "));
        assertInstanceOf(ExitCommand.class, Parser.parse("  BYE  "));
    }

    @Test
    public void parse_returnsCommandType() throws NomsException {
        // Sanity check that parse yields the Command supertype callers rely on.
        Command command = Parser.parse("list");
        assertInstanceOf(Command.class, command);
    }
}
