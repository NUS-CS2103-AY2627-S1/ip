package tangent.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;

public class CommandTypesTest {
    @Test
    public void fromInput_supportedKeywords_returnMatchingTypes() throws TangentException {
        assertEquals(CommandTypes.MARK, CommandTypes.fromInput("mark"));
        assertEquals(CommandTypes.UNMARK, CommandTypes.fromInput("unmark"));
        assertEquals(CommandTypes.DELETE, CommandTypes.fromInput("delete"));
        assertEquals(CommandTypes.TODO, CommandTypes.fromInput("todo"));
        assertEquals(CommandTypes.DEADLINE, CommandTypes.fromInput("deadline"));
        assertEquals(CommandTypes.EVENT, CommandTypes.fromInput("event"));
        assertEquals(CommandTypes.LIST, CommandTypes.fromInput("list"));
        assertEquals(CommandTypes.BYE, CommandTypes.fromInput("bye"));
        assertEquals(CommandTypes.FIND, CommandTypes.fromInput("find"));
    }

    @Test
    public void fromInput_unsupportedKeyword_throwsInvalidCommand() {
        TangentException exception = assertThrows(TangentException.class, () -> CommandTypes.fromInput("MARK"));

        assertEquals(ErrorMessages.INVALID_COMMAND_MESSAGE, exception.getMessage());
    }
}
