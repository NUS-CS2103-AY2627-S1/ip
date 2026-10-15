package jeryl.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandTest {
    @Test
    public void fromKeyword_knownLowercaseKeyword_returnsMatchingCommand() {
        assertEquals(Command.TODO, Command.fromKeyword("todo"));
        assertEquals(Command.LIST, Command.fromKeyword("list"));
        assertEquals(Command.BYE, Command.fromKeyword("bye"));
    }

    @Test
    public void fromKeyword_unknownKeyword_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromKeyword("frobnicate"));
    }

    @Test
    public void fromKeyword_uppercaseKeyword_isCaseInsensitive() {
        assertEquals(Command.TODO, Command.fromKeyword("TODO"));
    }

    @Test
    public void fromKeyword_emptyString_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromKeyword(""));
    }
}
