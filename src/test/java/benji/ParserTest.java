package benji;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class ParserTest {
    @Test
    public void parseToDoCommand_returnsToDoCommand() {
        Command command = Parser.getCommand("todo testing 12344");
        assertEquals(Command.TODO, command);
    }

    @Test
    public void parseUnKnownCommand_returnsUnknownCommand() {
        Command command = Parser.getCommand("need to book testing date");
        assertEquals(Command.UNKNOWN, command);
    }

    @Test
    public void parseHelpCommand_acceptsOnlyExactHelpCommand() {
        assertEquals(Command.HELP, Parser.getCommand("help"));
        assertEquals(Command.HELP, Parser.getCommand("Help"));
        assertEquals(Command.HELP, Parser.getCommand("HeLp"));
        assertEquals(Command.UNKNOWN, Parser.getCommand("Help me"));
    }

    @Test
    public void parseCommands_returnsMatchingCommands() {
        assertEquals(Command.LIST, Parser.getCommand("list"));
        assertEquals(Command.MARK, Parser.getCommand("mark 1"));
        assertEquals(Command.UNMARK, Parser.getCommand("unmark 1"));
        assertEquals(Command.DEADLINE,
                Parser.getCommand("deadline submit report /by 2026-09-14"));
        assertEquals(Command.EVENT,
                Parser.getCommand("event meeting /from 2pm /to 3pm"));
        assertEquals(Command.DELETE, Parser.getCommand("delete 1"));
        assertEquals(Command.FIND, Parser.getCommand("find homework"));
        assertEquals(Command.BYE, Parser.getCommand("bye"));
    }

    @Test
    public void parseBareArgumentCommands_returnsMatchingCommands() {
        assertEquals(Command.MARK, Parser.getCommand("mark"));
        assertEquals(Command.UNMARK, Parser.getCommand("unmark"));
        assertEquals(Command.TODO, Parser.getCommand("todo"));
        assertEquals(Command.DEADLINE, Parser.getCommand("deadline"));
        assertEquals(Command.EVENT, Parser.getCommand("event"));
        assertEquals(Command.DELETE, Parser.getCommand("delete"));
        assertEquals(Command.FIND, Parser.getCommand("find"));
    }
}
