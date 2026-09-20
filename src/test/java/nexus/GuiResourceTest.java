package nexus;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Checks that every resource required by the JavaFX controllers is packaged. */
class GuiResourceTest {
    @Test
    void guiResources_allRequiredFilesAreAvailable() {
        assertNotNull(getClass().getResource("/view/MainWindow.fxml"));
        assertNotNull(getClass().getResource("/view/DialogBox.fxml"));
        assertNotNull(getClass().getResource("/css/main.css"));
        assertNotNull(getClass().getResource("/images/DaUser.png"));
        assertNotNull(getClass().getResource("/images/DaDuke.png"));
    }
}
