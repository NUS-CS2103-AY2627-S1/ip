package mochi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mochi.command.AddDeadlineCommand;
import mochi.command.AddEventCommand;
import mochi.command.AddTodoCommand;
import mochi.command.Command;
import mochi.command.DeleteCommand;
import mochi.command.ExitCommand;
import mochi.command.ListCommand;
import mochi.command.MarkCommand;
import mochi.command.UnmarkCommand;

public class ParserTest {

    @Test
    public void parse_bye_returnsExitCommand() throws MochiException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_list_returnsListCommand() throws MochiException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws MochiException {
        Command cmd = Parser.parse("mark 2");
        assertInstanceOf(MarkCommand.class, cmd);
    }

    @Test
    public void parse_unmark_returnsUnmarkCommand() throws MochiException {
        Command cmd = Parser.parse("unmark 1");
        assertInstanceOf(UnmarkCommand.class, cmd);
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws MochiException {
        Command cmd = Parser.parse("delete 3");
        assertInstanceOf(DeleteCommand.class, cmd);
    }

    @Test
    public void parse_todo_returnsAddTodoCommand() throws MochiException {
        Command cmd = Parser.parse("todo read book");
        assertInstanceOf(AddTodoCommand.class, cmd);
    }

    @Test
    public void parse_deadline_returnsAddDeadlineCommand() throws MochiException {
        Command cmd = Parser.parse("deadline return book /by 2019-12-02");
        assertInstanceOf(AddDeadlineCommand.class, cmd);
    }

    @Test
    public void parse_event_returnsAddEventCommand() throws MochiException {
        Command cmd = Parser.parse("event meeting /from 2019-10-15 /to 2019-10-16");
        assertInstanceOf(AddEventCommand.class, cmd);
    }

    @Test
    public void parse_unknownCommand_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class, () -> Parser.parse("blah"));
        assertEquals("I'm sorry, but I don't know what that means :-(", ex.getMessage());
    }

    @Test
    public void parse_markNonNumeric_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class, () -> Parser.parse("mark abc"));
        assertEquals("Please give a task number, e.g., mark 2", ex.getMessage());
    }

    @Test
    public void parse_markNoArgs_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class, () -> Parser.parse("mark"));
        assertEquals("Please give a task number, e.g., mark 2", ex.getMessage());
    }

    @Test
    public void parse_todoEmpty_returnsAddTodoCommandWithEmptyArgs() throws MochiException {
        Command cmd = Parser.parse("todo");
        assertInstanceOf(AddTodoCommand.class, cmd);
    }

    @Test
    public void parse_deadlineMissingBy_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class, () -> Parser.parse("deadline return book"));
        assertEquals("Please add the deadline with /by, e.g., deadline return book /by Sunday",
                ex.getMessage());
    }

    @Test
    public void parse_eventMissingFrom_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class, () -> Parser.parse("event meeting"));
        assertEquals("Please add the start time with /from, e.g., event project meeting /from Mon 2pm /to 4pm",
                ex.getMessage());
    }

    @Test
    public void parse_eventMissingTo_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class,
                () -> Parser.parse("event meeting /from 2019-10-15"));
        assertEquals("Please add the end time with /to, e.g., event project meeting /from Mon 2pm /to 4pm",
                ex.getMessage());
    }
}
