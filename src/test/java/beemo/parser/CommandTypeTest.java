package beemo.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTypeTest {
    @Test
    void from_supportedCommands_correctTypes() {
        assertAll(
                () -> assertEquals(CommandType.BYE, CommandType.from("bye")),
                () -> assertEquals(CommandType.HELP, CommandType.from("help")),
                () -> assertEquals(CommandType.LIST, CommandType.from("list")),
                () -> assertEquals(CommandType.MARK, CommandType.from("mark 1")),
                () -> assertEquals(CommandType.UNMARK, CommandType.from("unmark 1")),
                () -> assertEquals(CommandType.DELETE, CommandType.from("delete 1")),
                () -> assertEquals(CommandType.FIND, CommandType.from("find book")),
                () -> assertEquals(CommandType.TODO, CommandType.from("todo read")),
                () -> assertEquals(CommandType.DEADLINE,
                        CommandType.from("deadline return /by 2026-08-30")),
                () -> assertEquals(CommandType.EVENT,
                        CommandType.from("event meeting /from 2pm /to 4pm")));
    }

    @Test
    void from_unsupportedOrSimilarCommands_unknownType() {
        assertAll(
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("bye now")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("help me")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("list later")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("marking 1")),
                () -> assertEquals(CommandType.UNKNOWN, CommandType.from("blah")));
    }
}
