package orbit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandTypeTest {
    @Test
    public void from_knownCommand_returnsMatchingType() {
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline return book /by 2026-08-30"));
    }

    @Test
    public void from_mixedCaseCommand_isCaseInsensitive() {
        assertEquals(CommandType.LIST, CommandType.from("LiSt"));
    }

    @Test
    public void from_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("remind me"));
    }
}
