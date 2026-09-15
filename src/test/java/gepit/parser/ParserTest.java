package gepit.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import gepit.GepitException;
import gepit.task.Deadline;
import gepit.task.Event;
import gepit.task.Todo;

public class ParserTest {

    @Test
    public void parseDeadline_validInput_returnsDeadline()
            throws GepitException {
        Deadline deadline = Parser.parseDeadline(
                "submit report /by 2026-09-10");

        assertEquals("submit report", deadline.getDescription());
        assertEquals(
                LocalDate.of(2026, 9, 10),
                deadline.getDue());
    }

    @Test
    public void parseDeadline_invalidDate_throwsException() {
        assertThrows(GepitException.class, () ->
                Parser.parseDeadline(
                        "submit report /by definitely-not-a-date"));
    }

    @Test
    public void parseDeadline_missingBy_throwsException() {
        assertThrows(GepitException.class, () ->
                Parser.parseDeadline("submit report"));
    }

    @Test
    public void parseEvent_validInput_returnsEvent()
            throws GepitException {
        Event event = Parser.parseEvent(
                "meeting /from 2026-09-10 /to 2026-09-12");

        assertEquals("meeting", event.getDescription());
        assertEquals(
                LocalDate.of(2026, 9, 10),
                event.getStart());
        assertEquals(
                LocalDate.of(2026, 9, 12),
                event.getEnd());
    }

    @Test
    public void parseEvent_endBeforeStart_throwsException() {
        assertThrows(GepitException.class, () ->
                Parser.parseEvent(
                        "meeting /from 2026-09-12 /to 2026-09-10"));
    }

    @Test
    void parseUpdate_validInput_returnsUpdateArguments() throws GepitException {
        Parser.UpdateArguments arguments =
                Parser.parseUpdate("2 buy groceries");

        assertEquals(2, arguments.taskNumber());
        assertEquals("buy groceries", arguments.description());
    }

    @Test
    void parseUpdate_descriptionContainsDelimiter_preservesDescription()
            throws GepitException {
        Parser.UpdateArguments arguments =
                Parser.parseUpdate("1 return book /by whatever");

        assertEquals("return book /by whatever", arguments.description());
    }

    @Test
    void parseUpdate_missingDescription_throwsException() {
        assertThrows(GepitException.class, () ->
                Parser.parseUpdate("1"));
    }

    @Test
    void parseUpdate_invalidTaskNumber_throwsException() {
        assertThrows(GepitException.class, () ->
                Parser.parseUpdate("abc new description"));
    }

    @Test
    void getArgument_extraWhitespace_returnsTrimmedArgument()
            throws GepitException {
        assertEquals("read book", Parser.getArgument("  todo    read book  "));
    }

    @Test
    void parseTodo_blankDescription_throwsException() {
        assertThrows(GepitException.class, () -> Parser.parseTodo("   "));
    }

    @Test
    void parseTodo_validInput_trimsDescription() throws GepitException {
        Todo todo = Parser.parseTodo("  read book  ");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    void parseEvent_sameStartAndEnd_throwsException() {
        assertThrows(GepitException.class, () -> Parser.parseEvent(
                "meeting /from 2026-09-10 /to 2026-09-10"));
    }

    @Test
    void parseDeadline_repeatedByParameter_throwsException() {
        assertThrows(GepitException.class, () -> Parser.parseDeadline(
                "report /by 2026-09-10 /by 2026-09-11"));
    }

    @Test
    void parseTaskNumber_zero_throwsException() {
        assertThrows(GepitException.class, () -> Parser.parseTaskNumber("0"));
    }
}
