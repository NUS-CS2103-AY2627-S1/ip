package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import command.DeadlineCommand;
import command.DeleteCommand;
import command.EventCommand;
import command.ExitCommand;
import command.FindCommand;
import command.ListCommand;
import command.ListDateCommand;
import command.MarkCommand;
import command.SortCommand;
import command.TodoCommand;
import command.UnmarkCommand;
import exception.CbtException;
import task.Deadline;
import task.Event;
import task.Task;
import task.Todo;

/** Tests conversion of user and storage text into application objects. */
public class ParserTest {
    /** Verifies that known command words produce the expected command types. */
    @Test
    public void parseCommand_validCommands_correctCommandTypes() throws CbtException {
        assertInstanceOf(TodoCommand.class, Parser.parseCommand("todo read book"));
        assertInstanceOf(TodoCommand.class, Parser.parseCommand("  todo   read   book  "));
        assertInstanceOf(DeadlineCommand.class, Parser.parseCommand("deadline submit /by 2/12/2026"));
        assertInstanceOf(EventCommand.class,
                Parser.parseCommand("event meeting /from 2/12/2026 0900 /to 2/12/2026 1000"));
        assertInstanceOf(ListDateCommand.class, Parser.parseCommand("date 2019-12-02"));
        assertInstanceOf(ListDateCommand.class, Parser.parseCommand("listdate 2019-12-02"));
        assertInstanceOf(ListCommand.class, Parser.parseCommand("LIST"));
        assertInstanceOf(FindCommand.class, Parser.parseCommand("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parseCommand("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parseCommand("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parseCommand("delete 1"));
        assertInstanceOf(SortCommand.class, Parser.parseCommand("sort date"));
        assertInstanceOf(ExitCommand.class, Parser.parseCommand("bye"));
    }

    /** Verifies that an unknown command word is rejected. */
    @Test
    public void parseCommand_unknownCommand_exceptionThrown() {
        assertThrows(CbtException.class, () -> Parser.parseCommand("dance"));
    }

    /** Verifies that blank input and unexpected arguments for argument-free commands are rejected. */
    @Test
    public void parseCommand_blankOrUnexpectedArguments_exceptionThrown() {
        assertThrows(CbtException.class, () -> Parser.parseCommand("   "));
        assertThrows(CbtException.class, () -> Parser.parseCommand(null));
        assertThrows(CbtException.class, () -> Parser.parseCommand("list extra"));
        assertThrows(CbtException.class, () -> Parser.parseCommand("bye now"));
    }

    /** Verifies parsing of each supported date-time format. */
    @Test
    public void parseLineToDate_supportedDateTimeFormats_correctDateTime() throws CbtException {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(expected, Parser.parseLineToDate("2/12/2019 1800"));
        assertEquals(expected, Parser.parseLineToDate("2-12-2019 1800"));
        assertEquals(expected, Parser.parseLineToDate("2-12-2019 18:00"));
        assertEquals(expected, Parser.parseLineToDate("2019-12-02 1800"));
        assertEquals(expected, Parser.parseLineToDate("2/12/2019 18:00"));
        assertEquals(expected, Parser.parseLineToDate("2019-12-02 18:00"));
        assertEquals(expected, Parser.parseLineToDate(" 2019-12-02 18:00 "));
    }

    /** Verifies that a date without a time is interpreted as midnight. */
    @Test
    public void parseLineToDate_dateWithoutTime_midnightUsed() throws CbtException {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 0, 0);

        assertEquals(expected, Parser.parseLineToDate("2/12/2019"));
        assertEquals(expected, Parser.parseLineToDate("2-12-2019"));
        assertEquals(expected, Parser.parseLineToDate("2019-12-02"));
    }

    /** Verifies that malformed and impossible dates are rejected. */
    @Test
    public void parseLineToDate_invalidAndImpossibleDates_exceptionThrown() {
        assertThrows(CbtException.class, () -> Parser.parseLineToDate("not a date"));
        assertThrows(CbtException.class, () -> Parser.parseLineToDate("31/02/2025"));
    }

    /** Verifies that the date-only parser rejects input containing a time. */
    @Test
    public void parseDate_dateTimeProvided_exceptionThrown() {
        assertThrows(CbtException.class, () -> Parser.parseDate("2019-12-02 1800"));
    }

    /** Verifies that every supported date-only format is parsed strictly after trimming. */
    @Test
    public void parseDate_supportedFormats_correctDateReturned() throws CbtException {
        LocalDate expected = LocalDate.of(2024, 2, 29);

        assertEquals(expected, Parser.parseDate("29/2/2024"));
        assertEquals(expected, Parser.parseDate("29-2-2024"));
        assertEquals(expected, Parser.parseDate(" 2024-02-29 "));
    }

    /** Verifies reconstruction of a completed todo whose description contains a divider. */
    @Test
    public void parseLineToTask_completedTodoWithDivider_contentAndStatusRestored() {
        Task task = Parser.parseLineToTask("TODO | 1 | read | chapter one");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][X] read | chapter one", task.toString());
        assertEquals("TODO | 1 | read | chapter one", task.toFileFormat());
    }

    /** Verifies that malformed and unknown stored records return {@code null}. */
    @Test
    public void parseLineToTask_malformedOrUnknownRecord_nullReturned() {
        assertNull(Parser.parseLineToTask("missing fields"));
        assertNull(Parser.parseLineToTask("REMINDER | 0 | call Alex"));
        assertNull(Parser.parseLineToTask("TODO | maybe | call Alex"));
        assertNull(Parser.parseLineToTask("TODO | 0 |   "));
    }

    /** Verifies reconstruction of stored deadlines and events, including completion state. */
    @Test
    public void parseLineToTask_validDatedRecords_tasksAndStatusRestored() {
        Task deadline = Parser.parseLineToTask("deadline | 0 | submit | report | 02/12/2026 1800");
        Task event = Parser.parseLineToTask(
                "EVENT | 1 | project | meeting | 02/12/2026 0900 | 02/12/2026 1000");

        assertInstanceOf(Deadline.class, deadline);
        assertEquals("DEADLINE | 0 | submit | report | 02/12/2026 1800", deadline.toFileFormat());
        assertInstanceOf(Event.class, event);
        assertEquals("EVENT | 1 | project | meeting | 02/12/2026 0900 | 02/12/2026 1000",
                event.toFileFormat());
    }

    /** Verifies that invalid stored date fields and event ranges are rejected. */
    @Test
    public void parseLineToTask_invalidDatedRecords_nullReturned() {
        assertNull(Parser.parseLineToTask("DEADLINE | 0 | submit | not-a-date"));
        assertNull(Parser.parseLineToTask("EVENT | 0 | meeting | bad-start | 02/12/2026 1000"));
        assertNull(Parser.parseLineToTask(
                "EVENT | 0 | meeting | 02/12/2026 1100 | 02/12/2026 1000"));
    }

    /** Verifies that dates use the expected user-facing display format. */
    @Test
    public void formatDate_validDate_userFacingFormatReturned() {
        assertEquals("Dec 02 2019", Parser.formatDate(LocalDate.of(2019, 12, 2)));
    }
}
