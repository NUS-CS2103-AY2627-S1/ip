package avon.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import avon.exception.UnknownCommandException;

class CommandTypeTest {
    @Test
    void parse_everyExactKeyword_returnsMatchingType() throws UnknownCommandException {
        for (CommandType commandType : CommandType.values()) {
            assertEquals(commandType, CommandType.parse(commandType.getKeyword()));
        }
    }

    @Test
    void parse_argumentCommandWithWhitespace_returnsMatchingType() throws UnknownCommandException {
        assertEquals(CommandType.TODO, CommandType.parse("todo rehearse Hamlet"));
        assertEquals(CommandType.MARK, CommandType.parse("mark\t1"));
    }

    @Test
    void parse_keywordPrefixOrArgumentFreeCommandWithArguments_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> CommandType.parse("todoist"));
        assertThrows(UnknownCommandException.class, () -> CommandType.parse("list all"));
        assertThrows(UnknownCommandException.class, () -> CommandType.parse("bye now"));
    }

    @Test
    void getSupportedKeywords_returnsEveryKeywordInDisplayOrder() {
        assertEquals("todo, deadline, event, list, find, mark, unmark, delete, help, bye",
                CommandType.getSupportedKeywords());
    }
}
