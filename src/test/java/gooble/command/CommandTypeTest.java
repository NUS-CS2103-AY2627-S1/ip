package gooble.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests command-word mapping and handler selection. */
class CommandTypeTest {
    @Test
    void fromString_knownCommand_returnsMatchingType() {
        assertEquals(CommandType.TODO, CommandType.fromString("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.fromString("DeAdLiNe"));
    }

    @Test
    void fromString_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromString("launch"));
    }

    @Test
    void handler_supportedCommand_returnsCorrectHandler() {
        assertEquals(TodoCommand.class, CommandType.TODO.handler("todo read book").getClass());
        assertEquals(ByeCommand.class, CommandType.BYE.handler("bye").getClass());
    }

    @Test
    void handler_unknownCommand_returnsNull() {
        assertNull(CommandType.UNKNOWN.handler("unknown"));
    }
}
