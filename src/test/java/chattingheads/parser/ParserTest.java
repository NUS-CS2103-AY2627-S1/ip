package chattingheads.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import chattingheads.command.AddDeadlineCommand;
import chattingheads.command.AddEventCommand;
import chattingheads.command.AddTodoCommand;
import chattingheads.command.Command;
import chattingheads.command.DeleteCommand;
import chattingheads.command.ExitCommand;
import chattingheads.command.FindCommand;
import chattingheads.command.ListCommand;
import chattingheads.command.MarkCommand;
import chattingheads.command.RescheduleDeadlineCommand;
import chattingheads.command.RescheduleEventCommand;
import chattingheads.command.UnmarkCommand;
import chattingheads.exception.ChattingHeadsException;
import chattingheads.exception.InvalidCommandException;
import chattingheads.exception.InvalidInputException;
import chattingheads.task.TaskList;
import chattingheads.task.Todo;
import chattingheads.ui.Ui;

public class ParserTest {

    private final Parser parser = new Parser();
    private final Ui ui = new Ui();

    @Test
    public void parse_validTodo_returnsAddTodoCommand() throws ChattingHeadsException {
        Command command = parser.parse("todo read book");
        assertInstanceOf(AddTodoCommand.class, command);
    }

    @Test
    public void parse_validTodo_returnsCorrectDescription() throws ChattingHeadsException {
        Command command = parser.parse("todo read book");
        AddTodoCommand addTodoCommand = (AddTodoCommand) command;
        assertEquals("read book", addTodoCommand.getDescription());
    }

    @Test
    public void parse_validTodoWithExtraWhitespace_normalisesDescription() throws ChattingHeadsException {
        Command command = parser.parse("  todo   read   book  ");
        AddTodoCommand addTodoCommand = (AddTodoCommand) command;
        assertEquals("read book", addTodoCommand.getDescription());
    }

