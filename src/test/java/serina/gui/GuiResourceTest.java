package serina.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests that resources required to launch Serina's GUI are packaged on the classpath.
 */
public class GuiResourceTest {
    @Test
    public void guiResources_packagedApplication_resourcesAreAvailable() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(DialogBox.class.getResource("/view/DialogBox.fxml"));
        assertNotNull(Main.class.getResource("/css/main.css"));
        assertNotNull(Main.class.getResource("/images/chat-background.png"));
        assertNotNull(Main.class.getResource("/images/serina-avatar.png"));
        assertNotNull(Main.class.getResource("/images/user-avatar.png"));
    }
}
