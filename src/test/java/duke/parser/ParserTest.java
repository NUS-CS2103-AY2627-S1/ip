package duke.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import duke.command.AddCommand;
import duke.command.AddPlaceCommand;
import duke.command.Command;
import duke.command.DeleteCommand;
import duke.command.DeletePlaceCommand;
import duke.command.ExitCommand;
import duke.command.FindCommand;
import duke.command.FindPlaceCommand;
import duke.command.ListCommand;
import duke.command.ListPlacesCommand;
import duke.command.MarkCommand;
import duke.command.UndoCommand;
import duke.command.UnmarkCommand;
import duke.exception.DukeException;

/**
 * Unit tests for the {@link Parser} class.
 */
public class ParserTest {

    @Test
    public void parse_byeCommand_returnsExitCommand() throws DukeException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws DukeException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parse_validTodoCommand_returnsAddCommand() throws DukeException {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_emptyTodoDescription_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("todo"));
        assertEquals("OOPS!!! The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_validDeadlineCommand_returnsAddCommand() throws DukeException {
        Command command = Parser.parse("deadline return book /by 2026-09-01 1800");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_deadlineMissingByKeyword_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("deadline return book"));
        assertEquals("OOPS!!! The description and /by time of a deadline cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_deadlineMissingByTime_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("deadline return book /by "));
        assertEquals("OOPS!!! The description and /by time of a deadline cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_validEventCommand_returnsAddCommand() throws DukeException {
        Command command = Parser.parse("event team meeting /from 2026-09-01 1400 /to 2026-09-01 1600");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parse_eventMissingToKeyword_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () ->
                Parser.parse("event team meeting /from 2026-09-01 1400"));
        assertEquals("OOPS!!! The /to time of an event cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_validMarkCommand_returnsMarkCommand() throws DukeException {
        Command command = Parser.parse("mark 2");
        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_markNonNumericIndex_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("mark abc"));
        assertEquals("OOPS!!! The task number provided is invalid.", exception.getMessage());
    }

