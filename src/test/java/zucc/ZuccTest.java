package zucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zucc.ui.Ui;

/**
 * Tests the application boundary shared by terminal and JavaFX interfaces.
 */
public class ZuccTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that the shared greeting introduces Zucc's restrained personality.
     */
    @Test
    public void showGreeting_newSession_introducesZuccPersonality() {
        RecordingUi ui = new RecordingUi();

        ui.showGreeting();

        assertEquals(
                "Hello! I'm Zucc.\nReady to keep tabs on your tasks—and only your tasks.",
                ui.getLastMessage());
    }

    /**
     * Verifies that a regular command sends its response through the supplied UI.
     *
     * @throws ZuccException if the isolated task store cannot be initialized.
     */
    @Test
    public void executeCommand_regularCommand_responseShownAndSessionContinues()
            throws ZuccException {
        RecordingUi ui = new RecordingUi();
        Zucc zucc = new Zucc(temporaryDirectory.resolve("tasks.txt"));

        boolean isExit = zucc.executeCommand("todo read the JavaFX guide", ui);

        assertFalse(isExit);
        assertTrue(ui.getLastMessage().contains("[T][ ] read the JavaFX guide"));
    }

    /**
     * Verifies that an invalid command is shown as feedback without ending the session.
     *
     * @throws ZuccException if the isolated task store cannot be initialized.
     */
    @Test
    public void executeCommand_invalidCommand_errorShownAndSessionContinues()
            throws ZuccException {
        RecordingUi ui = new RecordingUi();
        Zucc zucc = new Zucc(temporaryDirectory.resolve("tasks.txt"));

        boolean isExit = zucc.executeCommand("dance", ui);

        assertFalse(isExit);
        assertTrue(ui.getLastMessage().contains("doesn't recognize that command"));
    }

    /**
     * Verifies that the exit command shows its farewell and reports the ended session.
     *
     * @throws ZuccException if the isolated task store cannot be initialized.
     */
    @Test
    public void executeCommand_exitCommand_farewellShownAndSessionEnds()
            throws ZuccException {
        RecordingUi ui = new RecordingUi();
        Zucc zucc = new Zucc(temporaryDirectory.resolve("tasks.txt"));

        boolean isExit = zucc.executeCommand("bye", ui);

        assertTrue(isExit);
        assertEquals("Bye. Hope to see you again soon!", ui.getLastMessage());
    }

    /** Records messages without depending on a concrete frontend toolkit. */
    private static final class RecordingUi extends Ui {
        /** Messages shown during the test. */
        private final List<String> messages = new ArrayList<>();

        /** Records one response for later assertion. */
        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        /** Returns the most recently recorded response. */
        private String getLastMessage() {
            return messages.getLast();
        }
    }
}
