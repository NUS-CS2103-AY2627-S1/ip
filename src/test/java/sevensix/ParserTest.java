package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Verifies that command text is recognized and broken into the values the chatbot acts on.
 */
class ParserTest {
    @Test
    void isCommandKeywordAloneOrFollowedBySpaceReturnsTrue() {
        assertTrue(Parser.isCommand("todo", Parser.COMMAND_TODO));
        assertTrue(Parser.isCommand("todo read book", Parser.COMMAND_TODO));
    }

    @Test
    void isCommandKeywordAsPrefixOfLongerWordReturnsFalse() {
        assertFalse(Parser.isCommand("todolist", Parser.COMMAND_TODO));
    }

    @Test
    void parseTodoDescriptionReturnsTrimmedDescription() throws SevenSixException {
        assertEquals("read book", Parser.parseTodoDescription("todo   read book  "));
    }

    @Test
    void parseTodoDescriptionWithoutDescriptionThrows() {
        assertThrows(SevenSixException.class, () -> Parser.parseTodoDescription("todo"));
    }

    @Test
    void parseDeadlineSplitsDescriptionAndDueDateTime() throws SevenSixException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2019-06-06 1800");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 6, 6), deadline.getBy());
        assertEquals(LocalTime.of(18, 0), deadline.getByTime());
    }

    @Test
    void parseDeadlineWithoutByMarkerThrows() {
        assertThrows(SevenSixException.class, () -> Parser.parseDeadline("deadline return book"));
    }

    @Test
    void parseDeadlineWithBlankDueTimeThrows() {
        assertThrows(SevenSixException.class, () -> Parser.parseDeadline("deadline return book /by  "));
    }

    @Test
    void parseEventSplitsDescriptionStartAndEnd() throws SevenSixException {
        Event event = Parser.parseEvent("event project meeting /from 2019-06-06 /to 2019-06-07");

        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2019, 6, 6), event.getFrom());
        assertEquals(LocalDate.of(2019, 6, 7), event.getTo());
    }

    @Test
    void parseEventMissingToMarkerThrows() {
        assertThrows(SevenSixException.class,
                () -> Parser.parseEvent("event project meeting /from 2019-06-06"));
    }

    @Test
    void parseFindKeywordReturnsTrimmedKeyword() throws SevenSixException {
        assertEquals("book", Parser.parseFindKeyword("find  book "));
    }

    @Test
    void parseFindKeywordWithoutKeywordThrows() {
        assertThrows(SevenSixException.class, () -> Parser.parseFindKeyword("find"));
    }

    @Test
    void parseTaskNumberReturnsOneBasedNumber() throws SevenSixException {
        assertEquals(2, Parser.parseTaskNumber("mark 2", Parser.COMMAND_MARK));
    }

    @Test
    void parseTaskNumberWithNonNumericArgumentThrows() {
        assertThrows(SevenSixException.class, () -> Parser.parseTaskNumber("mark two", Parser.COMMAND_MARK));
    }
}
