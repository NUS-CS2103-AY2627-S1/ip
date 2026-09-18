package chud.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import chud.command.AddDeadlineCommand;
import chud.command.AddEventCommand;
import chud.command.AddTodoCommand;
import chud.command.Command;
import chud.command.DeleteCommand;
import chud.command.ExitCommand;
import chud.command.FindCommand;
import chud.command.ListCommand;
import chud.command.MarkCommand;
import chud.command.OnCommand;
import chud.command.UnknownCommand;
import chud.command.UnmarkCommand;
import chud.exception.ChudException;
import chud.task.SortDirection;
import chud.task.SortKey;

class ParserTest {
    // ---- parseCommandWord / parseArguments ----

    @Test
    void parseCommandWord_wordWithArguments_returnsWordBeforeFirstSpace() {
        assertEquals("todo", Parser.parseCommandWord("todo borrow book"));
    }

    @Test
    void parseCommandWord_noArguments_returnsWholeInput() {
        assertEquals("list", Parser.parseCommandWord("list"));
    }

    @Test
    void parseArguments_wordWithArguments_returnsTextAfterFirstSpaceTrimmed() {
        assertEquals("borrow book", Parser.parseArguments("todo   borrow book"));
    }

    @Test
    void parseArguments_noArguments_returnsEmptyString() {
        assertEquals("", Parser.parseArguments("list"));
    }

    // ---- parseTaskIndex ----

    @Test
    void parseTaskIndex_validNumberWithinRange_returnsZeroBasedIndex() throws ChudException {
        assertEquals(0, Parser.parseTaskIndex("1", 3, "mark"));
        assertEquals(2, Parser.parseTaskIndex("3", 3, "mark"));
    }

    @Test
    void parseTaskIndex_emptyArguments_exceptionThrown() {
        ChudException exception = assertThrows(ChudException.class, () ->
                Parser.parseTaskIndex("", 3, "mark"));

        assertTrue(exception.getMessage().contains("mark"));
    }

