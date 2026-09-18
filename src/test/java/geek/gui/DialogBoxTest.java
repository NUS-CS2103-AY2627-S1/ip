package geek.gui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the classification that selects the alert dialog style.
 */
class DialogBoxTest {
    @Test
    void requiresAttention_errorsAndWarnings_returnsTrue() {
        assertAll(() -> assertTrue(
                        DialogBox.requiresAttention(
                                "OOPS!!! Please enter a command."
                        )
                ), () -> assertTrue(
                        DialogBox.requiresAttention(
                                "Warning: Skipped corrupted saved tasks."
                        )
                ), () -> assertTrue(
                        DialogBox.requiresAttention(
                                "Task added.\n\n"
                                        + "OOPS!!! I could not save the tasks."
                        )
                )
        );
    }

    @Test
    void requiresAttention_normalResponse_returnsFalse() {
        assertFalse(
                DialogBox.requiresAttention(
                        "Here's what's on your mission board."
                )
        );
    }
}
