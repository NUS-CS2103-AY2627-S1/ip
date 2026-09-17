package yachiyo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static yachiyo.TestAssertions.assertYachiyoException;
import static yachiyo.exception.ErrorCategory.WARNING;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import yachiyo.exception.YachiyoException;

/**
 * Tests conversion of command words into {@link CommandType} values.
 */
public class CommandTypeTest {
    @Test
    public void parse_allSupportedCommands_matchingTypesReturned() throws YachiyoException {
        for (CommandType expectedType : CommandType.values()) {
            assertEquals(expectedType, CommandType.parse(expectedType.name()));
        }
    }

    @Test
    public void parse_lowercaseCommand_matchingTypeReturned() throws YachiyoException {
        for (CommandType expectedType : CommandType.values()) {
            String lowercaseCommand = expectedType.name().toLowerCase(Locale.ROOT);
            assertEquals(expectedType, CommandType.parse(lowercaseCommand));
        }
    }

    @Test
    public void parse_mixedCaseCommand_matchingTypeReturned() throws YachiyoException {
        assertEquals(CommandType.DEADLINE, CommandType.parse("DeAdLiNe"));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertYachiyoException(WARNING, () -> CommandType.parse("unknown"));
    }

    @Test
    public void parse_emptyCommand_exceptionThrown() {
        assertYachiyoException(WARNING, () -> CommandType.parse(""));
    }
}
