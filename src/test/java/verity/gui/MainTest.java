package verity.gui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests resources required by the JavaFX application.
 */
class MainTest {

    @Test
    void stylesheetResources_arePackagedWithApplication() {
        assertAll(
                () -> assertNotNull(
                        Main.class.getResource(
                                "/css/main.css")),
                () -> assertNotNull(
                        Main.class.getResource(
                                "/css/dialog-box.css"))
        );
    }

    @Test
    void fxmlViews_arePackagedWithApplication() {
        assertAll(
                () -> assertNotNull(
                        Main.class.getResource(
                                "/view/MainWindow.fxml")),
                () -> assertNotNull(
                        Main.class.getResource(
                                "/view/DialogBox.fxml"))
        );
    }

    @Test
    void verityAvatar_isPackagedWithApplication() {
        assertAll(
                () -> assertNotNull(
                        Main.class.getResource("/images/verity_bot.png")),
                () -> assertNotNull(
                        Main.class.getResource("/images/verity_red.jpeg")),
                () -> assertNotNull(
                        Main.class.getResource("/images/mc_bg_good.png")),
                () -> assertNotNull(
                        Main.class.getResource("/images/mc_bg_bad.png"))
        );
    }
}
