package orbit.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import orbit.task.Todo;

class ParsedCommandTest {
    @Test
    void factories_inconsistentCommandPayload_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                ParsedCommand.withoutArguments(CommandType.MARK));
        assertThrows(AssertionError.class, () ->
                ParsedCommand.withTask(CommandType.FIND, new Todo("read book")));
        assertThrows(AssertionError.class, () ->
                ParsedCommand.withTaskNumber(CommandType.TODO, 1));
        assertThrows(AssertionError.class, () ->
                ParsedCommand.withKeyword(CommandType.LIST, "book"));
    }

    @Test
    void withKeyword_blankKeyword_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                ParsedCommand.withKeyword(CommandType.FIND, " "));
    }
}
