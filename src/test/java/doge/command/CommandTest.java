package doge.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import doge.exception.DogeException;

/** Tests conversion from command keywords to {@link Command} values. */
class CommandTest {

    /** Verifies that every supported keyword maps to the expected command. */
    @Test
    void fromText_supportedKeywords_returnsMatchingCommands() throws DogeException {
        assertEquals(Command.TODO, Command.fromText("todo"));
        assertEquals(Command.DEADLINE, Command.fromText("deadline"));
        assertEquals(Command.EVENT, Command.fromText("event"));
        assertEquals(Command.MARK, Command.fromText("mark"));
        assertEquals(Command.UNMARK, Command.fromText("unmark"));
        assertEquals(Command.PRIORITY, Command.fromText("priority"));
        assertEquals(Command.UNPRIORITY, Command.fromText("unpriority"));
        assertEquals(Command.LIST, Command.fromText("list"));
        assertEquals(Command.FIND, Command.fromText("find"));
        assertEquals(Command.BYE, Command.fromText("bye"));
        assertEquals(Command.DELETE, Command.fromText("delete"));
    }

    /** Verifies that unsupported keywords produce a user-facing exception. */
    @Test
    void fromText_unsupportedKeyword_throwsDogeException() {
        DogeException exception = assertThrows(DogeException.class, () -> Command.fromText("fetch"));

        assertEquals("I don't understand that command", exception.getMessage());
    }

    /** Verifies that blank and null keywords produce a helpful exception. */
    @Test
    void fromText_blankOrNullKeyword_throwsDogeException() {
        assertEquals("Please enter a command.",
                assertThrows(DogeException.class, () -> Command.fromText(" ")).getMessage());
        assertEquals("Please enter a command.",
                assertThrows(DogeException.class, () -> Command.fromText(null)).getMessage());
    }
}
