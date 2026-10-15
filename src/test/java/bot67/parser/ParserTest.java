package bot67.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bot67.exception.Bot67Exception;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_recognisesOnlyExitCommand() throws Bot67Exception {
        assertNotNull(parser.parse("bye", "art"));
        assertNull(parser.parse("list", "art"));
    }

    @Test
    void parseTaskNumber_acceptsTrimmedRangeAndRejectsInvalidValues() throws Bot67Exception {
        assertEquals(7, parser.parseTaskNumber(" 7 "));
        assertThrows(Bot67Exception.class, () -> parser.parseTaskNumber("abc"));
        assertThrows(Bot67Exception.class, () -> parser.parseTaskNumber("0"));
        assertEquals(101, parser.parseTaskNumber("101"));
        assertThrows(Bot67Exception.class, () -> parser.parseTaskNumber("999999999999999999"));
    }

    @Test
    void commandValidation_acceptsValidCommandsAndRejectsMalformedOnes() throws Bot67Exception {
        parser.requireText("task");
        parser.requireValidDeadline("deadline report /by 2026-10-15");
        parser.requireValidEvent("event meeting /from 2026-10-15T14:00 /to 2026-10-15T16:00");
        assertThrows(Bot67Exception.class, () -> parser.requireText("  "));
        assertThrows(Bot67Exception.class, () -> parser.requireValidDeadline("deadline report /by"));
        assertThrows(Bot67Exception.class, () -> parser.requireValidEvent("event meeting /from x /to"));
    }
}
