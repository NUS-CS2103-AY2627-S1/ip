package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the response value passed from Duke to the GUI. */
public class CommandResponseTest {

    @Test
    public void twoArgumentConstructor_setsExitToFalse() {
        CommandResponse response = new CommandResponse("Invalid command", true);

        assertEquals("Invalid command", response.text());
        assertTrue(response.isError());
        assertFalse(response.shouldExit());
    }
}
