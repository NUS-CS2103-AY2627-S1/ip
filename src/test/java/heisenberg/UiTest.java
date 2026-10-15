package heisenberg;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests that the chatbot's messages retain its controlled-operation personality. */
public class UiTest {
    @Test
    public void getWelcomeMessage_usesControlledOperationPersona() {
        Ui ui = new Ui();

        assertTrue(ui.getWelcomeMessage().contains("precise operation"));
        assertTrue(ui.getWelcomeMessage().contains("formula"));
    }

    @Test
    public void getGoodbyeMessage_usesControlledOperationPersonality() {
        Ui ui = new Ui();

        assertTrue(ui.getGoodbyeMessage().contains("Stay out of my territory"));
    }
}
