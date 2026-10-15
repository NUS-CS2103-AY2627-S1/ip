package neil.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests the declarative layout rules for chat messages.
 */
class DialogBoxLayoutTest {
    @Test
    void dialogBoxFxml_responsiveLayout_fixedMessageWidthsNotUsed() throws IOException {
        String fxml = readResource("/view/DialogBox.fxml");

        assertTrue(fxml.contains("fx:id=\"dialogContent\""));
        assertTrue(fxml.contains("fx:id=\"errorTitle\""));
        assertFalse(fxml.contains("maxWidth=\"310.0\""));
        assertFalse(fxml.contains("maxWidth=\"282.0\""));
    }

    @Test
    void dialogBoxFxml_helpTable_horizontalScrollingEnabled() throws IOException {
        String fxml = readResource("/view/DialogBox.fxml");

        assertTrue(fxml.contains("fx:id=\"helpScrollPane\""));
        assertTrue(fxml.contains("hbarPolicy=\"AS_NEEDED\""));
        assertTrue(fxml.contains("vbarPolicy=\"NEVER\""));
        assertTrue(fxml.contains("fx:id=\"helpTable\""));
        assertTrue(fxml.contains("wrapText=\"false\""));
    }

    private String readResource(String resourcePath) throws IOException {
        try (InputStream input = DialogBoxLayoutTest.class.getResourceAsStream(resourcePath)) {
            assert input != null : "Test resource must exist: " + resourcePath;
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
