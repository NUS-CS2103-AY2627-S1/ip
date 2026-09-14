package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.DeleteCommand;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.HelpCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.OnCommand;
import nova.command.SortCommand;

public class ParserTest {

    @Test
    public void parse_bye_returnsExitCommandThatStopsTheLoop() throws NovaException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_returnsListCommandThatDoesNotStopTheLoop() throws NovaException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_todoWithDescription_returnsAddCommand() throws NovaException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("todo"));
        assertEquals("A todo needs a description. Try: todo read book", e.getMessage());

        assertThrows(NovaException.class, () -> Parser.parse("todo    "));
    }

    @Test
    public void parse_wellFormedDeadline_returnsAddCommand() throws NovaException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02 1800"));
    }

    @Test
    public void parse_deadlineWithoutBy_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("deadline return book"));
        assertEquals("Missing /by. A deadline needs a description and a /by date. "
                + "Try: deadline return book /by 2019-12-02", e.getMessage());
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertThrows(NovaException.class, () -> Parser.parse("deadline  /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithUnreadableDate_exceptionThrown() {
        assertThrows(NovaException.class, () -> Parser.parse("deadline return book /by someday"));
    }

    @Test
    public void parse_wellFormedEvent_returnsAddCommand() throws NovaException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2019-08-06 1400 /to 2019-08-06 1600"));
    }

    @Test
    public void parse_eventMissingTo_exceptionThrown() {
        NovaException e = assertThrows(
                NovaException.class, () -> Parser.parse("event meeting /from 2019-08-06 1400"));
        assertEquals("Missing /to. Try: event trip /from 2019-08-06 /to 2019-08-08",
                e.getMessage());
    }

    @Test
    public void parse_markAndUnmarkAndDelete_returnTheMatchingCommands() throws NovaException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("mark"));
        assertEquals("mark needs a task number. Try: mark 1", e.getMessage());

        NovaException f = assertThrows(NovaException.class, () -> Parser.parse("delete"));
        assertEquals("delete needs a task number. Try: delete 1", f.getMessage());
    }

    @Test
    public void parse_taskNumberNotANumber_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("mark abc"));
        assertEquals("\"abc\" is not a number. Try: mark 1", e.getMessage());
    }

    @Test
    public void parse_onWithDate_returnsOnCommand() throws NovaException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
    }

    @Test
    public void parse_onWithoutDate_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("on"));
        assertEquals("on needs a date. Try: on 2019-12-02", e.getMessage());
    }

    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws NovaException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("find"));
        assertEquals("find needs a keyword. Try: find book", e.getMessage());
    }

    @Test
    public void parse_sortAlone_returnsSortCommand() throws NovaException {
        assertInstanceOf(SortCommand.class, Parser.parse("sort"));
    }

    @Test
    public void parse_sortWithArguments_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("sort by date"));
        assertEquals("sort takes no arguments. Just type: sort", e.getMessage());
    }

    @Test
    public void parse_eventEndingBeforeItStarts_exceptionThrown() {
        NovaException e = assertThrows(
                NovaException.class, () -> Parser.parse("event trip /from 2019-12-31 /to 2019-01-01"));
        assertTrue(e.getMessage().contains("cannot end before it starts"));
    }

    @Test
    public void parse_eventStartingAndEndingAtSameMoment_accepted() throws NovaException {
        assertInstanceOf(
                AddCommand.class, Parser.parse("event standup /from 2019-08-06 0900 /to 2019-08-06 0900"));
    }

    @Test
    public void parse_markerGivenTwice_exceptionThrown() {
        NovaException byTwice = assertThrows(
                NovaException.class, () -> Parser.parse("deadline x /by 2019-01-01 /by 2019-02-02"));
        assertTrue(byTwice.getMessage().contains("more than once"));

        String repeatedFrom = "event x /from 2019-01-01 /from 2019-01-02 /to 2019-01-03";
        NovaException fromTwice = assertThrows(NovaException.class, () -> Parser.parse(repeatedFrom));
        assertTrue(fromTwice.getMessage().contains("more than once"));
    }

    @Test
    public void parse_eventMarkersOutOfOrder_exceptionThrown() {
        NovaException e = assertThrows(
                NovaException.class, () -> Parser.parse("event x /to 2019-01-02 /from 2019-01-01"));
        assertTrue(e.getMessage().contains("/from must come before /to"));
    }

    @Test
    public void parse_markerWithNothingAfterIt_exceptionThrown() {
        assertThrows(NovaException.class, () -> Parser.parse("deadline x /by "));
        assertThrows(NovaException.class, () -> Parser.parse("event x /from  /to 2019-01-01"));
        assertThrows(NovaException.class, () -> Parser.parse("event x /from 2019-01-01 /to "));
    }

    @Test
    public void parse_descriptionMissingBeforeMarker_exceptionThrown() {
        assertThrows(NovaException.class, () -> Parser.parse("deadline /by 2019-01-01"));
        assertThrows(
                NovaException.class, () -> Parser.parse("event /from 2019-01-01 /to 2019-01-02"));
    }

    @Test
    public void parse_help_returnsHelpCommand() throws NovaException {
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
    }

    @Test
    public void parse_unknownKeyword_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("blah"));
        assertEquals("I don't know that command. Type help to see what I understand.", e.getMessage());
    }
}