    @Test
    public void parse_missingTodoDescription_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("todo"));
    }

    @Test
    public void parse_blankInput_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("   "));
    }

    @Test
    public void parse_validDeadline_returnsAddDeadlineCommand() throws ChattingHeadsException {
        Command command = parser.parse("deadline submit report /by 28/08/2026 18:00");
        assertInstanceOf(AddDeadlineCommand.class, command);
    }

    @Test
    public void parse_validDeadline_parsesCorrectValues() throws ChattingHeadsException {
        TaskList taskList = new TaskList();
        Command command = parser.parse("deadline submit report /by 28/08/2026 18:00");

        command.execute(taskList, ui);

        assertEquals("D,submit report,false,2026-08-28T18:00", taskList.get(0).toCsv());
    }

    @Test
    public void parse_deadlineMissingByPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "deadline submit report 28/08/2026 18:00"
        ));
    }

    @Test
    public void parse_deadlineMissingDescription_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "deadline /by 28/08/2026 18:00"
        ));
    }

    @Test
    public void parse_deadlineMissingDateTime_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "deadline submit report /by"
        ));
    }

    @Test
    public void parse_deadlineInvalidDateTime_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "deadline submit report /by 31/02/2026 18:00"
        ));
    }

    @Test
    public void parse_deadlineDuplicateByPrefix_throwsInvalidInputException() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> parser.parse(
                "deadline submit /by 28/08/2026 18:00 /by 29/08/2026 18:00"
        ));
        assertTrue(exception.getMessage().contains("Duplicate prefix: /by"));
    }

    @Test
    public void parse_validEvent_returnsAddEventCommand() throws ChattingHeadsException {
        Command command = parser.parse("event meeting /from 28/08/2026 18:00 /to 28/08/2026 20:00");
        assertInstanceOf(AddEventCommand.class, command);
    }

    @Test
    public void parse_validEvent_parsesCorrectValues() throws ChattingHeadsException {
        TaskList taskList = new TaskList();
        Command command = parser.parse("event meeting /from 28/08/2026 18:00 /to 28/08/2026 20:00");

        command.execute(taskList, ui);

        assertEquals("E,meeting,false,2026-08-28T18:00,2026-08-28T20:00", taskList.get(0).toCsv());
    }

    @Test
    public void parse_eventMissingBothPrefixes_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("event meeting"));
    }

    @Test
    public void parse_eventMissingFromPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "event meeting /to 28/08/2026 20:00"
        ));
    }

    @Test
    public void parse_eventMissingToPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "event meeting /from 28/08/2026 18:00"
        ));
    }

    @Test
    public void parse_eventPrefixesInWrongOrder_throwsInvalidInputException() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> parser.parse(
                "event meeting /to 28/08/2026 20:00 /from 28/08/2026 18:00"
        ));
        assertTrue(exception.getMessage().contains("/from must come before /to"));
    }

    @Test
    public void parse_eventDuplicateFromPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "event meeting /from 28/08/2026 18:00 /from 28/08/2026 19:00 /to 28/08/2026 20:00"
        ));
    }

    @Test
    public void parse_eventDuplicateToPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "event meeting /from 28/08/2026 18:00 /to 28/08/2026 20:00 /to 28/08/2026 21:00"
        ));
    }

    @Test
    public void parse_eventMissingDescription_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "event /from 28/08/2026 18:00 /to 28/08/2026 20:00"
        ));
    }

    @Test
    public void parse_eventInvalidStart_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "event meeting /from tomorrow /to 28/08/2026 20:00"
        ));
    }

    @Test
    public void parse_eventInvalidEnd_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "event meeting /from 28/08/2026 18:00 /to tomorrow"
        ));
    }

    @Test
    public void parse_validList_returnsListCommand() throws ChattingHeadsException {
        Command command = parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_validFind_returnsFindCommand() throws ChattingHeadsException {
        Command command = parser.parse("find book");
        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_validFind_parsesWholeKeyword() throws ChattingHeadsException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book chapter"));
        taskList.add(new Todo("read notes"));
        Command command = parser.parse("find read book");

        String response = command.execute(taskList, ui);

        assertTrue(response.contains("1. [T][ ] read book chapter"));
        assertFalse(response.contains("2. [T][ ] read notes"));
    }

    @Test
    public void parse_findMissingKeyword_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("find"));
    }

    @Test
    public void parse_validMark_returnsCorrectTaskNumber() throws ChattingHeadsException {
        Command command = parser.parse("mark 2");
        MarkCommand markCommand = (MarkCommand) command;
        assertEquals(2, markCommand.getTaskNumber());
    }

    @Test
    public void parse_validUnmark_returnsCorrectTaskNumber() throws ChattingHeadsException {
        Command command = parser.parse("unmark 3");
        UnmarkCommand unmarkCommand = (UnmarkCommand) command;
        assertEquals(3, unmarkCommand.getTaskNumber());
    }

    @Test
    public void parse_validDelete_returnsCorrectTaskNumber() throws ChattingHeadsException {
        Command command = parser.parse("delete 3");
        DeleteCommand deleteCommand = (DeleteCommand) command;
        assertEquals(3, deleteCommand.getTaskNumber());
    }

    @Test
    public void parse_taskNumberMissing_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("mark"));
    }

    @Test
    public void parse_taskNumberNotInteger_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("mark two"));
    }

    @Test
    public void parse_tooManyTaskNumberArguments_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("mark 1 2"));
    }

    @Test
    public void parse_validRescheduleDeadline_returnsCorrectCommandAndTaskNumber()
            throws ChattingHeadsException {
        Command command = parser.parse("reschedule 2 /by 30/08/2026 18:00");

        assertInstanceOf(RescheduleDeadlineCommand.class, command);
        assertEquals(2, ((RescheduleDeadlineCommand) command).getTaskNumber());
    }

    @Test
    public void parse_validRescheduleEvent_returnsCorrectCommandAndTaskNumber()
            throws ChattingHeadsException {
        Command command = parser.parse(
                "reschedule 2 /from 30/08/2026 18:00 /to 30/08/2026 20:00");

        assertInstanceOf(RescheduleEventCommand.class, command);
        assertEquals(2, ((RescheduleEventCommand) command).getTaskNumber());
    }

    @Test
    public void parse_rescheduleMissingArguments_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("reschedule"));
    }

    @Test
    public void parse_rescheduleMissingSchedulePrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("reschedule 1"));
    }

    @Test
    public void parse_rescheduleInvalidTaskNumber_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule one /by 30/08/2026 18:00"
        ));
    }

    @Test
    public void parse_rescheduleDeadlineMissingDateTime_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("reschedule 1 /by"));
    }

    @Test
    public void parse_rescheduleEventMissingTo_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 /from 30/08/2026 18:00"
        ));
    }

    @Test
    public void parse_rescheduleEventMissingFrom_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 /to 30/08/2026 20:00"
        ));
    }

    @Test
    public void parse_rescheduleEventPrefixesInWrongOrder_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 /to 30/08/2026 20:00 /from 30/08/2026 18:00"
        ));
    }

    @Test
    public void parse_rescheduleIncompatiblePrefixes_throwsInvalidInputException() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 /by 30/08/2026 18:00 /from 30/08/2026 19:00 /to 30/08/2026 20:00"
        ));
        assertTrue(exception.getMessage().contains("cannot be used together"));
    }

    @Test
    public void parse_rescheduleDeadlinePrefixNotImmediatelyAfterTaskNumber_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 later /by 30/08/2026 18:00"
        ));
    }

    @Test
    public void parse_rescheduleEventPrefixNotImmediatelyAfterTaskNumber_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 later /from 30/08/2026 18:00 /to 30/08/2026 20:00"
        ));
    }

    @Test
    public void parse_rescheduleDuplicateByPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 /by 30/08/2026 18:00 /by 31/08/2026 18:00"
        ));
    }

    @Test
    public void parse_rescheduleDuplicateFromPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 /from 30/08/2026 18:00 /from 30/08/2026 19:00 /to 30/08/2026 20:00"
        ));
    }

    @Test
    public void parse_rescheduleDuplicateToPrefix_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(
                "reschedule 1 /from 30/08/2026 18:00 /to 30/08/2026 20:00 /to 30/08/2026 21:00"
        ));
    }

    @Test
    public void parse_validBye_returnsExitCommand() throws ChattingHeadsException {
        Command command = parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
    }

    @Test
    public void parse_invalidCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> parser.parse("test"));
    }
}
