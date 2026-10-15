package es;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void parse_recognisesSupportedCommands() {
        assertEquals(Command.TODO, Parser.parse("todo read book"));
        assertEquals(Command.DEADLINE, Parser.parse("deadline report /by 2026-08-28"));
        assertEquals(Command.EVENT, Parser.parse("event meeting /from 2026-08-28 /to 2026-08-29"));
        assertEquals(Command.BYE, Parser.parse("bye"));
    }

    @Test
    void parse_rejectsUnknownOrPrefixedCommands() {
        assertNull(Parser.parse("blah"));
        assertNull(Parser.parse("toddlers"));
        assertNull(Parser.parse(" Todo read book"));
    }
}
