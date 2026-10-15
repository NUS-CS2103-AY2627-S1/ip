package nabmak.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Parser}.
 */
public class ParserTest {

    private NabmakException parseExpectingError(String input, int taskNum, int placeNum) {
        return assertThrows(NabmakException.class, () -> Parser.parse(input, taskNum, placeNum));
    }

    @Test
    public void parse_validCommands_noExceptionThrown() {
        assertDoesNotThrow(() -> Parser.parse("todo read book", 0, 0));
        assertDoesNotThrow(() -> Parser.parse("deadline return book /by 01-12-2026 18:00", 0, 0));
        assertDoesNotThrow(() -> Parser.parse("event book review /from 01-12-2026 09:00 /to 02-12-2026 17:00", 0, 0));
        assertDoesNotThrow(() -> Parser.parse("list", 0, 0));
        assertDoesNotThrow(() -> Parser.parse("bye", 0, 0));
        assertDoesNotThrow(() -> Parser.parse("find book", 0, 0));
        assertDoesNotThrow(() -> Parser.parse("place add Library /details Level 2", 0, 0));
    }

    @Test
    public void parse_todoWithoutDescription_throwsException() {
        assertEquals("You need to say what task you wanna do.",
                parseExpectingError("todo", 0, 0).getMessage());
    }

    @Test
    public void parse_deadlineMissingBy_throwsException() {
        assertEquals("Deadline must have a '<description> /by <date>'.",
                parseExpectingError("deadline return book", 0, 0).getMessage());
    }

    @Test
    public void parse_deadlineEmptyDescription_throwsException() {
        assertEquals("Deadline cant have empty description",
                parseExpectingError("deadline /by 01-12-2026 18:00", 0, 0).getMessage());
    }

    @Test
    public void parse_deadlineBadDateFormat_throwsException() {
        assertEquals("Deadline date must be 'dd-MM-yyyy HH:mm'.",
                parseExpectingError("deadline return book /by tomorrow", 0, 0).getMessage());
        assertEquals("Deadline date must be 'dd-MM-yyyy HH:mm'.",
                parseExpectingError("deadline return book /by 32-13-2026 18:00", 0, 0).getMessage());
    }

    @Test
    public void parse_eventMissingTo_throwsException() {
        assertEquals("Event must have a '<description> /from <date> /to <date>'.",
                parseExpectingError("event book review /from 01-12-2026 09:00", 0, 0).getMessage());
    }

    @Test
    public void parse_eventBadDates_throwsException() {
        assertEquals("Event start date must be 'dd-MM-yyyy HH:mm'.",
                parseExpectingError("event book review /from monday /to 02-12-2026 17:00", 0, 0)
                        .getMessage());
        assertEquals("Event end date must be 'dd-MM-yyyy HH:mm'.",
                parseExpectingError("event book review /from 01-12-2026 09:00 /to friday", 0, 0)
                        .getMessage());
    }

    @Test
    public void parse_markOutOfRange_throwsException() {
        assertEquals("You arent that busy", parseExpectingError("mark 3", 2, 0).getMessage());
        assertEquals("You arent that busy", parseExpectingError("mark 0", 2, 0).getMessage());
        assertDoesNotThrow(() -> Parser.parse("mark 2", 2, 0));
    }

    @Test
    public void parse_nonNumericTaskNumber_throwsException() {
        assertEquals("Give valid task number", parseExpectingError("delete abc", 2, 0).getMessage());
    }

    @Test
    public void parse_placeCommands_validatedCorrectly() {
        assertEquals("Place must have a '<name> /details <details>'.",
                parseExpectingError("place add Library", 0, 0).getMessage());
        assertEquals("You dont have that many places",
                parseExpectingError("place delete 2", 0, 1).getMessage());
        assertEquals("Use place add, place list or place delete.",
                parseExpectingError("place idk", 0, 0).getMessage());
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        parseExpectingError("dance", 0, 0);
    }

}
