package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests command boundaries independently of argument validation and execution.
 */
public class CommandTypeTest {
    @Test
    public void from_bareArgumentCommands_recognizesCommandsForMissingArgumentValidation() {
        String[] inputs = {"mark", "unmark", "delete", "todo", "deadline", "event", "on", "find"};
        CommandType[] expected = {CommandType.MARK, CommandType.UNMARK, CommandType.DELETE,
            CommandType.TODO, CommandType.DEADLINE, CommandType.EVENT, CommandType.ON, CommandType.FIND};
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(expected[i], CommandType.from(inputs[i]), inputs[i]);
        }
    }

    @Test
    public void from_commandWordPrefixes_doesNotMistakeOtherWordsForCommands() {
        for (String input : new String[] {"marking 1", "unmarked 1", "deleteAll", "todo-list",
            "deadlines book", "events meeting", "only today", "finder book", "helpful", "byebye"}) {
            assertEquals(CommandType.UNKNOWN, CommandType.from(input), input);
        }
    }

    @Test
    public void from_argumentPolicy_acceptsSpacesOnlyForArgumentCommands() {
        assertEquals(CommandType.FIND, CommandType.from("find   two words"));
        assertEquals(CommandType.TODO, CommandType.from("todo   read book"));
        for (String input : new String[] {"list 1", "help todo", "bye now"}) {
            assertEquals(CommandType.UNKNOWN, CommandType.from(input), input);
        }
    }
}
