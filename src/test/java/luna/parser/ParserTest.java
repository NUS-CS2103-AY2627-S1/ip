package luna.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import luna.exception.LunaException;

public class ParserTest {
    private static final String TODO_FORMAT_MESSAGE =
            "The description of a todo cannot be empty. Example: todo read book";

    private static final String DEADLINE_FORMAT_MESSAGE =
            "A deadline must include a description and end date in "
            + "this format: deadline <desc> /by <end>\n"
            + "Example: deadline return book /by 2019-10-15";

    @Test
    void parse_commandAliases_resolvesCanonicalCommands() throws LunaException {
        String[][] aliasMappings = {
            {"ls", "list"},
            {"t", "todo"},
            {"d", "deadline"},
            {"e", "event"},
            {"m", "mark"},
            {"u", "unmark"},
            {"del", "delete"},
            {"f", "find"},
            {"q", "bye"}
        };

        for (String[] aliasMapping : aliasMappings) {
            assertEquals(aliasMapping[1], Parser.parse(aliasMapping[0])[0]);
        }
    }

    @Test
    void parse_uppercaseAliasWithArguments_resolvesCommandAndPreservesArguments() throws LunaException {
        assertArrayEquals(new String[] {"todo", "read book"}, Parser.parse("T read book"));
    }

    // =======================
    // Tests for parseTodo()
    // =======================

    @Test
    void parseTodo_validInput_success() throws LunaException {
        // valid input
        assertEquals("read book", Parser.parseTodo("read book"));

        // valid input with extra spaces
        assertEquals("read book", Parser.parseTodo("   read book   "));
    }

    @Test
    void parseTodo_invalidInputs_exceptionThrown() {
        String[] inputs = { "", "   ", null };

        for (String input : inputs) {
            // assertThrows(expected exception, actual exception, failure message if there's mismatch)
            LunaException ex = assertThrows(LunaException.class, () -> Parser.parseTodo(input),
                    "Expected LunaException for input: " + input);

            // assertEquals(expected ex message, actual ex message, failure message if there's mismatch)
            assertEquals(
                    TODO_FORMAT_MESSAGE,
                    ex.getMessage(),
                    "Incorrect exception message for input: " + input);
        }
    }

    // =========================
    // Tests for parseDeadline()
    // =========================

    @Test
    void parseDeadline_validInput_success() throws LunaException {
        // valid input
        assertArrayEquals(
                new String[] { "return book", "2019-10-15" },
                Parser.parseDeadline("return book /by 2019-10-15"));

        // valid input with extra spaces
        assertArrayEquals(
                new String[] { "return book", "2019-10-15" },
                Parser.parseDeadline("  return book   /by    2019-10-15   "));
    }

    @Test
    void parseDeadline_invalidFormats_exceptionThrown() {
        String[] inputs = {
            "",
            "   ",
            "return book",
            "return book /by",
            " /by 2019-10-15"
        };

        for (String input : inputs) {
            LunaException ex = assertThrows(LunaException.class, () -> Parser.parseDeadline(input),
                    "Expected LunaException for input: " + input);

            assertEquals(DEADLINE_FORMAT_MESSAGE,
                    ex.getMessage(),
                    "Incorrect exception message for input: " + input);
        }
    }

}
