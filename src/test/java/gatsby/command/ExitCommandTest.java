package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import gatsby.testutil.RecordingUi;

/** Tests the session-ending command. */
class ExitCommandTest extends AbstractCommandTest {

    /** Verifies the goodbye message and exit signal. */
    @Test
    void execute_exitCommand_reportsGoodbyeAndIsExit() {
        RecordingUi ui = recordingUi();
        ExitCommand command = new ExitCommand();

        command.execute(null, ui);

        assertEquals(" Bye. Hope to see you again soon!", ui.messages().get(0));
        assertTrue(command.isExit());
    }

    /** Verifies that ordinary commands do not signal the end of a session. */
    @Test
    void isExit_ordinaryCommand_returnsFalse() {
        assertTrue(!new TodoCommand("todo").isExit());
    }
}
