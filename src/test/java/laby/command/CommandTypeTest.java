package laby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests conversion of every supported command word. */
class CommandTypeTest {

    @Test
    void from_supportedCommandWords_returnsExpectedTypes() {
        assertEquals(CommandType.BYE, CommandType.from("bye"));
        assertEquals(CommandType.LIST, CommandType.from("list"));
        assertEquals(CommandType.MARK, CommandType.from("mark"));
        assertEquals(CommandType.UNMARK, CommandType.from("unmark"));
        assertEquals(CommandType.DELETE_TASK, CommandType.from("deletetask"));
        assertEquals(CommandType.TODO, CommandType.from("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
        assertEquals(CommandType.EVENT, CommandType.from("event"));
        assertEquals(CommandType.FIND, CommandType.from("find"));
        assertEquals(CommandType.CONTACT, CommandType.from("contact"));
        assertEquals(CommandType.DELETE_CONTACT, CommandType.from("deletecontact"));
    }

    @Test
    void from_nullOrUnsupportedCommandWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("ARCHIVE"));
        assertEquals(CommandType.UNKNOWN, CommandType.from(null));
    }
}
