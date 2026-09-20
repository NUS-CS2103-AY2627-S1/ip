package duchess.gui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Verifies that every JavaFX FXML and stylesheet resource is packaged. */
public class GuiResourceTest {
    /** Verifies all resources referenced by JavaFX controllers are available. */
    @Test
    public void guiResources_allReferencedFilesExist() {
        assertAll(
                () -> assertNotNull(Main.class.getResource("/view/MainWindow.fxml")),
                () -> assertNotNull(Main.class.getResource("/view/Header.fxml")),
                () -> assertNotNull(Main.class.getResource("/view/QuickCommands.fxml")),
                () -> assertNotNull(Main.class.getResource("/view/ConversationView.fxml")),
                () -> assertNotNull(Main.class.getResource("/view/Composer.fxml")),
                () -> assertNotNull(Main.class.getResource("/view/DialogBox.fxml")),
                () -> assertNotNull(Main.class.getResource("/css/main.css")),
                () -> assertNotNull(Main.class.getResource("/css/dialog-box.css"))
        );
    }
}
