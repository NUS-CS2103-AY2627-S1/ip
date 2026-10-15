package megatron.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import megatron.command.Command;
import megatron.exception.MegatronException;

/** Tests command classification and parsing, which are central to the chatbot's behavior. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void getCommandType_supportedCommands_returnExpectedTypes() {
        assertEquals(CommandType.TODO, parser.getCommandType("todo buy milk"));
        assertEquals(CommandType.DEADLINE, parser.getCommandType("deadline submit report /by 2026-08-27"));
        assertEquals(CommandType.EVENT, parser.getCommandType("event meeting /from 2026-08-27 /to 2026-08-28"));
        assertEquals(CommandType.LIST, parser.getCommandType("list"));
        assertEquals(CommandType.MARK, parser.getCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, parser.getCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, parser.getCommandType("delete 1"));
        assertEquals(CommandType.SNOOZE, parser.getCommandType("snooze 1 2026-09-10"));
        assertEquals(CommandType.BYE, parser.getCommandType("bye"));
    }

    @Test
    void getCommandType_keywordPrefixWithoutSpace_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("todoist task"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("listing"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType(""));
    }

    @Test
    void getCommandType_findCommand_returnsFindType() {
        assertEquals(CommandType.FIND, parser.getCommandType("find book"));
        assertEquals(CommandType.FIND, parser.getCommandType("find"));
    }

    @Test
    void parse_supportedCommands_returnsCommand() throws MegatronException {
        assertNotNull(parser.parse("todo buy milk"));
        assertNotNull(parser.parse("deadline submit report /by 2026-08-27"));
        assertNotNull(parser.parse("event meeting /from 2026-08-27 /to 2026-08-28"));
        assertNotNull(parser.parse("list"));
        assertNotNull(parser.parse("mark 1"));
        assertNotNull(parser.parse("unmark 1"));
        assertNotNull(parser.parse("delete 1"));
        assertNotNull(parser.parse("snooze 1 2026-09-10"));
    }

    @Test
    void parse_exitCommand_commandIsExit() throws MegatronException {
        Command command = parser.parse("bye");
        assertTrue(command.isExit());
    }

    @Test
    void parse_unknownCommand_exceptionThrown() {
        assertThrows(MegatronException.class, () -> parser.parse("remind me"));
    }

    @Test
    void parse_malformedSpacing_exceptionThrown() {
        assertThrows(MegatronException.class, () -> parser.parse(" todo buy milk"));
        assertThrows(MegatronException.class, () -> parser.parse("todo  buy milk"));
        assertThrows(MegatronException.class, () -> parser.parse("todo buy milk "));
    }

    @Test
    void parse_blankInput_exceptionThrown() {
        assertThrows(MegatronException.class, () -> parser.parse("   "));
        assertThrows(MegatronException.class, () -> parser.parse(null));
    }

    @Test
    void parse_nonExitCommand_commandIsNotExit() throws MegatronException {
        assertFalse(parser.parse("list").isExit());
    }

    @Test
    void parse_findCommand_commandIsNotExit() throws MegatronException {
        assertFalse(parser.parse("find book").isExit());
    }
}
