package sioet.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sioet.SioetException;

public class ParserTest {

    @Test
    public void parse_bye_returnsExitCommand() throws SioetException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_list_returnsListCommand() throws SioetException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_todo_returnsTodoCommand() throws SioetException {
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_deadline_returnsDeadlineCommand() throws SioetException {
        assertInstanceOf(
                DeadlineCommand.class,
                Parser.parse("deadline return book /by 6/6/2026 2359")
        );
    }

    @Test
    public void parse_event_returnsEventCommand() throws SioetException {
        assertInstanceOf(
                EventCommand.class,
                Parser.parse(
                        "event project meeting /from 6/8/2026 1400 /to 6/8/2026 1600"
                )
        );
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws SioetException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmark_returnsMarkCommand() throws SioetException {
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws SioetException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws SioetException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_repeat_returnsRecurringTaskCommand() throws SioetException {
        assertInstanceOf(
                RecurringTaskCommand.class,
                Parser.parse("repeat project meeting /week")
        );
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        assertThrows(
                SioetException.class, () -> Parser.parse("unknown command"));
    }

    @Test
    public void parseDateTime_validDate_returnsCorrectDateTime()
            throws SioetException {
        LocalDateTime result = Parser.parseDateTime("6/8/2026 1400");

        assertEquals(
                LocalDateTime.of(2026, 8, 6, 14, 0),
                result
        );
    }

    @Test
    public void parseDateTime_invalidDate_throwsException() {
        assertThrows(
                SioetException.class, () -> Parser.parseDateTime("invalid date"));
    }
}
