package dingleberry.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests command recognition independently of argument validation. */
class CommandWordTest {
    @Test
    void fromInput_exactKeywords_matchesIgnoringCase() {
        for (CommandWord command : CommandWord.values()) {
            assertSame(command, CommandWord.fromInput(command.keyword()));
            assertSame(command, CommandWord.fromInput(
                    command.keyword().toUpperCase(Locale.ROOT)));
        }
    }

    @Test
    void fromInput_arguments_matchesEveryCommandExceptBye() {
        for (CommandWord command : CommandWord.values()) {
            String input = command.keyword().toUpperCase(Locale.ROOT)
                    + " argument";
            if (command == CommandWord.BYE) {
                assertNull(CommandWord.fromInput(input));
                assertNull(CommandWord.fromInput("bye "));
            } else {
                assertSame(command, CommandWord.fromInput(input));
                assertSame(command,
                        CommandWord.fromInput(command.keyword() + " "));
            }
        }
    }

    @Test
    void fromInput_misleadingPrefixesAndUnnormalizedInput_returnsNull() {
        for (CommandWord command : CommandWord.values()) {
            assertNull(CommandWord.fromInput(command.keyword() + "extra"));
            assertNull(CommandWord.fromInput(command.keyword() + "\targument"));
            assertNull(CommandWord.fromInput(" " + command.keyword()));
        }
        assertNull(CommandWord.fromInput(""));
        assertNull(CommandWord.fromInput("unknown"));
    }

    @Test
    void isExactInput_mixedCase_matchesOnlyWholeKeyword() {
        assertTrue(CommandWord.TODO.isExactInput("ToDo"));
        assertFalse(CommandWord.TODO.isExactInput("todo description"));
        assertFalse(CommandWord.TODO.isExactInput("todo "));
    }
}
