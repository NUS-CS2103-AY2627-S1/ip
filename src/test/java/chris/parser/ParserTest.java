package chris.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import chris.exception.ChrisException;
import chris.task.Deadline;
import chris.task.Event;
import chris.task.Todo;

/**
 * Tests command parsing and validation behavior.
 */
public class ParserTest {
    /**
     * Verifies that supported and unsupported command words are identified correctly.
     */
    @Test
    public void parseCommandType_supportedAndUnknownWords_returnsExpectedTypes() {
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline report /by 2026-09-30"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event meeting /from 2pm /to 4pm"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("dance"));
    }

    /**
     * Verifies that a valid find command returns its keyword.
     *
     * @throws ChrisException If the valid keyword is unexpectedly rejected.
     */
    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws ChrisException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    /**
     * Verifies that a find command without a keyword is rejected.
     */
    @Test
    public void parseFindKeyword_missingKeyword_throwsChrisException() {
        assertThrows(ChrisException.class, () -> Parser.parseFindKeyword("find"));
    }

    /**
     * Verifies that a valid todo command produces the expected task.
     *
     * @throws ChrisException If the valid command is unexpectedly rejected.
     */
    @Test
    public void parseTodo_validDescription_returnsTodo() throws ChrisException {
        Todo todo = Parser.parseTodo("todo read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    /**
     * Verifies that a todo without a description is rejected.
     */
    @Test
    public void parseTodo_missingDescription_throwsChrisException() {
        assertThrows(ChrisException.class, () -> Parser.parseTodo("todo"));
    }

    /**
     * Verifies that a valid deadline command produces the expected task.
     *
     * @throws ChrisException If the valid command is unexpectedly rejected.
     */
    @Test
    public void parseDeadline_validDate_returnsDeadline() throws ChrisException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2019-12-02");

        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    /**
     * Verifies that an impossible calendar date is rejected.
     */
    @Test
    public void parseDeadline_invalidDate_throwsChrisException() {
        assertThrows(ChrisException.class, () ->
                Parser.parseDeadline("deadline return book /by 2019-02-30"));
    }

    /**
     * Verifies that a deadline without its marker is rejected.
     */
    @Test
    public void parseDeadline_missingByMarker_throwsChrisException() {
        assertThrows(ChrisException.class, () ->
                Parser.parseDeadline("deadline return book 2019-12-02"));
    }

    /**
     * Verifies that a deadline requires text on both sides of its marker.
     */
    @Test
    public void parseDeadline_missingDescriptionOrDate_throwsChrisException() {
        assertThrows(ChrisException.class, () -> Parser.parseDeadline("deadline /by 2019-12-02"));
        assertThrows(ChrisException.class, () -> Parser.parseDeadline("deadline return book /by"));
    }

    /**
     * Verifies that a valid event command produces the expected task.
     *
     * @throws ChrisException If the valid command is unexpectedly rejected.
     */
    @Test
    public void parseEvent_validDetails_returnsEvent() throws ChrisException {
        Event event = Parser.parseEvent("event project meeting /from Monday 2pm /to Monday 4pm");

        assertEquals("[E][ ] project meeting (from: Monday 2pm to: Monday 4pm)", event.toString());
    }

    /**
     * Verifies that an event requires both timing markers.
     */
    @Test
    public void parseEvent_missingMarker_throwsChrisException() {
        assertThrows(ChrisException.class, () ->
                Parser.parseEvent("event project meeting /to Monday 4pm"));
        assertThrows(ChrisException.class, () ->
                Parser.parseEvent("event project meeting /from Monday 2pm"));
    }

    /**
     * Verifies that an event requires a description, start, and end value.
     */
    @Test
    public void parseEvent_missingDetail_throwsChrisException() {
        assertThrows(ChrisException.class, () ->
                Parser.parseEvent("event /from Monday 2pm /to Monday 4pm"));
        assertThrows(ChrisException.class, () ->
                Parser.parseEvent("event meeting /from /to Monday 4pm"));
        assertThrows(ChrisException.class, () ->
                Parser.parseEvent("event meeting /from Monday 2pm /to"));
    }

    /**
     * Verifies that a valid one-based task number is converted to a zero-based index.
     *
     * @throws ChrisException If the valid number is unexpectedly rejected.
     */
    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws ChrisException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", "mark", 3));
    }

    /**
     * Verifies that missing, non-numeric, and out-of-range task numbers are rejected.
     */
    @Test
    public void parseTaskIndex_invalidNumber_throwsChrisException() {
        assertThrows(ChrisException.class, () -> Parser.parseTaskIndex("mark", "mark", 2));
        assertThrows(ChrisException.class, () -> Parser.parseTaskIndex("mark two", "mark", 2));
        assertThrows(ChrisException.class, () -> Parser.parseTaskIndex("mark 0", "mark", 2));
        assertThrows(ChrisException.class, () -> Parser.parseTaskIndex("mark 3", "mark", 2));
    }
}
