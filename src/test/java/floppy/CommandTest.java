package floppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class CommandTest {
    @Test
    void constructors_differentCommandArguments_storeExpectedValues() {
        Command listCommand = new Command(CommandType.LIST);
        Command markCommand = new Command(CommandType.MARK, 2);
        Todo todo = new Todo("read book");
        Command addCommand = new Command(todo);
        Command findCommand = new Command("book");

        assertEquals(CommandType.LIST, listCommand.getCommandType());
        assertEquals(CommandType.MARK, markCommand.getCommandType());
        assertEquals(2, markCommand.getTaskIndex());
        assertEquals(CommandType.ADD, addCommand.getCommandType());
        assertSame(todo, addCommand.getTask());
        assertEquals(CommandType.FIND, findCommand.getCommandType());
        assertEquals("book", findCommand.getKeyword());
    }
}
