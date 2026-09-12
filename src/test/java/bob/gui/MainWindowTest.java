package bob.gui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/** Tests presentation logic used by the main JavaFX window. */
class MainWindowTest {

    @Test
    void getTypingText_consecutiveFrames_cyclesThroughOneToThreeDots() {
        assertAll(
                () -> assertEquals("Bob is thinking.", MainWindow.getTypingText(0)),
                () -> assertEquals("Bob is thinking..", MainWindow.getTypingText(1)),
                () -> assertEquals("Bob is thinking...", MainWindow.getTypingText(2)),
                () -> assertEquals("Bob is thinking.", MainWindow.getTypingText(3)),
                () -> assertEquals("Bob is thinking...", MainWindow.getTypingText(-1)));
    }

    @Test
    void validateInput_nullOrBlankInput_returnsFeedback() {
        Optional<String> expectedFeedback = Optional.of(
                "Enter a command, or open Help for examples.");

        assertAll(
                () -> assertEquals(expectedFeedback, MainWindow.validateInput(null)),
                () -> assertEquals(expectedFeedback, MainWindow.validateInput("")),
                () -> assertEquals(expectedFeedback, MainWindow.validateInput("   ")));
    }

    @Test
    void validateInput_nonBlankInput_returnsEmptyResult() {
        assertEquals(Optional.empty(), MainWindow.validateInput("todo read a book"));
    }
}
