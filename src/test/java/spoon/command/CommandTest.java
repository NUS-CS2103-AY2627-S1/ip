package spoon.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class CommandTest {

    @Nested
    class FromStringTests {

        @ParameterizedTest
        @CsvSource({
                "bye, BYE",
                "list, LIST",
                "mark, MARK",
                "unmark, UNMARK",
                "delete, DELETE",
                "on, ON",
                "by, BY",
                "find, FIND",
                "todo, TODO",
                "deadline, DEADLINE",
                "event, EVENT",
                "help, HELP"
        })
        void fromString_validLowerCase_returnsMatchingEnum(String input, Command expected) {
            assertEquals(expected, Command.fromString(input));
        }

        @ParameterizedTest
        @CsvSource({
                "BYE, BYE",
                "LiSt, LIST",
                "mArK, MARK",
                "UnMaRk, UNMARK",
                "DeLeTe, DELETE",
                "ToDo, TODO",
                "DEADLINE, DEADLINE"
        })
        void fromString_mixedCase_returnsMatchingEnum(String input, Command expected) {
            assertEquals(expected, Command.fromString(input));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void fromString_nullEmptyOrBlank_returnsUnknown(String input) {
            assertEquals(Command.UNKNOWN, Command.fromString(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "invalid",
                "unknown",
                "todoo",
                "deadlines",
                "events",
                "123",
                "!@#"
        })
        void fromString_unrecognizedInputs_returnsUnknown(String input) {
            assertEquals(Command.UNKNOWN, Command.fromString(input));
        }

        @Test
        void fromString_exactUnknownString_returnsUnknownEnum() {
            assertEquals(Command.UNKNOWN, Command.fromString("unknown"));
        }
    }
}
