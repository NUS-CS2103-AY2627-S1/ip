package seedu.goat.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.goat.GoatException;
import seedu.goat.command.Command;
import seedu.goat.task.Deadline;
import seedu.goat.task.Event;
import seedu.goat.task.Todo;

/**
 * Tests the translation of raw user input into commands and tasks.
 * <p>
 * Parser deliberately knows nothing about the task list, so every case here is decided
 * from the text alone and needs no fixture beyond the input string.
 */
public class ParserTest {

    @Test
    public void parse_commandWithArguments_argumentsSeparated() throws GoatException {
        Parser.ParsedCommand parsed = Parser.parse("deadline return book /by 2019-12-02");
        assertEquals(Command.DEADLINE, parsed.command());
        assertEquals("return book /by 2019-12-02", parsed.arguments());
    }

    @Test
    public void parse_commandWithoutArguments_argumentsEmpty() throws GoatException {
        assertEquals("", Parser.parse("list").arguments());
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        GoatException e = assertThrows(GoatException.class, () -> Parser.parse("blah"));
        assertTrue(e.getMessage().contains("blah"), "the message should quote what was typed");
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parse(""));
    }

    @Test
    public void parseTodo_validDescription_todoCreated() throws GoatException {
        Todo todo = Parser.parseTodo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_validInput_deadlineCreated() throws GoatException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02");
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_missingBy_exceptionThrown() {
        GoatException e = assertThrows(GoatException.class,
                () -> Parser.parseDeadline("return book"));
        assertTrue(e.getMessage().contains("/by"));
    }

    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseDeadline("/by 2019-12-02"));
    }

    @Test
    public void parseDeadline_emptyBy_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseDeadline("return book /by"));
    }

    @Test
    public void parseDeadline_unreadableDate_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseDeadline("return book /by Sunday"));
    }

    @Test
    public void parseEvent_validInput_eventCreated() throws GoatException {
        Event event = Parser.parseEvent("meeting /from 2019-10-15 /to 2019-10-16");
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
    }

    @Test
    public void parseEvent_sameDayStartAndEnd_eventCreated() throws GoatException {
        // A single-day event is legitimate: the end is not before the start.
        Event event = Parser.parseEvent("standup /from 2019-10-15 /to 2019-10-15");
        assertEquals("[E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)", event.toString());
    }

    @Test
    public void parseEvent_missingFrom_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseEvent("meeting /to 2019-10-16"));
    }

    @Test
    public void parseEvent_missingTo_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseEvent("meeting /from 2019-10-15"));
    }

    @Test
    public void parseEvent_toWrittenBeforeFrom_exceptionThrown() {
        // /to is searched for only after /from, so this must be rejected rather than
        // silently parsed with the two halves swapped.
        assertThrows(GoatException.class,
                () -> Parser.parseEvent("meeting /to 2019-10-16 /from 2019-10-15"));
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionThrown() {
        GoatException e = assertThrows(GoatException.class,
                () -> Parser.parseEvent("meeting /from 2019-10-16 /to 2019-10-15"));
        assertTrue(e.getMessage().contains("cannot end before it starts"));
    }

    @Test
    public void parseEvent_missingDescription_exceptionThrown() {
        assertThrows(GoatException.class,
                () -> Parser.parseEvent("/from 2019-10-15 /to 2019-10-16"));
    }

    @Test
    public void parseTaskNumber_validNumber_numberReturned() throws GoatException {
        assertEquals(2, Parser.parseTaskNumber("2", Command.MARK));
    }

    @Test
    public void parseTaskNumber_outOfRangeNumber_numberReturnedAnyway() throws GoatException {
        // Whether task 99 exists depends on the list, which Parser cannot see, so this
        // is deliberately not Parser's job to reject.
        assertEquals(99, Parser.parseTaskNumber("99", Command.DELETE));
    }

    @Test
    public void parseTaskNumber_missingArgument_exceptionThrown() {
        GoatException e = assertThrows(GoatException.class,
                () -> Parser.parseTaskNumber("", Command.MARK));
        assertTrue(e.getMessage().contains("mark"), "the message should name the command");
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseTaskNumber("abc", Command.MARK));
    }

    @Test
    public void parseTaskNumber_numberTooLargeForInt_exceptionThrown() {
        // Integer.parseInt overflows here; it must be reported, not propagated raw.
        assertThrows(GoatException.class,
                () -> Parser.parseTaskNumber("2147483648", Command.MARK));
    }

    @Test
    public void parseKeyword_keywordGiven_keywordReturned() throws GoatException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    public void parseKeyword_severalWords_keptTogether() throws GoatException {
        assertEquals("project meeting", Parser.parseKeyword("project meeting"));
    }

    @Test
    public void parseKeyword_missingKeyword_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseKeyword(""));
    }

    @Test
    public void parse_findCommand_recognised() throws GoatException {
        Parser.ParsedCommand parsed = Parser.parse("find book");
        assertEquals(Command.FIND, parsed.command());
        assertEquals("book", parsed.arguments());
    }

    @Test
    public void parseDate_validDate_dateReturned() throws GoatException {
        assertEquals("Oct 15 2019",
                seedu.goat.DateFormats.format(Parser.parseDate("2019-10-15")));
    }

    @Test
    public void parseDate_missingDate_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseDate(""));
    }
    @Test
    public void parse_repeatedParameters_exceptionThrown() {
        assertThrows(GoatException.class,
                () -> Parser.parseDeadline("book /by 2026-09-18 /by 2026-09-19"));
        assertThrows(GoatException.class,
                () -> Parser.parseEvent("trip /from 2026-09-18 /from 2026-09-19 /to 2026-09-20"));
        assertThrows(GoatException.class,
                () -> Parser.parseEvent("trip /to 2026-09-20 /from 2026-09-18 /to 2026-09-20"));
    }

    @Test
    public void parseEvent_emptyDates_exceptionThrown() {
        assertThrows(GoatException.class, () -> Parser.parseEvent("trip /from /to 2026-09-18"));
        assertThrows(GoatException.class, () -> Parser.parseEvent("trip /from 2026-09-18 /to"));
    }

    @Test
    public void parse_whitespaceAndExtraArguments_validated() throws GoatException {
        assertEquals("read book", Parser.parse(" \ttodo\t read book ").arguments());
        assertThrows(GoatException.class, () -> Parser.parse("list extra"));
        assertThrows(GoatException.class, () -> Parser.parse("bye extra"));
        assertThrows(GoatException.class, () -> Parser.parse("todo one\ntodo two"));
    }
}
