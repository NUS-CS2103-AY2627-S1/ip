package zabud.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Verifies that resources required by the packaged GUI are present.
 */
class GuiResourcesTest {
    @Test
    void guiResources_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(DialogBox.class.getResource("/view/DialogBox.fxml"));
        assertNotNull(MainWindow.class.getResource("/images/DaUser.png"));
        assertNotNull(MainWindow.class.getResource("/images/DaDuke.png"));
        assertNotNull(Main.class.getResource("/css/main.css"));
    }
}
