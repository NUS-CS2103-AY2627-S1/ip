package neil.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests the declarative layout rules for the main window.
 */
class MainWindowLayoutTest {
    @Test
    void mainWindowFxml_composerBarUsed_textFieldGrows() throws IOException {
        String fxml = readResource("/view/MainWindow.fxml");

        assertTrue(fxml.contains("styleClass=\"composer-bar\""));
        assertTrue(fxml.contains("HBox.hgrow=\"ALWAYS\""));
    }

    @Test
    void mainWindowFxml_commandHistory_hintAndKeyboardHandlerPresent() throws IOException {
        String fxml = readResource("/view/MainWindow.fxml");

        assertTrue(fxml.contains("onKeyPressed=\"#handleHistoryNavigation\""));
        assertTrue(fxml.contains("promptText=\"Type a command... (Up/Down for history)\""));
    }

    @Test
    void mainCss_sendButtonStyled_allInteractionStatesPresent() throws IOException {
        String css = readResource("/css/main.css");

        assertTrue(css.contains("#sendButton:hover"));
        assertTrue(css.contains("#sendButton:pressed"));
        assertTrue(css.contains("#sendButton:focused"));
        assertTrue(css.contains("#sendButton:disabled"));
        assertTrue(css.contains("-fx-font-weight: 600;"));
    }

    private String readResource(String resourcePath) throws IOException {
        try (InputStream input = MainWindowLayoutTest.class.getResourceAsStream(resourcePath)) {
            assert input != null : "Test resource must exist: " + resourcePath;
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
