package skynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests valid and invalid command parsing.
 */
public class ParserTest {

    @Test
    void parseTodo_validCommand_createsTodo() throws SkynetException {
        Task task = Parser.parseTodo("todo buy milk");

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("buy milk", todo.getDescription());
    }

    @Test
    void parseTodo_missingDescription_throwsFriendlyError() {
        SkynetException exception = assertThrows(SkynetException.class, () ->
                Parser.parseTodo("todo"));

        assertEquals("Please input a target.", exception.getMessage());
    }

    @Test
    void parseTodo_malformedCommand_isRejected() {
        assertThrows(SkynetException.class, () -> Parser.parseTodo(null));
        assertThrows(SkynetException.class, () -> Parser.parseTodo(" todo task"));
        assertThrows(SkynetException.class, () -> Parser.parseTodo("todo task "));
        assertThrows(SkynetException.class, () -> Parser.parseTodo("todo  task"));
        assertThrows(SkynetException.class, () -> Parser.parseTodo("todowrong task"));
    }

    @Test
    void parseDeadline_validCommand_createsDeadline() throws SkynetException {
        Task task = Parser.parseDeadline("deadline submit report /by 2026-12-01 18:00");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 12, 1, 18, 0), deadline.getBy());
    }

    @Test
    void parseDeadline_missingKeywordOrValue_isRejected() {
        assertThrows(SkynetException.class, () ->
                Parser.parseDeadline("deadline submit report"));
        assertThrows(SkynetException.class, () ->
                Parser.parseDeadline("deadline /by 2026-12-01 18:00"));
        assertThrows(SkynetException.class, () ->
                Parser.parseDeadline("deadline submit report /by"));
        assertThrows(SkynetException.class, () ->
                Parser.parseDeadline(
                        "deadline submit report /by 2026-12-01 18:00 /by 19:00"));
    }

    @Test
    void parseDeadline_invalidDate_isRejected() {
        assertThrows(SkynetException.class, () ->
                Parser.parseDeadline("deadline report /by 2026-02-30 18:00"));
        assertThrows(SkynetException.class, () ->
                Parser.parseDeadline("deadline report /by 2026-12-01 25:00"));
        assertThrows(SkynetException.class, () ->
                Parser.parseDeadline("deadline report /by not-a-date"));
    }

    @Test
    void parseEvent_validCommand_createsEvent() throws SkynetException {
        Task task = Parser.parseEvent(
                "event meeting /from 2026-12-01 10:00 /to 2026-12-01 11:00");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 12, 1, 10, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 12, 1, 11, 0), event.getTo());
    }

    @Test
    void parseEvent_invalidOrderOrDuplicateKeywords_isRejected() {
        assertThrows(SkynetException.class, () ->
                Parser.parseEvent(
                        "event meeting /from 2026-12-01 11:00 /to 2026-12-01 10:00"));
        assertThrows(SkynetException.class, () ->
                Parser.parseEvent(
                        "event meeting /from 2026-12-01 10:00 /to 2026-12-01 10:00"));
        assertThrows(SkynetException.class, () ->
                Parser.parseEvent(
                        "event meeting /from 2026-12-01 10:00 "
                                + "/from 2026-12-01 10:30 /to 2026-12-01 11:00"));
        assertThrows(SkynetException.class, () ->
                Parser.parseEvent(
                        "event meeting /from 2026-02-30 10:00 /to 2026-12-01 11:00"));
    }

    @Test
    void parseFind_validAndMissingKeyword_behavesCorrectly() throws SkynetException {
        assertEquals("report", Parser.parseFind("find report"));
        assertThrows(SkynetException.class, () -> Parser.parseFind("find"));
        assertThrows(SkynetException.class, () -> Parser.parseFind("find  report"));
    }

    @Test
    void getTaskIndex_validNumber_returnsZeroBasedIndex() throws SkynetException {
        assertEquals(0, Parser.getTaskIndex("mark 1", "mark", 3));
        assertEquals(2, Parser.getTaskIndex("delete 3", "delete", 3));
    }

    @Test
    void getTaskIndex_invalidNumber_isRejected() {
        assertThrows(SkynetException.class, () -> Parser.getTaskIndex("mark", "mark", 3));
        assertThrows(SkynetException.class, () -> Parser.getTaskIndex("mark 0", "mark", 3));
        assertThrows(SkynetException.class, () -> Parser.getTaskIndex("mark 4", "mark", 3));
        assertThrows(SkynetException.class, () -> Parser.getTaskIndex("mark -1", "mark", 3));
        assertThrows(SkynetException.class, () -> Parser.getTaskIndex("mark abc", "mark", 3));
        assertThrows(SkynetException.class, () -> Parser.getTaskIndex("mark 1 2", "mark", 3));
        assertThrows(SkynetException.class, () ->
                Parser.getTaskIndex("mark 999999999999999999999", "mark", 3));
        assertThrows(SkynetException.class, () -> Parser.getTaskIndex(null, "mark", 3));
    }
}
