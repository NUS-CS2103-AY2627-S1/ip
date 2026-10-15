package bo.gui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Verifies that the JavaFX resources required by Bo are packaged. */
class GuiResourceTest {
    /** Verifies that the FXML views and stylesheet are available at runtime. */
    @Test
    void guiResources_arePackaged() {
        assertAll(
                () -> assertNotNull(Main.class.getResource("/view/MainWindow.fxml")),
                () -> assertNotNull(Main.class.getResource("/view/DialogBox.fxml")),
                () -> assertNotNull(Main.class.getResource("/view/main.css")));
    }
}
