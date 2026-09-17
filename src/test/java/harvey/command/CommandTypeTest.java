package harvey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import harvey.HarveyException;

/**
 * Tests {@link CommandType}, which decides whether a typed word is a command at all.
 * This is the first thing every line goes through, so its error messages are the ones
 * users see most often.
 */
public class CommandTypeTest {
    @Test
    public void fromKeyword_everyKnownKeyword_returnsMatchingType() throws HarveyException {
        // Looping over values() means a command added later is covered automatically.
        for (CommandType type : CommandType.values()) {
            assertEquals(type, CommandType.fromKeyword(type.getKeyword()));
        }
    }

    @Test
    public void fromKeyword_unknownWord_exceptionNamesTheWordAndListsCommands()
            throws HarveyException {
        HarveyException e = assertThrows(
                HarveyException.class, () -> CommandType.fromKeyword("blah"));
        assertTrue(e.getMessage().contains("\"blah\""),
                "the message should quote what the user typed: " + e.getMessage());
        assertTrue(e.getMessage().contains("todo"),
                "the message should list the commands: " + e.getMessage());
    }

    @Test
    public void fromKeyword_emptyString_exceptionThrown() {
        // Reached when the user presses Enter on a blank line.
        assertThrows(HarveyException.class, () -> CommandType.fromKeyword(""));
    }

    @Test
    public void fromKeyword_wrongCase_returnsMatchingType() throws HarveyException {
        // A user with caps lock on still means the same command.
        assertEquals(CommandType.TODO, CommandType.fromKeyword("TODO"));
        assertEquals(CommandType.LIST, CommandType.fromKeyword("List"));
    }

    @Test
    public void getExample_everyType_exampleStartsWithItsOwnKeyword() {
        // The examples are shown to correct a mistake, so an example that does not
        // actually use the command it belongs to would be worse than none.
        for (CommandType type : CommandType.values()) {
            assertTrue(type.getExample().startsWith(type.getKeyword()),
                    type.getKeyword() + " has example: " + type.getExample());
        }
    }

    @Test
    public void listKeywords_always_namesEveryCommand() {
        String listed = CommandType.listKeywords();
        for (CommandType type : CommandType.values()) {
            assertTrue(listed.contains(type.getKeyword()),
                    type.getKeyword() + " missing from: " + listed);
        }
    }
}
