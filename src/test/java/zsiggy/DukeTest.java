package zsiggy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks validation shared by task update commands.
 */
public class DukeTest {
    @Test
    public void getResponse_invalidTaskNumbers_returnsHelpfulErrors() {
        Duke duke = new Duke();
        for (String command : new String[]{"mark", "unmark", "delete"}) {
            assertEquals("Oi. Give me a proper task number.", duke.getResponse(command + " abc"));
            assertEquals("Oi. That task doesn't exist.", duke.getResponse(command + " 0"));
            assertEquals("Oi. That task doesn't exist.", duke.getResponse(command + " 1"));
            assertEquals("Oi. That task doesn't exist.", duke.getResponse(command + " -1"));
        }
    }
}
