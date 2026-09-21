package aster.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import aster.exception.AsterException;

/**
 * Tests validation and data extraction performed by {@link Parser}.
 */
class ParserTest {

    @Test
    void parse_commandAndTrimmedArguments_commandAndArgumentsReturned() throws AsterException {
        Parser.ParsedCommand parsed = Parser.parse("  deadline   submit report /by 2026-09-01  ");

        assertEquals(Command.DEADLINE, parsed.getCommand());
        assertEquals("submit report /by 2026-09-01", parsed.getArguments());
    }

    @Test
    void parse_noArgumentCommand_commandReturnedWithEmptyArguments() throws AsterException {
        Parser.ParsedCommand parsed = Parser.parse("list");

        assertEquals(Command.LIST, parsed.getCommand());
        assertEquals("", parsed.getArguments());
    }

    @Test
    void parse_tagCommand_commandAndArgumentsReturned() throws AsterException {
        Parser.ParsedCommand parsed = Parser.parse("tag 2 #fun");

        assertEquals(Command.TAG, parsed.getCommand());
        assertEquals("2 #fun", parsed.getArguments());
    }

    @Test
    void parse_nullEmptyAndUnknownInput_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parse(null));
        assertThrows(AsterException.class, () -> Parser.parse("   "));
        assertThrows(AsterException.class, () -> Parser.parse("listing"));
    }

    @Test
    void parseTodoDescription_presentDescription_descriptionReturned() throws AsterException {
        assertEquals("read a book", Parser.parseTodoDescription("read a book"));
    }

    @Test
    void parseTodoDescription_emptyDescription_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseTodoDescription(""));
    }

    @Test
    void parseDeadlineDetails_validDetails_trimmedFieldsReturned() throws AsterException {
        Parser.DeadlineDetails details = Parser.parseDeadlineDetails("  submit report  /by  2026-09-01 ");

        assertEquals("submit report", details.getDescription());
        assertEquals("2026-09-01", details.getBy());
    }

    @Test
    void parseDeadlineDetails_missingOrMalformedParts_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseDeadlineDetails("submit report"));
        assertThrows(AsterException.class, () -> Parser.parseDeadlineDetails("/by 2026-09-01"));
        assertThrows(AsterException.class, () -> Parser.parseDeadlineDetails("submit report /by"));
        assertThrows(AsterException.class, () -> Parser.parseDeadlineDetails("submit /bydate"));
        assertThrows(AsterException.class, () -> Parser.parseDeadlineDetails("submit /by 2026-09-01 /by 2026-09-02"));
    }

    @Test
    void parseDeadlineDetails_unparseableByValue_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseDeadlineDetails("submit report /by Sunday"));
        assertThrows(AsterException.class, () -> Parser.parseDeadlineDetails("submit report /by no idea"));
    }

    @Test
    void parseEventDetails_validDetails_trimmedFieldsReturned() throws AsterException {
        Parser.EventDetails details = Parser.parseEventDetails(
                "  project meeting  /from  2026-09-01 09:00  /to  2026-09-01 10:00 ");

        assertEquals("project meeting", details.getDescription());
        assertEquals("2026-09-01 09:00", details.getFrom());
        assertEquals("2026-09-01 10:00", details.getTo());
    }

    @Test
    void parseEventDetails_missingMalformedOrOutOfOrderParts_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /to noon"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /from 9am"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /to noon /from 9am"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("/from 9am /to noon"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /from /to noon"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /from 9am /to"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /fromtime 9am /to noon"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /from 2026-09-01 /to 2026-09-01"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /from 2026-09-02 /to 2026-09-01"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /from 2026-09-01 /from 2026-09-02"
                + " /to 2026-09-03"));
        assertThrows(AsterException.class, () -> Parser.parseEventDetails("meeting /from 2026-09-01 /to 2026-09-02"
                + " /to 2026-09-03"));
    }

    @Test
    void parseOnDate_supportedFormats_datesReturned() throws AsterException {
        assertEquals(LocalDate.of(2026, 9, 1), Parser.parseOnDate("2026-09-01"));
        assertEquals(LocalDate.of(2026, 9, 1), Parser.parseOnDate("1/9/2026"));
        assertEquals(LocalDate.of(2026, 9, 1), Parser.parseOnDate("Sep 1 2026"));
        assertEquals(LocalDate.of(2026, 9, 1), Parser.parseOnDate("1 Sep 2026"));
    }

    @Test
    void parseOnDate_emptyOrInvalidDate_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseOnDate(""));
        assertThrows(AsterException.class, () -> Parser.parseOnDate("tomorrow"));
        assertThrows(AsterException.class, () -> Parser.parseOnDate("2026-02-30"));
    }

    @Test
    void parseFindKeyword_presentKeyword_trimmedKeywordReturned() throws AsterException {
        assertEquals("book", Parser.parseFindKeyword("  book  "));
    }

    @Test
    void parseFindKeyword_emptyKeyword_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseFindKeyword(""));
    }

    @Test
    void parseTagDetails_validDetails_trimmedFieldsReturned() throws AsterException {
        Parser.TagDetails details = Parser.parseTagDetails("  2   #project_work ");

        assertEquals("2", details.getTaskNumberText());
        assertEquals("#project_work", details.getTag());
    }

    @Test
    void parseTagDetails_missingOrInvalidParts_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseTagDetails(""));
        assertThrows(AsterException.class, () -> Parser.parseTagDetails("2"));
        assertThrows(AsterException.class, () -> Parser.parseTagDetails("2 fun"));
        assertThrows(AsterException.class, () -> Parser.parseTagDetails("2 #"));
    }

    @Test
    void parseTaskNumber_validBoundaryNumbers_numbersReturned() throws AsterException {
        assertEquals(1, Parser.parseTaskNumber("1", 3, "mark"));
        assertEquals(3, Parser.parseTaskNumber("3", 3, "mark"));
    }

    @Test
    void parseTaskNumber_missingMalformedOutOfRangeOrOverflowNumber_exceptionThrown() {
        assertThrows(AsterException.class, () -> Parser.parseTaskNumber("1", 0, "mark"));
        assertThrows(AsterException.class, () -> Parser.parseTaskNumber("", 3, "mark"));
        assertThrows(AsterException.class, () -> Parser.parseTaskNumber("1 2", 3, "mark"));
        assertThrows(AsterException.class, () -> Parser.parseTaskNumber("1.0", 3, "mark"));
        assertThrows(AsterException.class, () -> Parser.parseTaskNumber("0", 3, "mark"));
        assertThrows(AsterException.class, () -> Parser.parseTaskNumber("4", 3, "mark"));
        assertThrows(AsterException.class, () -> Parser.parseTaskNumber("999999999999999999999", 3, "mark"));
    }
}
