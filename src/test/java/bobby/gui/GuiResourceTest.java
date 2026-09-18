package bobby.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests that Bobby's FXML views are available on the runtime classpath.
 */
class GuiResourceTest {
    /**
     * Verifies that the main-window view is packaged as a resource.
     */
    @Test
    void mainWindowFxml_applicationResources_resourceFound() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
    }

    /**
     * Verifies that the dialog-box view is packaged as a resource.
     */
    @Test
    void dialogBoxFxml_applicationResources_resourceFound() {
        assertNotNull(DialogBox.class.getResource("/view/DialogBox.fxml"));
    }

    /**
     * Verifies that the GUI theme is packaged as a resource.
     */
    @Test
    void themeStylesheet_applicationResources_resourceFound() {
        assertNotNull(Main.class.getResource("/view/BobbyTheme.css"));
    }

    /**
     * Verifies that Bobby's profile image is packaged as a resource.
     */
    @Test
    void bobbyProfileImage_applicationResources_resourceFound() {
        assertNotNull(MainWindow.class.getResource("/images/Lord_Bobby_Portrait.png"));
    }
}
