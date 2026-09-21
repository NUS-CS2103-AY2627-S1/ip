package janet.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandNameTest {
    @Test
    void from_knownCommands_returnsMatchingNames() {
        assertEquals(CommandName.LIST, CommandName.from("list"));
        assertEquals(CommandName.TODO, CommandName.from("todo read"));
        assertEquals(CommandName.EVENT, CommandName.from("event meeting"));
        assertEquals(CommandName.DEADLINE, CommandName.from("deadline submit"));
        assertEquals(CommandName.DELETE, CommandName.from("delete 1"));
        assertEquals(CommandName.MARK, CommandName.from("mark 1"));
        assertEquals(CommandName.UNMARK, CommandName.from("unmark 1"));
        assertEquals(CommandName.FIND, CommandName.from("find book"));
        assertEquals(CommandName.BYE, CommandName.from("bye"));
    }

    @Test
    void from_mixedCaseAndWhitespaceSeparator_recognizesCommandName() {
        assertEquals(CommandName.TODO, CommandName.from("ToDo read"));
        assertEquals(CommandName.FIND, CommandName.from("FIND\tbook"));
    }

    @Test
    void from_unknownOrEmptyCommand_returnsUnknown() {
        assertEquals(CommandName.UNKNOWN, CommandName.from("dance"));
        assertEquals(CommandName.UNKNOWN, CommandName.from(""));
    }
}
