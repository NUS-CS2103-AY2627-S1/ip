package kafka.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests command identification for supported and unsupported input.
 */
class CommandTypeTest {
    @ParameterizedTest
    @CsvSource({
        "todo read book, TODO", "deadline report /by Friday, DEADLINE",
        "event meeting /from 2pm /to 3pm, EVENT", "mark 1, MARK", "unmark 1, UNMARK",
        "delete 1, DELETE", "rename 1 read novel, RENAME", "snooze 1 /by Sunday, SNOOZE", "find book, FIND"
    })
    void parseInput_commandWithArguments_returnsCommand(String input, CommandType expected) {
        assertEquals(expected, CommandType.parseInput(input));
    }

    @ParameterizedTest
    @EnumSource(value = CommandType.class, names = "UNKNOWN", mode = EnumSource.Mode.EXCLUDE)
    void parseInput_bareKeyword_returnsCommand(CommandType command) {
        assertEquals(command, CommandType.parseInput(command.getKeyword()));
    }

    @ParameterizedTest
    @EnumSource(value = CommandType.class, names = "UNKNOWN", mode = EnumSource.Mode.EXCLUDE)
    void parseInput_surroundingWhitespace_returnsCommand(CommandType command) {
        assertEquals(command, CommandType.parseInput(" \t\u2003" + command.getKeyword() + "\u2003\t "));
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo\tread book", "todo\u2003read book", " todo  read book "})
    void parseInput_whitespaceBeforeArguments_returnsCommand(String input) {
        assertEquals(CommandType.TODO, CommandType.parseInput(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "help", "Todo read book", "LIST", "todos", "snoozed 1 /by Sunday",
        "list extra", "bye extra"})
    void parseInput_unsupportedInput_returnsUnknown(String input) {
        assertEquals(CommandType.UNKNOWN, CommandType.parseInput(input));
    }
}
