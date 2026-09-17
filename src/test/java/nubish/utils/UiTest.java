package nubish.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests UI response state updates.
 */
public class UiTest {
    /**
     * Verifies that greeting sets the latest response as a non-error response.
     */
    @Test
    public void greet_setsGreetingResponse() {
        UI ui = new UI();

        ui.greet();

        assertTrue(ui.getLastResponse().contains("Wazzup! I'm Nubish."));
        assertFalse(ui.isLastResponseError());
    }

    /**
     * Verifies that showing an error sets the latest response as an error.
     */
    @Test
    public void showError_errorMessage_setsErrorResponse() {
        UI ui = new UI();

        ui.showError("error message");

        assertEquals("error message\n", ui.getLastResponse());
        assertTrue(ui.isLastResponseError());
    }

    /**
     * Verifies that empty reminders produce no message.
     */
    @Test
    public void getReminders_emptyReminders_returnsEmptyString() {
        UI ui = new UI();

        assertEquals("", ui.getReminders(""));
    }

    /**
     * Verifies that non-empty reminders include the reminder header.
     */
    @Test
    public void getReminders_nonEmptyReminders_returnsReminderMessage() {
        UI ui = new UI();

        assertEquals("REMINDER! These tasks are due today:\n1. [T][ ] read book\n",
                ui.getReminders("1. [T][ ] read book\n"));
    }

    /**
     * Verifies that printing the command list marks the response as an error.
     */
    @Test
    public void printCommandList_setsErrorResponse() {
        UI ui = new UI();

        ui.printCommandList();

        assertTrue(ui.getLastResponse().contains("Here is a list of the current commands"));
        assertTrue(ui.isLastResponseError());
    }
}
