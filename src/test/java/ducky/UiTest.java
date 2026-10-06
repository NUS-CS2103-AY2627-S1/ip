package ducky;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests user-interface message formatting.
 */
class UiTest {
    private final Ui ui = new Ui();

    @Test
    void showWelcome_containsDuckyPersonality() {
        String message = ui.showWelcome();

        assertTrue(message.contains("Ducky"));
        assertTrue(message.contains("ready to help"));
        assertFalse(message.contains("____"));
    }

    @Test
    void showExitMessage_containsDuckThemedFarewell() {
        assertTrue(ui.showExitMessage().contains("See you in the pond soon"));
    }

    @Test
    void showError_containsErrorPrefix() {
        String message = ui.showError("invalid command");

        assertTrue(message.contains("QUACK! invalid command"));
        assertFalse(message.contains("____"));
        assertFalse(message.contains("🐥"));
    }
}
