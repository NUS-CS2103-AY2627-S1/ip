package plana.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTypeTest {

    @Test
    void parseInput_exactAndArgumentBearingCommands_correctTypesReturned() {
        assertEquals(CommandType.BYE, CommandType.parseInput("bye"));
        assertEquals(CommandType.LIST, CommandType.parseInput("list"));
        assertEquals(CommandType.TODO, CommandType.parseInput("todo buy milk"));
        assertEquals(CommandType.DEADLINE, CommandType.parseInput("deadline report /by 31/08/2026"));
        assertEquals(CommandType.EVENT, CommandType.parseInput("event meeting /from 31/08/2026 /to 01/09/2026"));
        assertEquals(CommandType.ON, CommandType.parseInput("on 31/08/2026"));
        assertEquals(CommandType.FIND, CommandType.parseInput("find book"));
        assertEquals(CommandType.DELETE, CommandType.parseInput("delete 1"));
        assertEquals(CommandType.MARK, CommandType.parseInput("mark 1"));
        assertEquals(CommandType.UNMARK, CommandType.parseInput("unmark 1"));
        assertEquals(CommandType.CLIENT, CommandType.parseInput("client list"));
        assertEquals(CommandType.TODO, CommandType.parseInput("  TODO   buy milk  "));
    }

    @Test
    void parseInput_helpAliasesAndCaseInsensitiveHelp_helpReturned() {
        assertEquals(CommandType.HELP, CommandType.parseInput("?"));
        assertEquals(CommandType.HELP, CommandType.parseInput("HELP"));
    }

    @Test
    void parseInput_unknownOrPartialCommand_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, CommandType.parseInput("todoist buy milk"));
        assertEquals(CommandType.BYE, CommandType.parseInput("bye now"));
        assertEquals(CommandType.HELP, CommandType.parseInput("please HELP me"));
        assertEquals(CommandType.UNKNOWN, CommandType.parseInput(""));
    }

    @Test
    void getCommandText_allCommandTypes_expectedTextReturned() {
        assertEquals("bye", CommandType.BYE.getCommandText());
        assertEquals("help", CommandType.HELP.getCommandText());
        assertEquals("list", CommandType.LIST.getCommandText());
        assertEquals("on", CommandType.ON.getCommandText());
        assertEquals("find", CommandType.FIND.getCommandText());
        assertEquals("delete", CommandType.DELETE.getCommandText());
        assertEquals("mark", CommandType.MARK.getCommandText());
        assertEquals("unmark", CommandType.UNMARK.getCommandText());
        assertEquals("client", CommandType.CLIENT.getCommandText());
        assertEquals("deadline", CommandType.DEADLINE.getCommandText());
        assertEquals("event", CommandType.EVENT.getCommandText());
        assertEquals("todo", CommandType.TODO.getCommandText());
        assertEquals("", CommandType.UNKNOWN.getCommandText());
    }
}
