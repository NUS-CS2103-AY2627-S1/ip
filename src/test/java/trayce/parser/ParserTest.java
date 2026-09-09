package trayce.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import trayce.task.Deadline;
import trayce.task.Event;
import trayce.task.Task;

/** Tests conversion of user task commands into the correct task objects. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_validCommands_createCorrectTaskTypesAndDetails() {
        Task todo = parser.parseTask("TODO read book");
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask("deadline submit report /by 2026-09-15"));
        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event orientation /from 2026-09-01 /to 2026-09-03"));

        assertEquals("read book", todo.getDescription());
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), deadline.getBy());
        assertEquals(LocalDate.of(2026, 9, 1), event.getFrom());
        assertEquals(LocalDate.of(2026, 9, 3), event.getTo());
    }

    @Test
    void parseTask_missingDetailsOrInvalidDate_returnsNull() {
        assertNull(parser.parseTask("todo"));
        assertNull(parser.parseTask("deadline return book /by 15/09/2026"));
        assertNull(parser.parseTask("event meeting /from 2026-09-01"));
        assertNull(parser.parseTask("unknown command"));
    }
}
