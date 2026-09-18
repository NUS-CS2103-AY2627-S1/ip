package joebiden.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import joebiden.exception.JoeBidenException;
import joebiden.task.Deadline;
import joebiden.task.Event;

public class ParserTest {

    @Test
    public void parseDeadline_validInput_returnsDeadline()
            throws JoeBidenException {
        Deadline deadline = Parser.parseDeadline(
                "submit report /by 2026-09-15 1800"
        );

        assertEquals("submit report", deadline.getName());
        assertEquals(
                LocalDateTime.of(2026, 9, 15, 18, 0),
                deadline.getBy()
        );
    }

    @Test
    public void parseDeadline_invalidDate_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.parseDeadline("submit report /by abc"));
    }

    @Test
    public void parseDeadline_missingBy_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.parseDeadline("submit report"));
    }

    @Test
    public void parseDescription_validDescription_returnsDescription()
            throws JoeBidenException {
        assertEquals(
                "read book",
                Parser.parseDescription("read book")
        );
    }

    @Test
    public void parseDescription_emptyDescription_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.parseDescription(""));
    }

    @Test
    public void getTaskNumber_validNumber_returnsNumber()
            throws JoeBidenException {
        assertEquals(
                3,
                Parser.getTaskNumber("3")
        );
    }

    @Test
    public void getTaskNumber_nonNumeric_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.getTaskNumber("abc"));
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword()
            throws JoeBidenException {
        assertEquals(
                "book",
                Parser.parseFindKeyword("book")
        );
    }

    @Test
    public void parseFindKeyword_emptyKeyword_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.parseFindKeyword(""));
    }

    @Test
    public void parseEvent_validInput_returnsEvent()
            throws JoeBidenException {
        Event event = Parser.parseEvent(
                "meeting /from 2026-09-16 1400 "
                        + "/to 2026-09-16 1600"
        );

        assertEquals("meeting", event.getName());
        assertEquals(
                LocalDateTime.of(2026, 9, 16, 14, 0),
                event.getFrom()
        );
        assertEquals(
                LocalDateTime.of(2026, 9, 16, 16, 0),
                event.getTo()
        );
    }

    @Test
    public void parseEvent_missingFrom_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.parseEvent(
                        "meeting /to 2026-09-16 1600"
                ));
    }

    @Test
    public void parseEvent_missingTo_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.parseEvent(
                        "meeting /from 2026-09-16 1400"
                ));
    }

    @Test
    public void parseEvent_invalidDate_throwsException() {
        assertThrows(JoeBidenException.class, () ->
                Parser.parseEvent(
                        "meeting /from abc /to 2026-09-16 1600"
                ));
    }
}
