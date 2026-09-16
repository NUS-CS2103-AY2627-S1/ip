package tt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTest {

    @Test
    void findCommand_isRecognized() {
        assertEquals(Command.FIND, Command.fromString("find"));
    }

    @Test
    void fromString_mixedCaseCommand_returnsMatchingCommand() {
        assertEquals(Command.DEADLINE, Command.fromString("DeAdLiNe"));
    }

    @Test
    void fromString_nullOrUnsupportedCommand_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromString(null));
        assertEquals(Command.UNKNOWN, Command.fromString("postpone"));
    }
}
