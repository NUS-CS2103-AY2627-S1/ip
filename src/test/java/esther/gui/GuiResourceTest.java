package esther.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests that JavaFX resources are available on the classpath.
 */
public class GuiResourceTest {
    @Test
    public void guiResources_existOnClasspath() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(Main.class.getResource("/view/DialogBox.fxml"));
        assertNotNull(Main.class.getResource("/css/main.css"));
        assertNotNull(Main.class.getResource("/css/dialog-box.css"));
        assertNotNull(Main.class.getResource("/images/user.png"));
        assertNotNull(Main.class.getResource("/images/esther.png"));
    }
}