    @Test
    public void parse_markMissingIndex_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("mark"));
        assertEquals("OOPS!!! The task number cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_validUnmarkCommand_returnsUnmarkCommand() throws DukeException {
        Command command = Parser.parse("unmark 3");
        assertInstanceOf(UnmarkCommand.class, command);
    }

    @Test
    public void parse_validDeleteCommand_returnsDeleteCommand() throws DukeException {
        Command command = Parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, command);
    }

    @Test
    public void parse_validFindCommand_returnsFindCommand() throws DukeException {
        Command command = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_emptyFindKeyword_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("find"));
        assertEquals("OOPS!!! The search keyword cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("   "));
        assertEquals("OOPS!!! Command cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse("invalidCommand"));
        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-(", exception.getMessage());
    }

    @Test
    public void parse_undoCommand_returnsUndoCommand() throws DukeException {
        Command command = Parser.parse("undo");
        assertInstanceOf(UndoCommand.class, command);
    }

    @Test
    public void parse_validPlaceCommand_returnsAddPlaceCommand() throws DukeException {
        Command command = Parser.parse("place Sentosa");
        assertInstanceOf(AddPlaceCommand.class, command);
    }

    @Test
    public void parse_placeCommandWithDetails_returnsAddPlaceCommand() throws DukeException {
        Command command = Parser.parse("place Jumbo Seafood /details Riverside Point");
        assertInstanceOf(AddPlaceCommand.class, command);
    }

    @Test
    public void parse_emptyPlaceName_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () -> Parser.parse("place"));
        assertEquals("OOPS!!! The name of a place cannot be empty.", ex.getMessage());
    }

    @Test
    public void parse_placesCommand_returnsListPlacesCommand() throws DukeException {
        Command command = Parser.parse("places");
        assertInstanceOf(ListPlacesCommand.class, command);
    }

    @Test
    public void parse_deletePlaceCommand_returnsDeletePlaceCommand() throws DukeException {
        Command command = Parser.parse("deleteplace 1");
        assertInstanceOf(DeletePlaceCommand.class, command);
    }

    @Test
    public void parse_deletePlaceMissingIndex_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () -> Parser.parse("deleteplace"));
        assertEquals("OOPS!!! The place number cannot be empty.", ex.getMessage());
    }

    @Test
    public void parse_findPlaceCommand_returnsFindPlaceCommand() throws DukeException {
        Command command = Parser.parse("findplace seafood");
        assertInstanceOf(FindPlaceCommand.class, command);
    }

    @Test
    public void parse_findPlaceMissingKeyword_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () -> Parser.parse("findplace"));
        assertEquals("OOPS!!! The search keyword cannot be empty.", ex.getMessage());
    }

    @Test
    public void parse_taskIndexZeroOrNegative_exceptionThrown() {
        DukeException ex1 = assertThrows(DukeException.class, () -> Parser.parse("mark 0"));
        assertEquals("OOPS!!! The task number must be greater than 0.", ex1.getMessage());

        DukeException ex2 = assertThrows(DukeException.class, () -> Parser.parse("delete -1"));
        assertEquals("OOPS!!! The task number must be greater than 0.", ex2.getMessage());
    }

    @Test
    public void parse_taskIndexTrailingTokens_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () -> Parser.parse("mark 1 2"));
        assertEquals("OOPS!!! Please provide only a single task number.", ex.getMessage());
    }

    @Test
    public void parse_placeIndexZeroOrNegative_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () -> Parser.parse("deleteplace 0"));
        assertEquals("OOPS!!! The place number must be greater than 0.", ex.getMessage());
    }

    @Test
    public void parse_placeIndexTrailingTokens_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () -> Parser.parse("deleteplace 1 extra"));
        assertEquals("OOPS!!! Please provide only a single place number.", ex.getMessage());
    }

    @Test
    public void parse_pipeInInput_exceptionThrown() {
        DukeException ex1 = assertThrows(DukeException.class, () -> Parser.parse("todo read | book"));
        assertTrue(ex1.getMessage().contains("reserved and cannot be used"));

        DukeException ex2 = assertThrows(DukeException.class, () ->
                Parser.parse("deadline submit | report /by 2026-09-01 1800"));
        assertTrue(ex2.getMessage().contains("reserved and cannot be used"));

        DukeException ex3 = assertThrows(DukeException.class, () ->
                Parser.parse("place Library | Central /details 3rd floor"));
        assertTrue(ex3.getMessage().contains("reserved and cannot be used"));

        DukeException ex4 = assertThrows(DukeException.class, () -> Parser.parse("find test|pipe"));
        assertTrue(ex4.getMessage().contains("reserved and cannot be used"));
    }

    @Test
    public void parse_duplicateDeadlineFlags_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () ->
                Parser.parse("deadline return book /by 2026-09-01 1800 /by 2026-09-02 1800"));
        assertEquals("OOPS!!! Multiple /by parameters are not allowed.", ex.getMessage());
    }

    @Test
    public void parse_duplicateEventFlags_exceptionThrown() {
        DukeException ex1 = assertThrows(DukeException.class, () ->
                Parser.parse("event trip /from 2026-09-01 1000 /from 2026-09-01 1100 /to 2026-09-01 1200"));
        assertEquals("OOPS!!! Multiple /from parameters are not allowed.", ex1.getMessage());

        DukeException ex2 = assertThrows(DukeException.class, () ->
                Parser.parse("event trip /from 2026-09-01 1000 /to 2026-09-01 1200 /to 2026-09-01 1300"));
        assertEquals("OOPS!!! Multiple /to parameters are not allowed.", ex2.getMessage());
    }

    @Test
    public void parse_eventOrderReversed_exceptionThrown() {
        DukeException ex = assertThrows(DukeException.class, () ->
                Parser.parse("event concert /to 2026-09-01 2200 /from 2026-09-01 1900"));
        assertEquals("OOPS!!! The /from parameter must appear before the /to parameter.", ex.getMessage());
    }

    @Test
    public void parse_placeMultipleOrConflictingDetailsFlags_exceptionThrown() {
        DukeException ex1 = assertThrows(DukeException.class, () ->
                Parser.parse("place Park /details Area 1 /details Area 2"));
        assertEquals("OOPS!!! Multiple /details parameters are not allowed.", ex1.getMessage());

        DukeException ex2 = assertThrows(DukeException.class, () ->
                Parser.parse("place Park /desc Area 1 /desc Area 2"));
        assertEquals("OOPS!!! Multiple /desc parameters are not allowed.", ex2.getMessage());

        DukeException ex3 = assertThrows(DukeException.class, () ->
                Parser.parse("place Park /details Area 1 /desc Area 2"));
        assertEquals("OOPS!!! Cannot specify both /details and /desc.", ex3.getMessage());
    }

    @Test
    public void parse_nullCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parse(null));
    }
}
