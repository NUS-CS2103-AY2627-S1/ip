package kong.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CommandTypeTest {
    @ParameterizedTest
    @CsvSource({
        "todo, TODO",
        "TODO, TODO",
        "deadline, DEADLINE",
        "DEADLINE, DEADLINE",
        "event, EVENT",
        "list, LIST",
        "find, FIND",
        "on, ON",
        "mark, MARK",
        "unmark, UNMARK",
        "delete, DELETE",
        "bye, BYE"
    })
    void fromString_validCommandWords_returnsCorrespondingType(String input, CommandType expected) {
        assertEquals(expected, CommandType.fromString(input));
    }

    @Test
    void fromString_unrecognizedWords_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromString("unknown"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromString("blah"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromString("12345"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromString(""));
    }
}
