package niulai.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests explicit lookup of resources required by the JavaFX interface. */
class MainTest {
    /** Verifies that required application resources resolve successfully. */
    @Test
    void requireResource_existingResources_urlsReturned() {
        assertNotNull(Main.requireResource("/view/MainWindow.fxml"));
        assertNotNull(Main.requireResource("/view/DialogBox.fxml"));
        assertNotNull(Main.requireResource("/view/style.css"));
    }

    /** Verifies that a missing resource produces a diagnostic naming that resource. */
    @Test
    void requireResource_missingResource_exceptionNamesResource() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> Main.requireResource("/view/missing.fxml"));

        assertTrue(exception.getMessage().contains("/view/missing.fxml"));
    }
}
