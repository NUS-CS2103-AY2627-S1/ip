package odysseus.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandTest {
    @Test
    public void fromInput_knownWord_returnsCommand() {
        assertEquals(Command.DEADLINE,
                Command.fromInput("DEADLINE"));
    }

    @Test
    public void fromInput_unknownWord_returnsUnknown() {
        assertEquals(Command.UNKNOWN,
                Command.fromInput("garbage"));
    }

    @Test
    public void fromInput_mixedCaseWord_returnsCommand() {
        assertEquals(Command.TODO,
                Command.fromInput("tODO"));
    }
}
