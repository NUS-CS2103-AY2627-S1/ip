package toothless;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Verifies that the resources needed to launch the JavaFX window are packaged.
 */
class GuiResourcesTest {
    @Test
    void guiResources_allRequiredFilesArePresent() {
        assertNotNull(GuiResourcesTest.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(GuiResourcesTest.class.getResource("/view/DialogBox.fxml"));
        assertNotNull(GuiResourcesTest.class.getResource("/css/toothless.css"));
        assertNotNull(GuiResourcesTest.class.getResource("/fonts/ComicNeue-Regular.ttf"));
        assertNotNull(GuiResourcesTest.class.getResource("/fonts/OFL.txt"));
        assertNotNull(GuiResourcesTest.class.getResource("/images/night-sky-header.png"));
        assertNotNull(GuiResourcesTest.class.getResource("/images/toothless-avatar.png"));
        assertNotNull(GuiResourcesTest.class.getResource("/images/user-avatar.png"));
    }

    @Test
    void conversationFont_isPackagedAsLicensedTrueType() throws IOException {
        try (InputStream font = GuiResourcesTest.class.getResourceAsStream(
                "/fonts/ComicNeue-Regular.ttf");
                InputStream license = GuiResourcesTest.class.getResourceAsStream("/fonts/OFL.txt")) {
            assertNotNull(font);
            assertNotNull(license);
            assertArrayEquals(new byte[] {0, 1, 0, 0}, font.readNBytes(4));
            String licenseText = new String(license.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(licenseText.contains("The Comic Neue Project Authors"));
            assertTrue(licenseText.contains("SIL OPEN FONT LICENSE Version 1.1"));
        }
    }

    @Test
    void conversationFont_isScopedToMessageBubbles() throws IOException {
        try (InputStream input = GuiResourcesTest.class.getResourceAsStream("/css/toothless.css")) {
            assertNotNull(input);
            String css = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            int bubbleStart = css.indexOf(".dialog-bubble .message-content {");
            assertTrue(bubbleStart >= 0);
            String bubbleRules = css.substring(bubbleStart, css.indexOf('}', bubbleStart));

            assertTrue(bubbleRules.contains("-fx-font-family: \"Comic Neue\";"));
            assertTrue(css.contains(".dialog-bubble .star-glyph,"));
            assertTrue(css.contains(".message-font-fallback .dialog-bubble .message-content"));
            assertFalse(css.substring(0, css.indexOf('}')).contains("Comic Neue"));
        }
    }

    @Test
    void mainWindow_helpPanel_containsNestedEditExamples() throws IOException {
        try (InputStream input = GuiResourcesTest.class.getResourceAsStream(
                "/view/MainWindow.fxml")) {
            assertNotNull(input);
            String fxml = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertEquals(1, fxml.split("text=\"✎  Edit task\"", -1).length - 1);
            assertTrue(fxml.contains("onAction=\"#toggleEditHelp\""));
            assertTrue(fxml.contains("fx:id=\"editCommandButtons\""));
            assertTrue(fxml.contains("text=\"✎  Edit description\""));
            assertTrue(fxml.contains("text=\"◷  Edit deadline\""));
            assertTrue(fxml.contains("text=\"✦  Edit event\""));
            assertTrue(fxml.contains("edit 1 /description read the new textbook"));
            assertTrue(fxml.contains("edit 2 /by 2026-09-20"));
            assertTrue(fxml.contains("edit 3 /from 21 September 2026 2pm "
                    + "/to 21 September 2026 5pm"));
            assertFalse(fxml.contains("Any task: edit"));
        }
    }

    @Test
    void errorDialog_stylesBubbleWithoutChangingNormalReplies() throws IOException {
        try (InputStream input = GuiResourcesTest.class.getResourceAsStream("/css/toothless.css")) {
            assertNotNull(input);
            String css = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(css.contains(".toothless-dialog .dialog-bubble"));
            assertTrue(css.contains(".error-dialog .dialog-bubble"));
            assertTrue(css.contains("-fx-background-color: #f9e4dd"));
            assertTrue(css.contains("-fx-border-color: #a3483e"));
        }
    }

    @Test
    void mainWindow_responsiveLayout_keepsConversationAndControlsAccessible() throws IOException {
        try (InputStream input = GuiResourcesTest.class.getResourceAsStream(
                "/view/MainWindow.fxml")) {
            assertNotNull(input);
            String fxml = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(fxml.contains("<BorderPane prefHeight="));
            assertTrue(fxml.contains("fx:id=\"scrollPane\" fitToWidth=\"true\""));
            assertTrue(fxml.contains("fx:id=\"commandHelp\" fitToWidth=\"true\""));
            assertTrue(fxml.contains("maxHeight=\"220.0\""));
            assertTrue(fxml.contains("fx:id=\"userInput\" HBox.hgrow=\"ALWAYS\""));
            assertTrue(fxml.contains("fx:id=\"sendButton\""));
            assertFalse(fxml.contains("prefWrapLength=\"510.0\""));
        }
        try (InputStream input = GuiResourcesTest.class.getResourceAsStream(
                "/view/DialogBox.fxml")) {
            assertNotNull(input);
            String fxml = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(fxml.contains("<TextFlow fx:id=\"dialog\""));
            assertTrue(fxml.contains("preserveRatio=\"true\""));
            assertFalse(fxml.contains("maxWidth=\"320.0\""));
        }
    }
}
