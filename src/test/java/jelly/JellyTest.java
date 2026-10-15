package jelly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jelly.ui.Ui;

class JellyTest {

    @Test
    void executeCommand_help_returnsAvailableCommands() {
        Jelly jelly = new Jelly();

        assertEquals(Ui.HELP_MESSAGE, jelly.executeCommand("help"));
        assertFalse(jelly.wasLastResponseAnError());
    }

    @Test
    void executeCommand_invalidTaskDescription_returnsError() {
        Jelly jelly = new Jelly();

        String response = jelly.executeCommand("todo bad | data");

        assertTrue(jelly.wasLastResponseAnError());
        assertTrue(response.contains("valid command"));
    }
}
