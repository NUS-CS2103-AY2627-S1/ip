package javafx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/** Tests behavior belonging specifically to the JavaFX main window. */
public class MainWindowTest {
    /** Verifies that only a bye command, ignoring case and surrounding spaces, closes the GUI. */
    @Test
    public void exitIfRequested_variedInputs_exitActionRunOnlyForBye() {
        AtomicInteger exitCount = new AtomicInteger();

        MainWindow.exitIfRequested("bye", exitCount::incrementAndGet);
        MainWindow.exitIfRequested(" BYE ", exitCount::incrementAndGet);
        MainWindow.exitIfRequested("bye now", exitCount::incrementAndGet);
        MainWindow.exitIfRequested("list", exitCount::incrementAndGet);

        assertEquals(2, exitCount.get());
    }

    /** Verifies that the initial server message gives actionable command guidance. */
    @Test
    public void getWelcomeMessage_messageContainsEssentialCommands() {
        String welcomeMessage = MainWindow.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("todo"));
        assertTrue(welcomeMessage.contains("deadline"));
        assertTrue(welcomeMessage.contains("event"));
        assertTrue(welcomeMessage.contains("list"));
        assertTrue(welcomeMessage.contains("sort date"));
        assertTrue(welcomeMessage.contains("bye"));
    }
}