    @Test
    void parseTaskIndex_nonNumeric_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseTaskIndex("abc", 3, "mark"));
    }

    @Test
    void parseTaskIndex_zero_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseTaskIndex("0", 3, "mark"));
    }

    @Test
    void parseTaskIndex_negative_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseTaskIndex("-1", 3, "mark"));
    }

    @Test
    void parseTaskIndex_greaterThanTaskCount_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseTaskIndex("4", 3, "mark"));
    }

    @Test
    void parseTaskIndex_emptyTaskList_exceptionMentionsEmptyList() {
        ChudException exception = assertThrows(ChudException.class, () ->
                Parser.parseTaskIndex("1", 0, "mark"));

        assertTrue(exception.getMessage().contains("empty"));
    }

    // ---- parseTodoDescription ----

    @Test
    void parseTodoDescription_nonEmpty_returnsArgumentsUnchanged() throws ChudException {
        assertEquals("borrow book", Parser.parseTodoDescription("borrow book"));
    }

    @Test
    void parseTodoDescription_empty_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseTodoDescription(""));
    }

    // ---- parseDeadlineArgs ----

    @Test
    void parseDeadlineArgs_validArguments_returnsDescriptionAndBy() throws ChudException {
        Parser.DeadlineArgs args = Parser.parseDeadlineArgs("return book /by 2019-12-02");

        assertEquals("return book", args.description);
        assertEquals("Dec 02 2019", args.by.toString());
    }

    @Test
    void parseDeadlineArgs_missingByMarker_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseDeadlineArgs("return book"));
    }

    @Test
    void parseDeadlineArgs_emptyDescription_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseDeadlineArgs("/by 2019-12-02"));
    }

    @Test
    void parseDeadlineArgs_emptyByValue_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseDeadlineArgs("return book /by"));
    }

    @Test
    void parseDeadlineArgs_unparsableByValue_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseDeadlineArgs("return book /by not-a-date"));
    }

    // ---- parseEventArgs ----

    @Test
    void parseEventArgs_validArguments_returnsDescriptionFromAndTo() throws ChudException {
        Parser.EventArgs args = Parser.parseEventArgs(
                "project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        assertEquals("project meeting", args.description);
        assertEquals("Dec 02 2019, 2:00PM", args.from.toString());
        assertEquals("Dec 02 2019, 4:00PM", args.to.toString());
    }

    @Test
    void parseEventArgs_missingFromMarker_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseEventArgs("project meeting /to 2019-12-02"));
    }

    @Test
    void parseEventArgs_missingToMarker_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseEventArgs("project meeting /from 2019-12-02"));
    }

    @Test
    void parseEventArgs_toMarkerBeforeFromMarker_exceptionThrown() {
        // The /to marker appearing before /from in the text is rejected on its own, regardless
        // of what dates follow -- this is a textual-order check, distinct from the chronological
        // check below.
        assertThrows(ChudException.class, () ->
                Parser.parseEventArgs("project meeting /to 2019-12-02 /from 2019-12-03"));
    }

    @Test
    void parseEventArgs_toDateChronologicallyBeforeFromDate_exceptionThrown() {
        // Markers are in the right textual order (/from before /to), but the /to date is
        // earlier than the /from date -- this must still be rejected.
        ChudException exception = assertThrows(ChudException.class, () ->
                Parser.parseEventArgs("project meeting /from 2026-09-22 /to 2026-09-21"));

        assertTrue(exception.getMessage().contains("can't be before"));
    }

    @Test
    void parseEventArgs_toDateSameAsFromDate_doesNotThrow() throws ChudException {
        // An event starting and ending at the same instant is a degenerate case, not a reversed
        // one -- only a /to strictly before /from should be rejected.
        Parser.EventArgs args = Parser.parseEventArgs(
                "project meeting /from 2026-09-22 0900 /to 2026-09-22 0900");

        assertEquals("Sep 22 2026, 9:00AM", args.from.toString());
        assertEquals("Sep 22 2026, 9:00AM", args.to.toString());
    }

    @Test
    void parseEventArgs_emptyDescription_exceptionThrown() {
        assertThrows(ChudException.class, () ->
                Parser.parseEventArgs("/from 2019-12-02 /to 2019-12-03"));
    }

    @Test
    void parseEventArgs_emptyFromOrToValue_exceptionThrown() {
        assertThrows(ChudException.class, () ->
                Parser.parseEventArgs("project meeting /from /to 2019-12-03"));
    }

    @Test
    void parseEventArgs_unparsableDateValue_exceptionThrown() {
        assertThrows(ChudException.class, () ->
                Parser.parseEventArgs("project meeting /from not-a-date /to 2019-12-03"));
    }

    // ---- parseOnDate ----

    @Test
    void parseOnDate_validDate_returnsTaskDateTime() throws ChudException {
        assertEquals("Dec 02 2019", Parser.parseOnDate("2019-12-02").toString());
    }

    @Test
    void parseOnDate_emptyArguments_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseOnDate(""));
    }

    @Test
    void parseOnDate_unparsableDate_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseOnDate("not-a-date"));
    }

    // ---- parseFindKeyword ----

    @Test
    void parseFindKeyword_nonEmpty_returnsArgumentsUnchanged() throws ChudException {
        assertEquals("book", Parser.parseFindKeyword("book"));
    }

    @Test
    void parseFindKeyword_empty_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseFindKeyword(""));
    }

    // ---- parseListSortArgs ----

    @Test
    void parseListSortArgs_emptyArguments_returnsNull() throws ChudException {
        assertNull(Parser.parseListSortArgs(""));
    }

    @Test
    void parseListSortArgs_keyOnly_defaultsToAscending() throws ChudException {
        Parser.ListSortArgs args = Parser.parseListSortArgs("/sort date");

        assertEquals(SortKey.DATE, args.key);
        assertEquals(SortDirection.ASC, args.direction);
    }

    @Test
    void parseListSortArgs_keyAndDirection_returnsBoth() throws ChudException {
        Parser.ListSortArgs args = Parser.parseListSortArgs("/sort date desc");

        assertEquals(SortKey.DATE, args.key);
        assertEquals(SortDirection.DESC, args.direction);
    }

    @Test
    void parseListSortArgs_everyRecognizedKey_isAccepted() throws ChudException {
        assertEquals(SortKey.DATE, Parser.parseListSortArgs("/sort date").key);
        assertEquals(SortKey.DESCRIPTION, Parser.parseListSortArgs("/sort description").key);
        assertEquals(SortKey.TYPE, Parser.parseListSortArgs("/sort type").key);
        assertEquals(SortKey.DONE, Parser.parseListSortArgs("/sort done").key);
    }

    @Test
    void parseListSortArgs_missingSortMarker_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseListSortArgs("date"));
    }

    @Test
    void parseListSortArgs_unrecognizedKey_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseListSortArgs("/sort priority"));
    }

    @Test
    void parseListSortArgs_unrecognizedDirection_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseListSortArgs("/sort date sideways"));
    }

    @Test
    void parseListSortArgs_tooManyArguments_exceptionThrown() {
        assertThrows(ChudException.class, () -> Parser.parseListSortArgs("/sort date asc extra"));
    }

    // ---- parse (dispatch) ----

    @Test
    void parse_everyRecognizedCommandWord_returnsMatchingCommandType() {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(AddTodoCommand.class, Parser.parse("todo borrow book"));
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(AddEventCommand.class,
                Parser.parse("event project meeting /from 2019-12-02 /to 2019-12-03"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    void parse_unrecognizedCommandWord_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, Parser.parse("frobnicate"));
    }

    @Test
    void parse_exitCommand_isExitReturnsTrue() {
        Command command = Parser.parse("bye");

        assertTrue(command.isExit());
    }

    @Test
    void parse_nonExitCommand_isExitReturnsFalse() {
        Command command = Parser.parse("list");

        assertFalse(command.isExit());
    }
}
