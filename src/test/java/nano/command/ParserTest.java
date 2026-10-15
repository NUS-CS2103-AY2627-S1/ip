package nano.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void parseCommandRecognisesCommands() {
        Parser parser = new Parser();

        assertEquals(CommandType.BYE, parser.parseCommand("bye"));
        assertEquals(CommandType.LIST, parser.parseCommand("list"));
        assertEquals(CommandType.FIND, parser.parseCommand("find book"));
        assertEquals(CommandType.TODO, parser.parseCommand("todo homework"));
        assertEquals(CommandType.DEADLINE,
                parser.parseCommand("deadline submit report /by Friday"));
        assertEquals(CommandType.EVENT,
                parser.parseCommand("event meeting /from 2pm /to 3pm"));
    }

    @Test
    void parseCommandReturnsUnknownForInvalidCommand() {
        Parser parser = new Parser();

        assertEquals(CommandType.UNKNOWN, parser.parseCommand("hello"));
    }
}
