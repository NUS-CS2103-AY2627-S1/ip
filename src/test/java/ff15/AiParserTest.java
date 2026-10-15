package ff15;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ff15.command.AiAskCommand;
import ff15.command.AiDoCommand;

/** Tests {@link Parser#parse(String)} on the AI commands, {@code @ai} and {@code @do}. */
public class AiParserTest {

    @Test
    public void parse_aiCommandsWithText_buildTheMatchingCommand() throws Exception {
        assertInstanceOf(AiAskCommand.class, Parser.parse("@ai how do I add a task?"));
        assertInstanceOf(AiDoCommand.class, Parser.parse("@do remind me to call Pam"));
    }

    @Test
    public void parse_aiCommandsWithExtraSpaces_stillParse() throws Exception {
        assertInstanceOf(AiAskCommand.class, Parser.parse("  @ai   what is on?  "));
    }

    @Test
    public void parse_aiCommandsWithNothingAfter_throwException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("@ai"));
        assertThrows(FF15Exception.class, () -> Parser.parse("@do"));
        assertThrows(FF15Exception.class, () -> Parser.parse("@ai   "));
    }

    @Test
    public void parse_aiWordRunIntoText_isUnknown() {
        assertThrows(FF15Exception.class, () -> Parser.parse("@aihello"));
        assertThrows(FF15Exception.class, () -> Parser.parse("@dothis"));
    }
}
