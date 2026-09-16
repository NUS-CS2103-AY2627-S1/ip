package rambo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import rambo.exception.RamboException;

/**
 * Tests the conversion and validation of user input performed by {@link Parser}.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommand_validCommand_returnsCommandCharacter() {
        assertEquals('q', parser.parseCommand("q"));
    }

    @Test
    void parseCommand_byeCommand_returnsQuitCommandCharacter() {
        assertEquals('q', parser.parseCommand("bye"));
    }

    @Test
    void parseCommand_unknownCommand_throwsRamboException() {
        assertThrows(RamboException.class, () -> parser.parseCommand("x"));
    }

    @Test
    void parseCommand_validCommandWithTrailingText_throwsRamboException() {
        assertThrows(RamboException.class, () -> parser.parseCommand("10"));
        assertThrows(RamboException.class, () -> parser.parseCommand("q anything"));
    }

    @Test
    void parseCommand_listCommandWithSearchTerm_returnsListCommandCharacter() {
        assertEquals('3', parser.parseCommand("3 milk"));
    }

    @Test
    void parseCommand_priorityMenuOption_returnsPriorityCommandCharacter() {
        assertEquals('6', parser.parseCommand("6"));
    }

    @Test
    void parseCommand_blankInput_throwsRamboException() {
        assertThrows(RamboException.class, () -> parser.parseCommand(""));
    }

    @Test
    void parseTaskType_numericInput_returnsTaskType() {
        assertEquals(2, parser.parseTaskType("2"));
    }

    @Test
    void parseTaskType_nonNumericInput_throwsRamboException() {
        assertThrows(RamboException.class, () -> parser.parseTaskType("not-a-number"));
    }

    @Test
    void parseTaskNumber_numericInput_returnsTaskNumber() {
        assertEquals(3, parser.parseTaskNumber("3"));
    }

    @Test
    void parseTaskNumber_nonNumericInput_throwsRamboException() {
        assertThrows(RamboException.class, () -> parser.parseTaskNumber("first"));
    }

    @Test
    void parsePriorityLevel_supportedLevel_returnsLevel() {
        assertEquals(1, parser.parsePriorityLevel("1"));
        assertEquals(3, parser.parsePriorityLevel("3"));
    }

    @Test
    void parsePriorityLevel_invalidInput_throwsRamboException() {
        assertThrows(RamboException.class, () -> parser.parsePriorityLevel("high"));
        assertThrows(RamboException.class, () -> parser.parsePriorityLevel("0"));
        assertThrows(RamboException.class, () -> parser.parsePriorityLevel("4"));
    }

    @Test
    void parseSearchTerm_commandWithKeyword_returnsTrimmedKeyword() {
        assertEquals("milk", parser.parseSearchTerm("3   milk "));
    }

    @Test
    void parseDate_isoDate_returnsLocalDate() {
        assertEquals(LocalDate.of(2026, 9, 15), Parser.parseDate("2026-09-15"));
    }

    @Test
    void parseDate_invalidDate_throwsRamboException() {
        assertThrows(RamboException.class, () -> Parser.parseDate("2026-02-30"));
    }
}
