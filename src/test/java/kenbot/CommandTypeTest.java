package kenbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CommandType}, which turns the first word of a command into the
 * value the rest of the program switches on.
 *
 * <p>Test names read as {@code method_input_expectedResult}.</p>
 */
public class CommandTypeTest {

    @Test
    public void from_everyCommandWord_isRecognised() throws KenbotException {
        // Looped over the values themselves rather than a hand-written list, so
        // a command added later cannot be left untested by accident.
        for (CommandType command : CommandType.values()) {
            assertEquals(command, CommandType.from(command.name().toLowerCase()),
                    command + " should be reachable by typing its own name");
        }
    }

    @Test
    public void from_unknownWord_throws() {
        assertThrows(KenbotException.class, () -> CommandType.from("nonsense"));
    }

    @Test
    public void from_upperCase_throwsBecauseCommandsAreLowerCase() {
        assertThrows(KenbotException.class, () -> CommandType.from("LIST"));
    }

    @Test
    public void from_mixedCase_throws() {
        assertThrows(KenbotException.class, () -> CommandType.from("Todo"));
    }

    @Test
    public void from_empty_throws() {
        assertThrows(KenbotException.class, () -> CommandType.from(""));
    }

    @Test
    public void from_wordWithSurroundingSpaces_throws() {
        // Parser trims before calling this, so anything with spaces left on it
        // did not come from the normal path.
        assertThrows(KenbotException.class, () -> CommandType.from(" list "));
    }

    @Test
    public void from_unknownWord_saysSoInTheMessage() {
        KenbotException thrown = assertThrows(KenbotException.class, () ->
                CommandType.from("nonsense"));
        assertEquals("Don't know that one.", thrown.getMessage());
    }
}
