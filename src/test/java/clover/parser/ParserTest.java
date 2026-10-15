package clover.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import clover.command.AddTutoreeCommand;
import clover.command.DeadlineCommand;
import clover.command.DeleteCommand;
import clover.command.EventCommand;
import clover.command.ExitCommand;
import clover.command.FindCommand;
import clover.command.FindTutoreeCommand;
import clover.command.HelpCommand;
import clover.command.ListCommand;
import clover.command.ListTutoreesCommand;
import clover.command.MarkCommand;
import clover.command.ToDoCommand;
import clover.command.UnmarkCommand;
import clover.exception.CloverException;

/** Tests parsing of user input into Clover commands and values. */
class ParserTest {

    @Test
    void parse_knownCommandWords_correctCommandTypesReturned() throws CloverException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(ToDoCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline submit /by 2026-09-01"));
        assertInstanceOf(EventCommand.class, Parser.parse("event meeting /from 2026-09-01 /to 2026-09-02"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(AddTutoreeCommand.class, Parser.parse("add-tutoree Alice /address Home /fee 50"));
        assertInstanceOf(ListTutoreesCommand.class, Parser.parse("list-tutorees"));
        assertInstanceOf(FindTutoreeCommand.class, Parser.parse("find-tutoree Alice"));
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    void parse_commandWordWithDifferentCapitalisation_correctCommandTypeReturned() throws CloverException {
        assertInstanceOf(ToDoCommand.class, Parser.parse("TODO read book"));
    }

    @Test
    void parse_unknownCommandWord_exceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () -> Parser.parse("read book"));

        assertEquals("That command is not a forest path I know. Type \"help\" to view the supported commands.",
                exception.getMessage());
    }

    @Test
    void parse_blankInput_exceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () -> Parser.parse("   "));

        assertEquals("The grove needs a command or a quest description.", exception.getMessage());
    }

    @Test
    void parse_noArgumentCommandWithArguments_exceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () -> Parser.parse("list tasks"));

        assertEquals("The list command does not take any additional text.", exception.getMessage());
    }

    @Test
    void findSingleMarker_duplicateMarker_negativeOneReturned() {
        assertEquals(-1, Parser.findSingleMarker("work /by 2026-09-01 /by 2026-09-02", "/by"));
    }

    @Test
    void findSingleMarker_singleAbsentAndEmbeddedMarkers_returnsExpectedIndex() {
        assertEquals(5, Parser.findSingleMarker("work /by 2026-09-01", "/by"));
        assertEquals(-1, Parser.findSingleMarker("work /before tomorrow", "/by"));
        assertEquals(-1, Parser.findSingleMarker("work /bygone tomorrow", "/by"));
        assertEquals(5, Parser.findSingleMarker("work /by", "/by"));
    }

    @Test
    void rejectUnknownParameters_unknownParameter_exceptionIdentifiesParameter() {
        CloverException exception = assertThrows(CloverException.class, () ->
                Parser.rejectUnknownParameters("finish notes /due Friday", "/by", "/for"));

        assertEquals("Unknown parameter \"/due\" for this command.", exception.getMessage());
    }

    @Test
    void rejectUnknownParameters_allowedAndNonParameterSlashText_noExceptionThrown() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Parser.rejectUnknownParameters("read /for Alice at /tmp/file", "/for"));
    }

    @Test
    void isValidTaskNumber_firstAndLastTaskNumber_trueReturned() {
        assertTrue(Parser.isValidTaskNumber("1", 3));
        assertTrue(Parser.isValidTaskNumber("3", 3));
    }

    @Test
    void isValidTaskNumber_numberWithSurroundingWhitespace_trueReturned() {
        assertTrue(Parser.isValidTaskNumber(" 2 ", 3));
    }

    @Test
    void isValidTaskNumber_zeroOrNegativeNumber_falseReturned() {
        assertFalse(Parser.isValidTaskNumber("0", 3));
        assertFalse(Parser.isValidTaskNumber("-1", 3));
    }

    @Test
    void isValidTaskNumber_numberGreaterThanTaskCount_falseReturned() {
        assertFalse(Parser.isValidTaskNumber("4", 3));
        assertFalse(Parser.isValidTaskNumber("1", 0));
    }

    @Test
    void isValidTaskNumber_nonIntegerInput_falseReturned() {
        assertFalse(Parser.isValidTaskNumber("one", 3));
        assertFalse(Parser.isValidTaskNumber("1.5", 3));
        assertFalse(Parser.isValidTaskNumber("", 3));
        assertFalse(Parser.isValidTaskNumber("   ", 3));
    }

    @Test
    void isValidTaskNumber_overflowAndExplicitPlus_falseReturned() {
        assertFalse(Parser.isValidTaskNumber("99999999999999999999", 3));
        assertTrue(Parser.isValidTaskNumber("+2", 3));
    }

    @Test
    void parseTaskIndex_validTaskNumber_zeroBasedIndexReturned() {
        assertEquals(0, Parser.parseTaskIndex("1"));
        assertEquals(2, Parser.parseTaskIndex(" 3 "));
    }

    @Test
    void parseDate_validIsoDate_localDateReturned() throws CloverException {
        assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("2024-02-29"));
    }

    @Test
    void parseDate_nonexistentDate_specificExceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () -> Parser.parseDate("2024-02-30"));

        assertEquals("\"2024-02-30\" is not a real calendar date. Please check the month and day.",
                exception.getMessage());
    }

    @Test
    void parseDate_wrongShape_formatGuidanceExceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () -> Parser.parseDate("tomorrow"));

        assertEquals("Enter a date in yyyy-MM-dd format, for example 2026-02-28.", exception.getMessage());
    }

    @Test
    void parseDate_nonIsoShapes_formatGuidanceExceptionThrown() {
        assertThrows(CloverException.class, () -> Parser.parseDate("2026-2-03"));
        assertThrows(CloverException.class, () -> Parser.parseDate("2026/02/03"));
        assertThrows(CloverException.class, () -> Parser.parseDate("2025-02-29"));
    }

    @Test
    void parseTaskArguments_taskLinkedToTutoree_taskAndTutoreeSeparated() throws CloverException {
        TaskArguments arguments = Parser.parseTaskArguments("prepare worksheet /for Alice Tan");

        assertEquals("prepare worksheet", arguments.getTaskArguments());
        assertEquals("Alice Tan", arguments.getTutoreeName());
    }

    @Test
    void parseTaskArguments_blankTutoreeName_exceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () ->
                Parser.parseTaskArguments("prepare worksheet /for"));

        assertEquals("Please add a learning companion name after /for.", exception.getMessage());
    }

    @Test
    void parseTaskArguments_noMarkerAndRepeatedMarker_returnsTaskOrException() throws CloverException {
        assertEquals("prepare worksheet", Parser.parseTaskArguments("prepare worksheet").getTaskArguments());

        CloverException exception = assertThrows(CloverException.class, () ->
                Parser.parseTaskArguments("prepare /for Alice /for Bob"));
        assertEquals("Use /for only once, at the end of the task command.", exception.getMessage());
    }

    @Test
    void parse_everyNoArgumentCommandWithText_exceptionThrown() {
        assertThrows(CloverException.class, () -> Parser.parse("help now"));
        assertThrows(CloverException.class, () -> Parser.parse("bye now"));
        assertThrows(CloverException.class, () -> Parser.parse("list-tutorees now"));
    }
}
