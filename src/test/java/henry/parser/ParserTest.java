package henry.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import henry.exception.HenryException;
import henry.exception.InvalidDateException;
import henry.task.Deadline;
import henry.task.Event;
import henry.task.Task;
import henry.task.Todo;

/**
 * Tests task-number parsing and validation in {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parseCommandType_exactCommands_returnsMatchingTypes() {
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find"));
        assertEquals(CommandType.HELP, Parser.parseCommandType("help"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event"));
    }

    @Test
    public void parseCommandType_commandWithArguments_returnsMatchingType() {
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.EVENT,
                Parser.parseCommandType(
                        "event meeting /from 2/12/2019 1400 /to 2/12/2019 1500"));
    }

    @Test
    public void parseCommandType_aliases_returnsMatchingTypes() {
        assertEquals(CommandType.BYE, Parser.parseCommandType("b"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("l"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("m 1"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("u 1"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("del 1"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("f book"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("t read book"));
        assertEquals(CommandType.DEADLINE,
                Parser.parseCommandType("d submit report /by 2019-12-02"));
        assertEquals(CommandType.EVENT,
                Parser.parseCommandType(
                        "e meeting /from 2/12/2019 1400 /to 2/12/2019 1500"));
    }

    @Test
    public void parseCommandType_unknownOrMalformedCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType(""));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("dance"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("todoist"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("bye now"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("T read book"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("tread book"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("deluxe 1"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("b now"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("l extra"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("help me"));
    }

    @Test
    public void parseTaskIndex_firstTask_returnsZeroBasedIndex() throws HenryException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_lastTask_returnsZeroBasedIndex() throws HenryException {
        assertEquals(2, Parser.parseTaskIndex("delete 3", CommandType.DELETE, 3));
    }

    @Test
    public void parseTaskIndex_extraWhitespace_returnsZeroBasedIndex() throws HenryException {
        assertEquals(1, Parser.parseTaskIndex("u   2  ", CommandType.UNMARK, 3));
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTaskIndex("mark", CommandType.MARK, 3));

        assertEquals("I'll need a task number for that. For example: mark 1",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericTaskNumber_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTaskIndex("delete two", CommandType.DELETE, 3));

        assertEquals("'two' isn't a valid task number.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTaskIndex("mark 1", CommandType.MARK, 0));

        assertEquals("There aren't any tasks to mark yet.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_zeroTaskNumber_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTaskIndex("mark 0", CommandType.MARK, 3));

        assertEquals("I can't find task 0. Choose a number from 1 to 3.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_taskNumberAboveTaskCount_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTaskIndex("delete 4", CommandType.DELETE, 3));

        assertEquals("I can't find task 4. Choose a number from 1 to 3.", exception.getMessage());
    }

    @Test
    public void parseKeyword_keywordProvided_returnsKeyword() throws HenryException {
        assertEquals("read book", Parser.parseKeyword("f   read book  "));
    }

    @Test
    public void parseKeyword_keywordMissing_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseKeyword("find"));

        assertEquals("I'll need a keyword for that. For example: find book",
                exception.getMessage());
    }

    @Test
    public void parseTask_todoWithDescription_returnsTodo()
            throws HenryException, InvalidDateException {
        Task task = Parser.parseTask("t read a book", CommandType.TODO);

        assertInstanceOf(Todo.class, task);
        assertEquals("T | 0 | read a book", task.toFileString());
    }

    @Test
    public void parseTask_todoWithoutDescription_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("todo", CommandType.TODO));

        assertEquals("I'll need a description for that. For example: todo borrow a book",
                exception.getMessage());
    }

    @Test
    public void parseTask_validDeadline_returnsDeadline()
            throws HenryException, InvalidDateException {
        Task task = Parser.parseTask(
                "deadline submit report /by 2/12/2019 1800", CommandType.DEADLINE);

        assertInstanceOf(Deadline.class, task);
        assertEquals("D | 0 | submit report | 2019-12-02T18:00", task.toFileString());
    }

    @Test
    public void parseTask_deadlineWithoutBySeparator_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("deadline submit report", CommandType.DEADLINE));

        assertEquals("A deadline needs '/by'. For example: deadline submit report /by 2019-12-02",
                exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithoutDescription_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("deadline /by 2019-12-02", CommandType.DEADLINE));

        assertEquals("A deadline needs a description before '/by'.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithoutDate_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("deadline submit report /by", CommandType.DEADLINE));

        assertEquals("A deadline needs a date or time after '/by'.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithUnsupportedDateFormat_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask(
                        "deadline submit report /by tomorrow", CommandType.DEADLINE));

        assertEquals("Please use a deadline date like 2/12/2019 1800 or 2019-12-02.",
                exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithInvalidIsoDate_exceptionThrown() {
        InvalidDateException exception = assertThrows(InvalidDateException.class, () ->
                Parser.parseTask(
                        "deadline submit report /by 2023-02-29", CommandType.DEADLINE));

        assertEquals("That date is incorrect. Please enter a valid calendar date.",
                exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithDateOutsideMonth_exceptionThrown() {
        InvalidDateException exception = assertThrows(InvalidDateException.class, () ->
                Parser.parseTask(
                        "deadline submit report /by 31/4/2025 1800", CommandType.DEADLINE));

        assertEquals("That date is incorrect. Please enter a valid calendar date.",
                exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithInvalidTime_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask(
                        "deadline submit report /by 2/12/2019 2400", CommandType.DEADLINE));

        assertEquals("Please use a deadline date like 2/12/2019 1800 or 2019-12-02.",
                exception.getMessage());
    }

    @Test
    public void parseTask_validEvent_returnsEvent()
            throws HenryException, InvalidDateException {
        Task task = Parser.parseTask(
                "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1500",
                CommandType.EVENT);

        assertInstanceOf(Event.class, task);
        assertEquals("E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T15:00",
                task.toFileString());
    }

    @Test
    public void parseTask_eventWithoutFromSeparator_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("event meeting /to 2/12/2019 1500", CommandType.EVENT));

        assertEquals("An event needs '/from' and '/to'. "
                + "For example: event meeting /from 2/12/2019 1400 /to 2/12/2019 1500",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutToSeparator_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("event meeting /from 2/12/2019 1400", CommandType.EVENT));

        assertEquals("An event needs an ending time introduced by '/to'.", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutDescription_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask(
                        "event /from 2/12/2019 1400 /to 2/12/2019 1500", CommandType.EVENT));

        assertEquals("An event needs a description before '/from'.", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutStartTime_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("event meeting /from /to 2/12/2019 1500", CommandType.EVENT));

        assertEquals("An event needs a starting time after '/from'.", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutEndTime_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask("event meeting /from 2/12/2019 1400 /to", CommandType.EVENT));

        assertEquals("An event needs an ending time after '/to'.", exception.getMessage());
    }

    @Test
    public void parseTask_eventWithInvalidCalendarDate_exceptionThrown() {
        InvalidDateException exception = assertThrows(InvalidDateException.class, () ->
                Parser.parseTask(
                        "event meeting /from 31/4/2025 1400 /to 1/5/2025 1500",
                        CommandType.EVENT));

        assertEquals("That date is incorrect. Please enter a valid calendar date.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithUnsupportedDateFormat_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask(
                        "event meeting /from tomorrow /to later", CommandType.EVENT));

        assertEquals("Please use an event date like 2/12/2019 1400 or 2019-12-02.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventEndingBeforeStart_exceptionThrown() {
        HenryException exception = assertThrows(HenryException.class, () ->
                Parser.parseTask(
                        "event meeting /from 2/12/2019 1500 /to 2/12/2019 1400",
                        CommandType.EVENT));

        assertEquals("An event's ending time must be after its starting time.",
                exception.getMessage());
    }

    @Test
    public void parseTask_nonTaskCommand_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                Parser.parseTask("list", CommandType.LIST));
    }
}
