package whimsybot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import whimsybot.WhimsyBotException;

/** Tests conversion of command words into command types. */
class CommandTypeTest {
    @Test
    void fromString_knownCommand_returnsMatchingType() throws WhimsyBotException {
        assertEquals(CommandType.LIST, CommandType.fromString("list"));
        assertEquals(CommandType.EVENT, CommandType.fromString("event"));
        assertEquals(CommandType.HELP, CommandType.fromString("help"));
        assertEquals(CommandType.BYE, CommandType.fromString("bye"));
    }

    @Test
    void fromString_unknownCommand_throwsException() {
        assertThrows(WhimsyBotException.class, () -> CommandType.fromString("unknown"));
    }
}
