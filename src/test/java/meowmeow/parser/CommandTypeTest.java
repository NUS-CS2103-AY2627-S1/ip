package meowmeow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandTypeTest {

    @Test
    public void fromString_lowerCaseWord_commandReturned() {
        assertEquals(CommandType.DEADLINE, CommandType.fromString("deadline"));
    }

    @Test
    public void fromString_mixedCase_commandReturned() {
        assertEquals(CommandType.LIST, CommandType.fromString("LiSt"));
    }

    @Test
    public void fromString_unrecognisedWord_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromString("dance"));
    }

    @Test
    public void fromString_emptyString_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromString(""));
    }
}