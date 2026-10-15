package orbit.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import orbit.exception.OrbitException;
import orbit.task.Task;

/**
 * Tests command recognition and validation performed by {@link Parser}.
 */
class ParserTest {
    /**
     * Verifies that complete command words are recognized both alone and with arguments.
     */
    @Test
    void isCommand_exactCommandOrCommandWithArguments_returnsTrue() {
        assertTrue(Parser.isCommand("list", "list"));
        assertTrue(Parser.isCommand("todo read book", "todo"));
    }

    /**
     * Verifies that command text embedded in a different word or sentence is not recognized.
     */
    @Test
    void isCommand_partialOrPrefixedCommand_returnsFalse() {
        assertFalse(Parser.isCommand("listing", "list"));
        assertFalse(Parser.isCommand("please list", "list"));
    }

    /**
     * Verifies that a valid task number is parsed despite surrounding whitespace.
     *
     * @throws OrbitException if the valid test input is unexpectedly rejected
     */
    @Test
    void parseTaskNumber_validNumber_returnsOneBasedNumber() throws OrbitException {
        assertEquals(2, Parser.parseTaskNumber("mark   2  ", "mark", 3));
    }

    /**
     * Verifies that missing, non-numeric, out-of-range, and empty-list task numbers are rejected.
     */
    @Test
    void parseTaskNumber_invalidArguments_throwsOrbitException() {
        assertThrows(OrbitException.class, () -> Parser.parseTaskNumber("mark", "mark", 3));
        assertThrows(OrbitException.class, () -> Parser.parseTaskNumber("mark two", "mark", 3));
        assertThrows(OrbitException.class, () -> Parser.parseTaskNumber("mark 0", "mark", 3));
        assertThrows(OrbitException.class, () -> Parser.parseTaskNumber("mark 4", "mark", 3));
        assertThrows(OrbitException.class, () -> Parser.parseTaskNumber("mark 1", "mark", 0));
    }

    /**
     * Verifies that surrounding whitespace is removed from a valid find keyword.
     *
     * @throws OrbitException if the valid test input is unexpectedly rejected
     */
    @Test
    void parseFindKeyword_validKeyword_trimsAndReturnsKeyword() throws OrbitException {
        assertEquals("return book", Parser.parseFindKeyword("find   return book  "));
    }

    /**
     * Verifies that a find command without a keyword is rejected.
     */
    @Test
    void parseFindKeyword_missingKeyword_throwsOrbitException() {
        assertThrows(OrbitException.class, () -> Parser.parseFindKeyword("find"));
        assertThrows(OrbitException.class, () -> Parser.parseFindKeyword("find   "));
    }

    /**
     * Verifies that schedule dates are parsed and invalid dates are rejected.
     *
     * @throws OrbitException if the valid test input is unexpectedly rejected
     */
    @Test
    void parseScheduleDate_validAndInvalidArguments_returnsDateOrThrows() throws OrbitException {
        assertEquals(LocalDate.parse("2019-12-02"),
                Parser.parseScheduleDate("schedule 2019-12-02"));
        assertThrows(OrbitException.class, () -> Parser.parseScheduleDate("schedule"));
        assertThrows(OrbitException.class, () -> Parser.parseScheduleDate("schedule 2019-02-30"));
    }

    /**
     * Verifies that a todo description is trimmed and converted to its display and storage forms.
     *
     * @throws OrbitException if the valid test input is unexpectedly rejected
     */
    @Test
    void parseTodo_validDescription_trimsAndCreatesTodo() throws OrbitException {
        Task task = Parser.parseTodo("todo   read book  ");

        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toDataString());
    }

    /**
     * Verifies that a todo without a description is rejected.
     */
    @Test
    void parseTodo_emptyDescription_throwsOrbitException() {
        assertThrows(OrbitException.class, () -> Parser.parseTodo("todo   "));
        assertThrows(OrbitException.class, () -> Parser.parseTodo("todo left | right"));
    }

    /**
     * Verifies that a deadline command parses and formats a valid ISO date.
     *
     * @throws OrbitException if the valid test input is unexpectedly rejected
     */
    @Test
    void parseDeadline_validCommand_parsesAndFormatsDate() throws OrbitException {
        Task task = Parser.parseDeadline("deadline return book /by 2019-12-02");

        assertEquals("[D][ ] return book (by: Dec 2 2019)", task.toString());
        assertEquals("D | 0 | return book | 2019-12-02", task.toDataString());
    }

    /**
     * Verifies that incomplete deadline commands and impossible dates are rejected.
     */
    @Test
    void parseDeadline_missingFieldsOrInvalidDate_throwsOrbitException() {
        assertThrows(OrbitException.class, () -> Parser.parseDeadline("deadline return book"));
        assertThrows(OrbitException.class, () -> Parser.parseDeadline("deadline /by 2019-12-02"));
        assertThrows(OrbitException.class, () -> Parser.parseDeadline("deadline return book /by"));
        assertThrows(OrbitException.class, () -> Parser.parseDeadline("deadline return book /by 2019-02-30"));
    }

    /**
     * Verifies that an event command parses and formats valid start and end dates.
     *
     * @throws OrbitException if the valid test input is unexpectedly rejected
     */
    @Test
    void parseEvent_validCommand_parsesAndFormatsDates() throws OrbitException {
        Task task = Parser.parseEvent("event project meeting /from 2019-12-02 /to 2019-12-03");

        assertEquals("[E][ ] project meeting (from: Dec 2 2019 to: Dec 3 2019)", task.toString());
        assertEquals("E | 0 | project meeting | 2019-12-02 | 2019-12-03", task.toDataString());
    }

    /**
     * Verifies that incomplete event commands and impossible dates are rejected.
     */
    @Test
    void parseEvent_missingFieldsOrInvalidDates_throwsOrbitException() {
        assertThrows(OrbitException.class, () -> Parser.parseEvent("event project meeting"));
        assertThrows(OrbitException.class, () -> Parser.parseEvent("event /from 2019-12-02 /to 2019-12-03"));
        assertThrows(OrbitException.class, () -> Parser.parseEvent("event meeting /from 2019-12-02"));
        assertThrows(OrbitException.class, () -> Parser.parseEvent("event meeting /from /to 2019-12-03"));
        assertThrows(OrbitException.class, () -> Parser.parseEvent("event meeting /from 2019-12-02 /to"));
        assertThrows(OrbitException.class, () -> Parser.parseEvent(
                "event meeting /from 2019-02-30 /to 2019-12-03"));
        assertThrows(OrbitException.class, () -> Parser.parseEvent(
                "event meeting /from 2019-12-04 /to 2019-12-03"));
    }
}
