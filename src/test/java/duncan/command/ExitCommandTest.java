package duncan.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ExitCommandTest {
    @Test
    public void isExit_alwaysExits_trueReturned() {
        assertTrue(new ExitCommand().isExit());
    }

    // execute() is not covered here: it's a one-line ui.showGoodbye() call
    // with no branching of its own - console output already checked
    // end-to-end by the UI test plan's Farewell block.
}
