package eden.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests that the resources loaded by the JavaFX entry point are packaged on the classpath.
 */
public class GuiResourceTest {
    /**
     * Verifies that both layouts and their shared stylesheet can be resolved.
     */
    @Test
    public void resources_guiLayoutsAndStyles_areAvailable() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(DialogBox.class.getResource("/view/DialogBox.fxml"));
        assertNotNull(Main.class.getResource("/css/main.css"));
    }
}
