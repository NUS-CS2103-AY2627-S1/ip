package esther.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import javafx.util.Duration;

/**
 * Tests timing-related behavior of Esther's JavaFX window.
 */
public class MainWindowTest {
    @Test
    public void goodbyeDisplayDuration_isLongEnoughToRenderFarewell() {
        try {
            Field durationField = MainWindow.class.getDeclaredField(
                    "GOODBYE_DISPLAY_DURATION");
            durationField.setAccessible(true);
            Duration duration = (Duration) durationField.get(null);

            assertTrue(duration.toMillis() >= 500,
                    "The farewell should remain visible briefly before the window closes.");
        } catch (ReflectiveOperationException exception) {
            fail("A goodbye display delay is required before closing the GUI.", exception);
        }
    }
}
