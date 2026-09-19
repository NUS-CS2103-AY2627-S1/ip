package chai;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import chai.task.Deadline;
import chai.task.Event;
import chai.task.Todo;

/** Tests conversion of command text into command data. */
public class ParserTest {

    @Test
    public void parseTodo_validAndEmptyCommands_returnsTaskOrThrows() throws ChaiException {
        Todo todo = Parser.parseTodo("todo borrow book");

        assertEquals("borrow book", todo.getDescription());
        assertThrows(ChaiException.class, () -> Parser.parseTodo("todo"));
    }

    @Test
    public void parseDeadline_validAndInvalidDates_returnsTaskOrThrows() throws ChaiException {
        Deadline deadline = Parser.parseDeadline("DEADLINE  return book   /BY  2026-09-30");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 30), deadline.getBy());
        assertThrows(ChaiException.class,
                () -> Parser.parseDeadline("deadline return book /by next week"));
    }

    @Test
    public void parseEvent_validAndReversedDates_returnsTaskOrThrows() throws ChaiException {
        Event event = Parser.parseEvent(
                "EVENT  conference   /FROM  2026-09-20   /TO  2026-09-22");

        assertEquals("conference", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), event.getFrom());
        assertEquals(LocalDate.of(2026, 9, 22), event.getTo());
        assertThrows(ChaiException.class,
                () -> Parser.parseEvent("event conference /from 2026-09-22 /to 2026-09-20"));
    }

    @Test
    public void parseTaskIndex_validAndOutOfRangeNumbers_returnsIndexOrThrows() throws ChaiException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", "mark", 3));
        assertThrows(ChaiException.class, () -> Parser.parseTaskIndex("mark 4", "mark", 3));
        assertThrows(ChaiException.class, () -> Parser.parseTaskIndex("mark two", "mark", 3));
    }

    @Test
    public void parseFindKeyword_presentAndMissingKeywords_returnsKeywordOrThrows() throws ChaiException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
        assertThrows(ChaiException.class, () -> Parser.parseFindKeyword("find"));
    }

    @Test
    public void requireNoArguments_extraArgument_exceptionThrown() {
        ChaiException exception = assertThrows(
                ChaiException.class, () -> Parser.requireNoArguments("sort date", "sort"));

        assertEquals("Use: sort", exception.getMessage());
    }

    @Test
    public void requireNoArguments_exactCommand_noExceptionThrown() {
        assertDoesNotThrow(() -> Parser.requireNoArguments("SORT", "sort"));
    }
}
