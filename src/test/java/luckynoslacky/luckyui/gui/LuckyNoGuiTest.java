package luckynoslacky.luckyui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Tests JavaFX application startup and window configuration. */
@ExtendWith(ApplicationExtension.class)
class LuckyNoGuiTest {
    private Stage stage;

    /** Starts the production JavaFX application on a test stage. */
    @Start
    void start(Stage stage) throws IOException {
        this.stage = stage;
        new LuckyNoGui().start(stage);
    }

    /** Verifies that application startup creates the expected window. */
    @Test
    void luckyNoGui_startApplication_createsConfiguredWindow() {
        assertEquals("LuckyNoSlacky", stage.getTitle());
        assertNotNull(stage.getScene());
        assertEquals(400.0, stage.getScene().getWidth());
        assertEquals(600.0, stage.getScene().getHeight());
        assertEquals(320.0, stage.getMinWidth());
        assertEquals(480.0, stage.getMinHeight());
        assertNotNull(stage.getScene().getRoot());
        assertTrue(stage.getScene().getRoot().getStylesheets().stream()
                .anyMatch(stylesheet -> stylesheet.endsWith("/css/main.css")));
        assertTrue(stage.getScene().getRoot().getStylesheets().stream()
                .anyMatch(stylesheet -> stylesheet.endsWith("/css/personality.css")));
    }

    /** Verifies that the FXML and avatar resources are packaged for the GUI. */
    @Test
    void luckyNoGui_resourcePaths_areAvailable() {
        assertNotNull(LuckyNoGui.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(LuckyNoGui.class.getResource("/view/BrandHeader.fxml"));
        assertNotNull(LuckyNoGui.class.getResource("/css/main.css"));
        assertNotNull(LuckyNoGui.class.getResource("/css/dialogue-box.css"));
        assertNotNull(LuckyNoGui.class.getResource("/css/personality.css"));
        assertNotNull(LuckyNoGui.class.getResource("/images/luckynoslacky.jpg"));
        assertNotNull(LuckyNoGui.class.getResource("/images/user.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/clover-pattern.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/icons/task-todo.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/icons/task-deadline.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/icons/task-event.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/icons/task-completed.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/icons/task-incomplete.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/icons/response-success.png"));
        assertNotNull(LuckyNoGui.class.getResource("/images/icons/response-warning.png"));
        assertNotNull(LuckyNoGui.class.getResource(
                "/images/icons/response-system-error.png"));
        assertNotNull(LuckyNoGui.class.getResource("/fonts/PatrickHand-Regular.ttf"));
        assertNotNull(LuckyNoGui.class.getResource("/fonts/RobotoMono-Regular.ttf"));
        assertTrue(Font.getFamilies().contains("Patrick Hand"));
        assertTrue(Font.getFamilies().contains("Roboto Mono"));
    }

    /** Verifies that the testable chatbot factory cannot be null. */
    @Test
    void construct_nullChatbotFactory_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new LuckyNoGui(null));
    }
}
