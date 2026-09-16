package mira.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mira.task.Todo;

class CommandTest {
    @Test
    void factoryMethods_invalidInternalState_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                Command.withoutArguments(CommandType.TODO));
        assertThrows(AssertionError.class, () ->
                Command.withTask(CommandType.LIST, new Todo("read")));
        assertThrows(AssertionError.class, () ->
                Command.withTaskNumber(CommandType.MARK, 0));
        assertThrows(AssertionError.class, () ->
                Command.withKeyword(CommandType.FIND, " "));
    }
}
