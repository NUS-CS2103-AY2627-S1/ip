package labubu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests command-flow identification. */
public class CommandTest {
    @Test
    public void identifiesExactCommands() {
        assertEquals(Command.EXIT, identify("bye"));
        assertEquals(Command.EXIT, identify("quit"));
        assertEquals(Command.EXIT, identify("exit"));
        assertEquals(Command.EXIT, identify("EXIT"));
        assertEquals(Command.LIST, identify("list"));
        assertEquals(Command.HELP, identify("help"));
    }

    @Test
    public void identifiesCommandsWithArguments() {
        assertEquals(Command.TODO, identify("todo read book"));
        assertEquals(Command.DEADLINE, identify("deadline return book /by 06/07/2026"));
        assertEquals(Command.EVENT, identify("event meeting /from 06/07/2026 /to 07/07/2026"));
        assertEquals(Command.FIND, identify("find book"));
        assertEquals(Command.TASK_UPDATE, identify("mark 1"));
        assertEquals(Command.TASK_UPDATE, identify("unmark 1"));
        assertEquals(Command.TASK_UPDATE, identify("delete 1"));
    }

    @Test
    public void rejectsUnknownOrMalformedExactCommands() {
        assertEquals(Command.UNKNOWN, identify("unknown"));
        assertEquals(Command.UNKNOWN, identify("list extra"));
        assertEquals(Command.UNKNOWN, identify("help extra"));
        assertEquals(Command.UNKNOWN, identify("bye now"));
    }

    private Command identify(String input) {
        return Command.identify(input, input.split("\\s+"));
    }
}
