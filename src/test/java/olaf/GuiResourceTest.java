package olaf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Verifies that required JavaFX resources are packaged on the application classpath.
 */
class GuiResourceTest {
    @Test
    void guiResources_allRequiredResourcesArePresent() {
        assertNotNull(getClass().getResource("/view/MainWindow.fxml"));
        assertNotNull(getClass().getResource("/view/ChatMessage.fxml"));
        assertNotNull(getClass().getResource("/view/Olaf.css"));
        assertNotNull(getClass().getResource("/images/user-profile-pic.jpg"));
        assertNotNull(getClass().getResource("/images/chatbot-profile-pic.png"));
    }

    @Test
    void fxmlResources_useJavaFx25Namespace() throws IOException {
        assertUsesJavaFx25Namespace("/view/MainWindow.fxml");
        assertUsesJavaFx25Namespace("/view/ChatMessage.fxml");
    }

    private void assertUsesJavaFx25Namespace(String resourcePath) throws IOException {
        try (InputStream input = getClass().getResourceAsStream(resourcePath)) {
            assertNotNull(input);
            String fxml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(fxml.contains("xmlns=\"http://javafx.com/javafx/25\""));
        }
    }
}
