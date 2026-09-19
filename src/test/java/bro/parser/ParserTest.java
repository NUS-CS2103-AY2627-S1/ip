package bro.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bro.command.Command;
import bro.exception.BroException;

/**
 * Unit tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void constructor_noParameters_instanceCreated() {
        Parser parser = new Parser();
        assertNotNull(parser);
    }

    // -------------------------------------------------------------------------
    // parse (ParsedInput) tests
    // -------------------------------------------------------------------------

    @Test
    public void parse_validCommands_correctParsedInputReturned() {
        ParsedInput todoParsed = Parser.parse("todo read book");
        assertEquals(Command.TODO, todoParsed.command());
        assertEquals("read book", todoParsed.arguments());

        ParsedInput listParsed = Parser.parse("list");
        assertEquals(Command.LIST, listParsed.command());
        assertEquals("", listParsed.arguments());

        ParsedInput nullParsed = Parser.parse(null);
        assertEquals(Command.UNKNOWN, nullParsed.command());
        assertEquals("", nullParsed.arguments());

        ParsedInput emptyParsed = Parser.parse("   ");
        assertEquals(Command.UNKNOWN, emptyParsed.command());
        assertEquals("", emptyParsed.arguments());
    }

    // -------------------------------------------------------------------------
    // parseCommand tests
    // -------------------------------------------------------------------------

    @Test
    public void parseCommand_validCommands_correctCommandReturned() {
        assertEquals(Command.TODO, Parser.parseCommand("todo test task"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline test /by 28/8/2026"));
        assertEquals(Command.EVENT, Parser.parseCommand("event test /from 28/8/2026 /to 29/8/2026"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.TASKS, Parser.parseCommand("tasks 28/8/2026"));
        assertEquals(Command.MARK, Parser.parseCommand("mark 1"));
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark 1"));
        assertEquals(Command.DELETE, Parser.parseCommand("delete 1"));
        assertEquals(Command.EDIT, Parser.parseCommand("edit 1 /desc test"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_unknownOrEmptyInput_unknownReturned() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("unknownCommand 123"));
        assertEquals(Command.UNKNOWN, Parser.parseCommand(""));
        assertEquals(Command.UNKNOWN, Parser.parseCommand("   "));
        assertEquals(Command.UNKNOWN, Parser.parseCommand(null));
    }

    // -------------------------------------------------------------------------
    // parseArguments tests
    // -------------------------------------------------------------------------

    @Test
    public void parseArguments_commandWithArguments_argumentsReturned() {
        assertEquals("read book", Parser.parseArguments("todo read book"));
        assertEquals("return book /by 28/8/2026", Parser.parseArguments("deadline return book /by 28/8/2026"));
    }

    @Test
    public void parseArguments_noArgumentsOrEmptyInput_emptyStringReturned() {
        assertEquals("", Parser.parseArguments("list"));
        assertEquals("", Parser.parseArguments("bye"));
        assertEquals("", Parser.parseArguments(""));
        assertEquals("", Parser.parseArguments("   "));
        assertEquals("", Parser.parseArguments(null));
    }

    // -------------------------------------------------------------------------
    // parseTaskIndex tests
    // -------------------------------------------------------------------------

    @Test
    public void parseTaskIndex_validInput_zeroBasedIndexReturned() throws BroException {
        assertEquals(0, Parser.parseTaskIndex("1", "error message"));
        assertEquals(4, Parser.parseTaskIndex("5", "error message"));
        assertEquals(2, Parser.parseTaskIndex("  3  ", "error message"));
    }

    @Test
    public void parseTaskIndex_nullOrEmptyInput_exceptionThrown() {
        BroException e1 = assertThrows(BroException.class, () -> Parser.parseTaskIndex(null, "custom error 1"));
        assertEquals("custom error 1", e1.getMessage());

        BroException e2 = assertThrows(BroException.class, () -> Parser.parseTaskIndex("", "custom error 2"));
        assertEquals("custom error 2", e2.getMessage());

        BroException e3 = assertThrows(BroException.class, () -> Parser.parseTaskIndex("   ", "custom error 3"));
        assertEquals("custom error 3", e3.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericInput_exceptionThrown() {
        assertThrows(NumberFormatException.class, () -> Parser.parseTaskIndex("abc", "error"));
        assertThrows(NumberFormatException.class, () -> Parser.parseTaskIndex("1.5", "error"));
    }

    // -------------------------------------------------------------------------
    // parseTodoDescription tests
    // -------------------------------------------------------------------------

    @Test
    public void parseTodoDescription_validDescription_trimmedDescriptionReturned() throws BroException {
        assertEquals("buy milk", Parser.parseTodoDescription("buy milk"));
        assertEquals("buy groceries", Parser.parseTodoDescription("   buy groceries   "));
    }

    @Test
    public void parseTodoDescription_nullOrEmptyInput_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseTodoDescription(null));
        assertThrows(BroException.class, () -> Parser.parseTodoDescription(""));
        assertThrows(BroException.class, () -> Parser.parseTodoDescription("    "));
    }

    // -------------------------------------------------------------------------
    // parseDeadlineArguments tests
    // -------------------------------------------------------------------------

    @Test
    public void parseDeadlineDetails_validArguments_recordReturned() throws BroException {
        DeadlineDetails details = Parser.parseDeadlineDetails("return book /by 28/8/2026 1800");
        assertEquals("return book", details.description());
        assertEquals("28/8/2026 1800", details.deadline());

        DeadlineDetails trimmedDetails = Parser.parseDeadlineDetails("   submit report    /by    2026-08-28   ");
        assertEquals("submit report", trimmedDetails.description());
        assertEquals("2026-08-28", trimmedDetails.deadline());
    }

    @Test
    public void parseDeadlineArguments_validArguments_detailsReturned() throws BroException {
        String[] result = Parser.parseDeadlineArguments("return book /by 28/8/2026 1800");
        assertArrayEquals(new String[] { "return book", "28/8/2026 1800" }, result);

        String[] trimmedResult = Parser.parseDeadlineArguments("   submit report    /by    2026-08-28   ");
        assertArrayEquals(new String[] { "submit report", "2026-08-28" }, trimmedResult);
    }

    @Test
    public void parseDeadlineArguments_nullOrEmptyInput_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments(null));
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments(""));
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("   "));
    }

    @Test
    public void parseDeadlineArguments_missingByFlag_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("return book by 28/8/2026"));
    }

    @Test
    public void parseDeadlineArguments_blankDescription_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("   /by 28/8/2026"));
    }

    @Test
    public void parseDeadlineArguments_blankDeadline_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("return book /by   "));
    }

    // -------------------------------------------------------------------------
    // parseEventArguments and parseEventDetails tests
    // -------------------------------------------------------------------------

    @Test
    public void parseEventDetails_validArguments_recordReturned() throws BroException {
        EventDetails details = Parser.parseEventDetails("project meeting /from 28/8/2026 1400 /to 28/8/2026 1600");
        assertEquals("project meeting", details.description());
        assertEquals("28/8/2026 1400", details.start());
        assertEquals("28/8/2026 1600", details.end());

        EventDetails trimmedDetails = Parser.parseEventDetails("  orientation  /from  2026-08-28  /to  2026-08-29  ");
        assertEquals("orientation", trimmedDetails.description());
        assertEquals("2026-08-28", trimmedDetails.start());
        assertEquals("2026-08-29", trimmedDetails.end());
    }

    @Test
    public void parseEventArguments_validArguments_detailsReturned() throws BroException {
        String[] result = Parser.parseEventArguments("project meeting /from 28/8/2026 1400 /to 28/8/2026 1600");
        assertArrayEquals(new String[] { "project meeting", "28/8/2026 1400", "28/8/2026 1600" }, result);

        String[] trimmedResult = Parser.parseEventArguments("  orientation  /from  2026-08-28  /to  2026-08-29  ");
        assertArrayEquals(new String[] { "orientation", "2026-08-28", "2026-08-29" }, trimmedResult);
    }

    @Test
    public void parseEventArguments_nullOrEmptyInput_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEventArguments(null));
        assertThrows(BroException.class, () -> Parser.parseEventArguments(""));
        assertThrows(BroException.class, () -> Parser.parseEventArguments("    "));
    }

    @Test
    public void parseEventArguments_missingFromFlag_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEventArguments("project meeting /to 28/8/2026 1600"));
    }

    @Test
    public void parseEventArguments_missingToFlag_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEventArguments("project meeting /from 28/8/2026 1400"));
    }

    @Test
    public void parseEventArguments_reversedFlags_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser
                .parseEventArguments("meeting /to 28/8/2026 1600 /from 28/8/2026 1400"));
    }

    // -------------------------------------------------------------------------
    // parseQueryDate tests
    // -------------------------------------------------------------------------

    @Test
    public void parseQueryDate_validDate_localDateReturned() throws BroException {
        LocalDate expected = LocalDate.of(2026, 8, 28);
        assertEquals(expected, Parser.parseQueryDate("28/8/2026"));
        assertEquals(expected, Parser.parseQueryDate("2026-08-28"));
    }

    @Test
    public void parseQueryDate_invalidOrEmptyInput_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseQueryDate(""));
        assertThrows(BroException.class, () -> Parser.parseQueryDate("invalid date"));
    }

    // -------------------------------------------------------------------------
    // parseFindKeywords tests
    // -------------------------------------------------------------------------

    @Test
    public void parseFindKeywords_singleKeyword_keywordReturned() throws BroException {
        String[] result = Parser.parseFindKeywords("book");
        assertArrayEquals(new String[] { "book" }, result);

        String[] trimmedResult = Parser.parseFindKeywords("   read book   ");
        assertArrayEquals(new String[] { "read book" }, trimmedResult);
    }

    @Test
    public void parseFindKeywords_multipleCommaSeparatedKeywords_trimmedKeywordsReturned() throws BroException {
        String[] result = Parser.parseFindKeywords("book, test, assignment");
        assertArrayEquals(new String[] { "book", "test", "assignment" }, result);

        String[] resultWithExtraSpaces = Parser.parseFindKeywords("  book  ,   test phrase  ,homework  ");
        assertArrayEquals(new String[] { "book", "test phrase", "homework" }, resultWithExtraSpaces);
    }

    @Test
    public void parseFindKeywords_nullOrEmptyInput_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseFindKeywords(null));
        assertThrows(BroException.class, () -> Parser.parseFindKeywords(""));
        assertThrows(BroException.class, () -> Parser.parseFindKeywords("    "));
    }

    @Test
    public void parseFindKeywords_onlyCommasAndSpaces_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseFindKeywords(","));
        assertThrows(BroException.class, () -> Parser.parseFindKeywords(" , ,   , "));
    }

    // -------------------------------------------------------------------------
    // Exception message formatting tests (no UI tab characters)
    // -------------------------------------------------------------------------

    @Test
    public void parseExceptions_thrownMessages_containNoLeadingTab() {
        BroException todoEx = assertThrows(BroException.class, () -> Parser.parseTodoDescription(""));
        assertEquals("Sorry bro, you can't have an empty todo.\n"
                + "Format: todo <description> (e.g. todo read book)", todoEx.getMessage());

        BroException deadlineEmptyEx = assertThrows(BroException.class, () -> Parser.parseDeadlineArguments(""));
        assertEquals("Sorry bro, you can't have an empty deadline.\n"
                + "Format: deadline <description> /by <deadline> (e.g. deadline return book /by 2026-10-15 1800)",
                deadlineEmptyEx.getMessage());

        BroException deadlineFormatEx = assertThrows(BroException.class, () ->
                Parser.parseDeadlineArguments("read book by tomorrow"));
        assertEquals(
                "I think you forgot to add the deadline bro.\n"
                + "Format: deadline <description> /by <deadline> (e.g. deadline return book /by 2026-10-15 1800)",
                deadlineFormatEx.getMessage());

        BroException eventEmptyEx = assertThrows(BroException.class, () -> Parser.parseEventArguments(""));
        assertEquals("Sorry bro, you can't have an empty event.\n"
                + "Format: event <description> /from <start> /to <end> "
                + "(e.g. event camp /from 2026-10-10 /to 2026-10-12)", eventEmptyEx.getMessage());

        BroException eventFormatEx = assertThrows(BroException.class, () ->
                Parser.parseEventArguments("party /to 6pm"));
        assertEquals(
                "I think you messed up the event format bro.\n"
                + "Format: event <description> /from <start> /to <end> "
                + "(e.g. event camp /from 2026-10-10 /to 2026-10-12)",
                eventFormatEx.getMessage());

        BroException findEmptyEx = assertThrows(BroException.class, () -> Parser.parseFindKeywords(""));
        assertEquals("Bro, what are you trying to find? Please provide some keywords.\n"
                + "Format: find <keyword1>, <keyword2> (e.g. find book, project)", findEmptyEx.getMessage());
    }

    // -------------------------------------------------------------------------
    // parseEditDetails tests
    // -------------------------------------------------------------------------

    @Test
    public void parseEditDetails_singleDescriptionFlag_returnsExpectedDetails() throws BroException {
        EditDetails details = Parser.parseEditDetails("1 /desc read book");
        assertEquals(0, details.index());
        assertEquals("read book", details.description());
        assertNull(details.by());
        assertNull(details.from());
        assertNull(details.to());
        assertTrue(details.hasDescription());
        assertFalse(details.hasBy());
        assertFalse(details.hasFrom());
        assertFalse(details.hasTo());
    }

    @Test
    public void parseEditDetails_singleByFlag_returnsExpectedDetails() throws BroException {
        EditDetails details = Parser.parseEditDetails("2 /by 2026-10-15 1800");
        assertEquals(1, details.index());
        assertNull(details.description());
        assertEquals("2026-10-15 1800", details.by());
        assertNull(details.from());
        assertNull(details.to());
        assertTrue(details.hasBy());
        assertFalse(details.hasDescription());
    }

    @Test
    public void parseEditDetails_fromAndToFlags_returnsExpectedDetails() throws BroException {
        EditDetails details = Parser.parseEditDetails("3 /from 2pm /to 4pm");
        assertEquals(2, details.index());
        assertNull(details.description());
        assertNull(details.by());
        assertEquals("2pm", details.from());
        assertEquals("4pm", details.to());
        assertTrue(details.hasFrom());
        assertTrue(details.hasTo());
    }

    @Test
    public void parseEditDetails_multipleFlagsAnyOrder_returnsExpectedDetails() throws BroException {
        EditDetails details = Parser.parseEditDetails("5 /to 6pm /desc project meeting /from 4pm");
        assertEquals(4, details.index());
        assertEquals("project meeting", details.description());
        assertEquals("4pm", details.from());
        assertEquals("6pm", details.to());
        assertNull(details.by());
    }

    @Test
    public void parseEditDetails_slashInsideDescription_preservedCorrectly() throws BroException {
        EditDetails details = Parser.parseEditDetails("1 /desc read chapter 1/2");
        assertEquals("read chapter 1/2", details.description());
    }

    @Test
    public void parseEditDetails_missingIndexOrEmpty_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEditDetails(null));
        assertThrows(BroException.class, () -> Parser.parseEditDetails(""));
        assertThrows(BroException.class, () -> Parser.parseEditDetails("   "));
    }

    @Test
    public void parseEditDetails_missingFlags_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1"));
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1   "));
    }

    @Test
    public void parseEditDetails_unknownFlagsOnly_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1 /unknown test"));
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1 test"));
    }

    @Test
    public void parseEditDetails_emptyFlagValue_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1 /desc"));
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1 /desc   "));
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1 /desc /by tomorrow"));
    }

    @Test
    public void parseEditDetails_unrecognizedPrefixBeforeFlags_exceptionThrown() {
        assertThrows(BroException.class, () -> Parser.parseEditDetails("1 something /desc book"));
    }

    @Test
    public void parseEditDetails_invalidIndexFormat_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.parseEditDetails("abc /desc new task"));
    }

    // -------------------------------------------------------------------------
    // Delimiter and newline validation tests
    // -------------------------------------------------------------------------

    @Test
    public void parse_delimiterOrNewline_exceptionThrown() {
        BroException e1 = assertThrows(BroException.class, () ->
                Parser.parseTodoDescription("read | book"));
        assertTrue(e1.getMessage().contains("can't have the '|' character"));

        BroException e2 = assertThrows(BroException.class, () ->
                Parser.parseTodoDescription("read\nbook"));
        assertTrue(e2.getMessage().contains("can't have newline characters"));

        BroException e3 = assertThrows(BroException.class, () ->
                Parser.parseDeadlineArguments("read | book /by 2026-10-15"));
        assertTrue(e3.getMessage().contains("can't have the '|' character"));

        BroException e4 = assertThrows(BroException.class, () ->
                Parser.parseDeadlineArguments("read book /by 2026-10-15 | 1800"));
        assertTrue(e4.getMessage().contains("can't have the '|' character"));

        BroException e5 = assertThrows(BroException.class, () ->
                Parser.parseEventArguments("camp /from 2026-10-15 | 1000 /to 2026-10-16"));
        assertTrue(e5.getMessage().contains("can't have the '|' character"));

        BroException e6 = assertThrows(BroException.class, () ->
                Parser.parseEditDetails("1 /desc test\rtest"));
        assertTrue(e6.getMessage().contains("can't have newline characters"));
    }

    // -------------------------------------------------------------------------
    // Duplicate flags tests
    // -------------------------------------------------------------------------

    @Test
    public void parse_duplicateFlags_exceptionThrown() {
        BroException e1 = assertThrows(BroException.class, () ->
                Parser.parseDeadlineArguments("read book /by 2026-10-15 /by 2026-10-16"));
        assertTrue(e1.getMessage().contains("more than once"));

        BroException e2 = assertThrows(BroException.class, () ->
                Parser.parseEventArguments("camp /from 2026-10-15 /from 2026-10-16 /to 2026-10-17"));
        assertTrue(e2.getMessage().contains("more than once"));

        BroException e3 = assertThrows(BroException.class, () ->
                Parser.parseEventArguments("camp /from 2026-10-15 /to 2026-10-16 /to 2026-10-17"));
        assertTrue(e3.getMessage().contains("more than once"));

        BroException e4 = assertThrows(BroException.class, () ->
                Parser.parseEditDetails("1 /desc task1 /desc task2"));
        assertTrue(e4.getMessage().contains("more than once"));

        BroException e5 = assertThrows(BroException.class, () ->
                Parser.parseEditDetails("1 /by 2026-10-15 /by 2026-10-16"));
        assertTrue(e5.getMessage().contains("more than once"));
    }
}
