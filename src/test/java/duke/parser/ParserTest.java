package duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.ToDo;

/**
 * Tests command parsing behavior.
 */
public class ParserTest {
    @Test
    public void parseCommandType_knownCommands_returnsCorrectType() throws DukeException {
        assertEquals(Parser.CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(Parser.CommandType.DEADLINE, Parser.parseCommandType("deadline return book /by 2019-10-15"));
        assertEquals(Parser.CommandType.EVENT,
                Parser.parseCommandType("event meeting /from 2019-10-15 /to 2019-10-16"));
        assertEquals(Parser.CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(Parser.CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(Parser.CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(Parser.CommandType.SCHEDULE, Parser.parseCommandType("schedule 2019-10-15"));
        assertEquals(Parser.CommandType.EXIT, Parser.parseCommandType("bye"));
    }

    @Test
    public void parseCommandType_unknownCommand_throwsException() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseCommandType("hello"));
        assertEquals(
                "Sorry, I don't understand that command. Try todo, deadline, event, list, "
                        + "mark, unmark, delete, find, schedule, or bye.",
                exception.getMessage());
    }

    @Test
    public void parseCommandType_blankCommand_throwsHelpfulException() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseCommandType(" \t "));
        assertEquals("Please type a command. For example: todo read book", exception.getMessage());
    }

    @Test
    public void parseEvent_endBeforeStart_throwsException() {
        DukeException exception = assertThrows(DukeException.class,
                () -> Parser.parseEvent("event backwards /from 2019-10-16 /to 2019-10-15"));
        assertEquals("An event's end date cannot be before its start date. Check /from and /to.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_sameDay_returnsEvent() throws DukeException {
        Event task = Parser.parseEvent("event meeting /from 2019-10-15 /to 2019-10-15");
        assertEquals(task.getFrom(), task.getTo());
    }

    @Test
    public void parseDeadline_impossibleCalendarDate_throwsException() {
        assertThrows(DukeException.class, () -> Parser.parseDeadline("deadline report /by 2019-02-29"));
    }

    @Test
    public void parseFindKeyword_validCommand_returnsKeyword() throws DukeException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsException() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseFindKeyword("find"));

        assertEquals("A find command needs a keyword. For example: find book", exception.getMessage());
    }

    @Test
    public void parseScheduleDate_validIsoDate_returnsDate() throws DukeException {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseScheduleDate("schedule 2019-10-15"));
    }

    @Test
    public void parseScheduleDate_missingDate_throwsException() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseScheduleDate("schedule"));

        assertEquals("A schedule command needs a date. For example: schedule 2019-10-15", exception.getMessage());
    }

    @Test
    public void parseScheduleDate_invalidDate_throwsException() {
        DukeException exception = assertThrows(
                DukeException.class, () -> Parser.parseScheduleDate("schedule next Monday"));

        assertEquals(
                "Dates must be in yyyy-MM-dd format. For example: schedule 2019-10-15",
                exception.getMessage());
    }

    @Test
    public void parseToDo_validCommand_returnsTodo() throws DukeException {
        ToDo task = Parser.parseToDo("todo read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseToDo_missingDescription_throwsException() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseToDo("todo"));

        assertEquals("A todo needs a description. For example: todo read book", exception.getMessage());
    }

    @Test
    public void parseDeadline_validIsoDate_returnsDeadlineWithFormattedDate() throws DukeException {
        Deadline task = Parser.parseDeadline("deadline return book /by 2019-10-15");

        assertEquals("[D][ ] return book (by: Oct 15 2019)", task.toString());
        assertEquals("2019-10-15", task.getBy().toString());
    }

    @Test
    public void parseDeadline_invalidDate_throwsException() {
        DukeException exception = assertThrows(
                DukeException.class, () -> Parser.parseDeadline("deadline return book /by Sunday"));

        assertEquals(
                "Dates must be in yyyy-MM-dd format. For example: deadline submit report /by 2019-10-15",
                exception.getMessage());
    }

    @Test
    public void parseEvent_validIsoDates_returnsEventWithFormattedDates() throws DukeException {
        Event task = Parser.parseEvent("event project meeting /from 2019-10-15 /to 2019-10-16");

        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)", task.toString());
        assertEquals("2019-10-15", task.getFrom().toString());
        assertEquals("2019-10-16", task.getTo().toString());
    }

    @Test
    public void parseEvent_missingTo_throwsException() {
        DukeException exception = assertThrows(
                DukeException.class, () -> Parser.parseEvent("event project meeting /from 2019-10-15"));

        assertEquals(
                "An event needs /from and /to. For example: event meeting /from 2019-10-15 /to 2019-10-16",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validTaskNumber_returnsZeroBasedIndex() throws DukeException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", Parser.CommandType.MARK, 3));
        assertEquals(2, Parser.parseTaskIndex("delete 3", Parser.CommandType.DELETE, 3));
    }

    @Test
    public void parseTaskIndex_invalidTaskNumber_throwsException() {
        DukeException exception = assertThrows(
                DukeException.class, () -> Parser.parseTaskIndex("mark 4", Parser.CommandType.MARK, 3));

        assertEquals("Task 4 does not exist. Use list to see the available task numbers.", exception.getMessage());
    }
}
