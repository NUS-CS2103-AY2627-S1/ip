package zucc.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests behavior shared by all user interface implementations.
 */
public class UiTest {
    /** Verifies that the shared greeting is sent through the concrete message channel. */
    @Test
    public void showGreeting_recordingUi_sharedGreetingShown() {
        RecordingUi ui = new RecordingUi();

        ui.showGreeting();

        assertEquals(
                "Hello! I'm Zucc.\nReady to keep tabs on your tasks—and only your tasks.",
                ui.getLastMessage());
    }

    /** Records the most recent message for assertions. */
    private static final class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }

        @Override
        public void showError(String message) {
            lastMessage = message;
        }

        /** Returns the most recently recorded response. */
        private String getLastMessage() {
            return lastMessage;
        }
    }
}
