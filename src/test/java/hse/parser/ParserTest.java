package hse.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import hse.command.ByeCommand;
import hse.command.DeadlineCommand;
import hse.command.DeleteCommand;
import hse.command.EventCommand;
import hse.command.FindCommand;
import hse.command.FindDateCommand;
import hse.command.InvalidCommand;
import hse.command.ListCommand;
import hse.command.MarkCommand;
import hse.command.SortCommand;
import hse.command.TodoCommand;
import hse.command.UnmarkCommand;

class ParserTest {
    @Test
    void parse_findDateCommand_createsFindDateCommand() {
        assertInstanceOf(FindDateCommand.class, Parser.parse("finddate 3/9/2026"));
    }

    @Test
    void parse_sort_createsSortCommand() {
        assertInstanceOf(SortCommand.class, Parser.parse("sort"));
    }

    @Test
    void parse_supportedCommands_createsExpectedCommandTypes() {
        assertInstanceOf(ByeCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline read book /by 2026-09-20"));
        assertInstanceOf(EventCommand.class, Parser.parse("event meeting /from 2026-09-20 /to 2026-09-21"));
        assertInstanceOf(InvalidCommand.class, Parser.parse("unknown"));
    }

    @Test
    void parseDate_supportedFormats_returnsSameDate() {
        LocalDate expected = LocalDate.of(2026, 9, 3);

        assertEquals(expected, Parser.parseDate("2026-9-3"));
        assertEquals(expected, Parser.parseDate("3/9/2026"));
        assertEquals(expected, Parser.parseDate("3-9-2026"));
    }

    @Test
    void parseDateTime_dateAndDateTimeFormats_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2026, 9, 3, 14, 30), Parser.parseDateTime("2026-9-3 1430"));
        assertEquals(LocalDateTime.of(2026, 9, 3, 14, 30), Parser.parseDateTime("3/9/2026 1430"));
        assertEquals(LocalDateTime.of(2026, 9, 3, 14, 30), Parser.parseDateTime("3-9-2026 1430"));
        assertEquals(LocalDateTime.of(2026, 9, 3, 0, 0), Parser.parseDateTime("3/9/2026"));
    }

    @Test
    void parseDate_invalidDate_throwsDateTimeParseException() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> Parser.parseDate("30/2/2026"));
    }

    @Test
    void getIndex_firstAndLastTask_returnsZeroBasedIndexes() {
        assertEquals(0, Parser.getIndex("mark 1", 3));
        assertEquals(2, Parser.getIndex("delete 3", 3));
        assertEquals(0, Parser.getIndex("mark   1", 3));
    }

    @Test
    void getIndex_zeroOrPastLastTask_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> Parser.getIndex("mark 0", 3));
        assertThrows(IndexOutOfBoundsException.class, () -> Parser.getIndex("delete 4", 3));
    }

    @Test
    void getIndex_nonNumericTaskNumber_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.getIndex("mark one", 3));
    }
}
