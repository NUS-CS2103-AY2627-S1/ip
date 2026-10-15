package summer.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import summer.SummerException;
import summer.command.AddCommand;
import summer.command.DeleteCommand;
import summer.command.ExitCommand;
import summer.command.FindCommand;
import summer.command.ListCommand;
import summer.command.MarkCommand;
import summer.command.OnDateCommand;
import summer.command.SortCommand;
import summer.command.UnmarkCommand;

/**
 * Tests for {@link Parser}.
 *
 * <p>{@code createTask} holds the bulk of the logic (keyword dispatch, argument
 * splitting, date validation) and is tested exhaustively. {@code parse} is a
 * router, so it is checked for correct command selection and for the one input
 * class it can reject outright (a bad {@code on} date).
 */
public class ParserTest {

    // ---------- createTask: todo ----------

    @Test
    public void createTask_todo_returnsTodoWithDescription() throws SummerException {
        assertEquals("[T][ ] read book", Parser.createTask("todo read book").toString());
    }

    @Test
    public void createTask_todo_trimsSurroundingWhitespace() throws SummerException {
        assertEquals("[T][ ] read book", Parser.createTask("todo    read book   ").toString());
    }

    @Test
    public void createTask_todoWithoutDescription_throws() {
        assertThrows(SummerException.class, () -> Parser.createTask("todo"));
        assertThrows(SummerException.class, () -> Parser.createTask("todo    "));
    }

    @Test
    public void createTask_keywordWrongCase_throws() {
        assertThrows(SummerException.class, () -> Parser.createTask("TODO read book"));
    }

    // ---------- createTask: deadline ----------

    @Test
    public void createTask_deadline_returnsDeadlineWithFormattedDate() throws SummerException {
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                Parser.createTask("deadline return book /by 2019-10-15").toString());
    }

    @Test
    public void createTask_deadlineMissingByMarker_throws() {
        assertThrows(SummerException.class, () -> Parser.createTask("deadline return book"));
        assertThrows(SummerException.class, () -> Parser.createTask("deadline"));
    }

    @Test
    public void createTask_deadlineInvalidDate_throws() {
        assertThrows(SummerException.class, () -> Parser.createTask("deadline x /by tomorrow"));
        assertThrows(SummerException.class, () -> Parser.createTask("deadline x /by 2019-13-40"));
    }

    // ---------- createTask: event ----------

    @Test
    public void createTask_event_returnsEventWithFormattedRange() throws SummerException {
        assertEquals("[E][ ] camp (from: Oct 15 2019 to: Oct 17 2019)",
                Parser.createTask("event camp /from 2019-10-15 /to 2019-10-17").toString());
    }

    @Test
    public void createTask_eventMissingMarker_throws() {
        assertThrows(SummerException.class, () -> Parser.createTask("event camp /from 2019-10-15"));
        assertThrows(SummerException.class, () -> Parser.createTask("event camp /to 2019-10-17"));
    }

    @Test
    public void createTask_eventFromMarkerAfterToMarker_throws() {
        assertThrows(SummerException.class, () ->
                Parser.createTask("event camp /to 2019-10-17 /from 2019-10-15"));
    }

    @Test
    public void createTask_eventInvalidDate_throws() {
        assertThrows(SummerException.class, () ->
                Parser.createTask("event camp /from 2019-10-15 /to someday"));
    }

    // ---------- createTask: unknown ----------

    @Test
    public void createTask_unknownKeyword_throws() {
        assertThrows(SummerException.class, () -> Parser.createTask("blah blah"));
        assertThrows(SummerException.class, () -> Parser.createTask("todos"));
    }

    // ---------- parse: command routing ----------

    @Test
    public void parse_bye_returnsExitCommandThatExits() throws SummerException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertTrue(Parser.parse("bye").isExit());
    }

    @Test
    public void parse_list_returnsListCommand() throws SummerException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_sort_returnsSortCommand() throws SummerException {
        assertInstanceOf(SortCommand.class, Parser.parse("sort"));
    }

    @Test
    public void parse_markUnmarkDelete_routeToMatchingCommands() throws SummerException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 3"));
    }

    @Test
    public void parse_markUnmarkDeleteWithNonNumber_throws() {
        assertThrows(SummerException.class, () -> Parser.parse("mark two"));
        assertThrows(SummerException.class, () -> Parser.parse("unmark "));
        assertThrows(SummerException.class, () -> Parser.parse("delete last"));
    }

    @Test
    public void parse_onValidDate_returnsOnDateCommand() throws SummerException {
        assertInstanceOf(OnDateCommand.class, Parser.parse("on 2019-10-15"));
    }

    @Test
    public void parse_onInvalidDate_throws() {
        assertThrows(SummerException.class, () -> Parser.parse("on not-a-date"));
    }

    @Test
    public void parse_unrecognisedInput_fallsThroughToAddCommand() throws SummerException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read"));
        assertInstanceOf(AddCommand.class, Parser.parse("not a real command"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws SummerException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithoutKeyword_throws() {
        assertThrows(SummerException.class, () -> Parser.parse("find "));
    }

    @Test
    public void parse_bareFind_throws() {
        assertThrows(SummerException.class, () -> Parser.parse("find"));
    }
}
