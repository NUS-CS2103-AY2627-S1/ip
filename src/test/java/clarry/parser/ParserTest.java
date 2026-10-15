package clarry.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import clarry.ClarryException;

/** Tests command parsing and validation behavior. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_knownAndUnknownCommands_returnsExpectedTypes() {
        assertEquals(CommandType.HELP, parser.parseCommandType("help"));
        assertEquals(CommandType.TODO, parser.parseCommandType("todo read book"));
        assertEquals(CommandType.FIND, parser.parseCommandType("find book"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("dance"));
    }

    @Test
    void parseTodo_validInput_returnsTodoWithTrimmedDescription() throws ClarryException {
        assertEquals("read book", parser.parseTodo("todo   read book").getDescription());
    }

    @Test
    void parseTodo_missingDescription_throwsClarryException() {
        assertThrows(ClarryException.class, () -> parser.parseTodo("todo"));
    }

    @Test
    void parseDeadline_invalidDate_throwsClarryException() {
        assertThrows(ClarryException.class, () -> parser.parseDeadline("deadline submit report /by not-a-date"));
    }

    @Test
    void parseDate_validIsoDate_returnsDate() throws ClarryException {
        assertEquals(LocalDate.of(2026, 8, 27), parser.parseDate("on 2026-08-27"));
    }

    @Test
    void parseIndex_outOfRange_throwsClarryException() {
        assertThrows(ClarryException.class, () -> parser.parseIndex("delete 3", "delete", 2));
    }

    @Test
    void parseFindKeyword_validInput_returnsTrimmedKeyword() throws ClarryException {
        assertEquals("read book", parser.parseFindKeyword("find   read book"));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsClarryException() {
        assertThrows(ClarryException.class, () -> parser.parseFindKeyword("find"));
    }
}
