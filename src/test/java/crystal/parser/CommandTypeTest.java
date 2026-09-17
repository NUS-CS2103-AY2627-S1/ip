package crystal.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests the stable command keywords exposed by {@link CommandType}.
 */
public class CommandTypeTest {
    /** Verifies the stable user-facing keyword associated with every command type. */
    @Test
    public void getKeyword_allCommandTypes_returnExpectedVocabulary() {
        Map<CommandType, String> expectedKeywords = Map.ofEntries(
                Map.entry(CommandType.TODO, "todo"),
                Map.entry(CommandType.DEADLINE, "deadline"),
                Map.entry(CommandType.EVENT, "event"),
                Map.entry(CommandType.WITHIN, "within"),
                Map.entry(CommandType.LIST, "list"),
                Map.entry(CommandType.FIND, "find"),
                Map.entry(CommandType.MARK, "mark"),
                Map.entry(CommandType.UNMARK, "unmark"),
                Map.entry(CommandType.DELETE, "delete"),
                Map.entry(CommandType.HELP, "help"),
                Map.entry(CommandType.EXIT, "bye"),
                Map.entry(CommandType.UNKNOWN, ""));

        for (Map.Entry<CommandType, String> entry : expectedKeywords.entrySet()) {
            assertEquals(entry.getValue(), entry.getKey().getKeyword());
        }
    }
}
