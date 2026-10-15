package charliek.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests command keyword recognition and argument extraction.
 */
class CommandTypeTest {
    /**
     * Verifies that every supported command keyword is recognized.
     */
    @Test
    void getCommandFromInput_supportedKeywords_returnsMatchingCommandType() {
        assertEquals(Optional.of(CommandType.BYE), CommandType.getCommandFromInput("bye"));
        assertEquals(Optional.of(CommandType.LIST), CommandType.getCommandFromInput("list"));
        assertEquals(Optional.of(CommandType.MARK), CommandType.getCommandFromInput("mark 1"));
        assertEquals(Optional.of(CommandType.UNMARK), CommandType.getCommandFromInput("unmark 1"));
        assertEquals(Optional.of(CommandType.DELETE), CommandType.getCommandFromInput("delete 1"));
        assertEquals(Optional.of(CommandType.TODO), CommandType.getCommandFromInput("todo read book"));
        assertEquals(Optional.of(CommandType.DEADLINE),
                CommandType.getCommandFromInput("deadline return book /by 2019-12-02"));
        assertEquals(Optional.of(CommandType.EVENT),
                CommandType.getCommandFromInput("event meeting /from 2019-12-02 /to 2019-12-03"));
        assertEquals(Optional.of(CommandType.FIND), CommandType.getCommandFromInput("find book"));
        assertEquals(Optional.of(CommandType.HELP), CommandType.getCommandFromInput("help"));
        assertEquals(Optional.of(CommandType.HELP), CommandType.getCommandFromInput("help list"));
    }

    /**
     * Verifies that unknown, null, and invalid argument forms are rejected.
     */
    @Test
    void getCommandFromInput_unknownOrInvalidInput_returnsEmptyOptional() {
        assertTrue(CommandType.getCommandFromInput(null).isEmpty());
        assertTrue(CommandType.getCommandFromInput("").isEmpty());
        assertTrue(CommandType.getCommandFromInput(" todo read book").isEmpty());
        assertTrue(CommandType.getCommandFromInput("todo  read book").isEmpty());
        assertTrue(CommandType.getCommandFromInput("todo read\u0000book").isEmpty());
        assertTrue(CommandType.getCommandFromInput("unknown command").isEmpty());
        assertTrue(CommandType.getCommandFromInput("bye now").isEmpty());
        assertTrue(CommandType.getCommandFromInput("todoist something").isEmpty());
    }

    /**
     * Verifies extraction for exact commands and commands with arguments.
     */
    @Test
    void getArgumentFromInput_exactAndArgumentInputs_returnsTextAfterKeyword() {
        assertEquals("", CommandType.BYE.getArgumentFromInput("bye"));
        assertEquals("", CommandType.TODO.getArgumentFromInput("todo"));
        assertEquals("", CommandType.TODO.getArgumentFromInput("todo "));
        assertEquals("read book", CommandType.TODO.getArgumentFromInput("todo read book"));
        assertEquals("1", CommandType.MARK.getArgumentFromInput("mark 1"));
    }

    /**
     * Verifies that a null input has no argument.
     */
    @Test
    void getArgumentFromInput_nullInput_returnsEmptyString() {
        assertEquals("", CommandType.TODO.getArgumentFromInput(null));
    }

    /**
     * Verifies that exact command keywords can be resolved for detailed help.
     */
    @Test
    void getCommandFromKeyword_knownAndUnknownKeywords_returnsExpectedResult() {
        assertEquals(Optional.of(CommandType.LIST), CommandType.getCommandFromKeyword("list"));
        assertTrue(CommandType.getCommandFromKeyword("unknown").isEmpty());
        assertTrue(CommandType.getCommandFromKeyword(null).isEmpty());
    }

    /**
     * Verifies that every command definition exposes complete help metadata.
     */
    @Test
    void commandMetadata_allCommands_containsNonBlankValues() {
        for (CommandType command : CommandType.values()) {
            assertTrue(!command.getKeyword().isBlank());
            assertTrue(!command.getUsage().isBlank());
            assertTrue(!command.getDescription().isBlank());
            assertTrue(!command.getExample().isBlank());
            assertEquals(command, CommandType.getCommandFromKeyword(command.getKeyword()).orElseThrow());
        }
    }

    /**
     * Verifies that separators and control characters invalidating a full input are rejected.
     */
    @Test
    void getCommandFromInput_invalidSeparators_returnsEmptyOptional() {
        assertTrue(CommandType.getCommandFromInput("list\n").isEmpty());
        assertTrue(CommandType.getCommandFromInput("list\rtime").isEmpty());
        assertTrue(CommandType.getCommandFromInput("list\u2003time").isEmpty());
        assertTrue(CommandType.getCommandFromInput("list   time").isEmpty());
        assertTrue(CommandType.getCommandFromInput("list time ").isEmpty());
    }
}
