package aster.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * Tests that the chat window's stylesheet is where the window's layout says it is, and
 * that it styles the dialog boxes the window creates.
 *
 * <p>Nothing here starts JavaFX or builds a dialog box. The layout and the stylesheet are
 * read as plain files, and the style class names are compile-time constants copied into
 * this test, so a mismatch between the code, the layout and the stylesheet is caught
 * without a display. How the styles actually look is checked by hand.
 */
class MainStylesheetTest {
    private static final String WINDOW_LAYOUT = "/view/MainWindow.fxml";
    // The stylesheet as the window's layout refers to it, relative to the layout itself.
    private static final String STYLESHEET_REFERENCE = "../css/main.css";

    @Test
    void stylesheet_referencedByMainWindowLayout_resolvesToAFile() throws IOException, URISyntaxException {
        URL layout = MainStylesheetTest.class.getResource(WINDOW_LAYOUT);
        assertNotNull(layout, "the window layout must be on the class path");

        assertTrue(readText(layout).contains("stylesheets=\"@" + STYLESHEET_REFERENCE + "\""),
                "the window layout must attach the stylesheet");
        // Resolved against the layout, as the layout loader resolves an @ reference.
        Path stylesheet = Path.of(layout.toURI().resolve(STYLESHEET_REFERENCE));
        assertTrue(Files.isRegularFile(stylesheet),
                "the stylesheet must exist where the layout refers to it: " + stylesheet);
    }

    @Test
    void stylesheet_errorDialog_hasBubbleRule() throws IOException {
        URL stylesheet = MainStylesheetTest.class.getResource("/css/main.css");
        assertNotNull(stylesheet, "the stylesheet must be on the class path");

        String selector = "." + DialogBox.ERROR_STYLE_CLASS + " .bubble";
        assertTrue(readText(stylesheet).contains(selector),
                "the stylesheet must style error replies with " + selector);
    }

    @Test
    void stylesheet_windowParts_haveRules() throws IOException {
        URL stylesheet = MainStylesheetTest.class.getResource("/css/main.css");
        assertNotNull(stylesheet, "the stylesheet must be on the class path");
        String css = readText(stylesheet);

        String[] selectors = {".app-header", ".header-title", ".header-subtitle", ".conversation",
            ".composer", ".composer-field", ".composer-field:focused", ".composer-field:disabled",
            ".send-button", ".send-button:hover", ".send-button:pressed", ".send-button:disabled"};
        for (String selector : selectors) {
            assertTrue(css.contains(selector), "the stylesheet must style " + selector);
        }
    }

    @Test
    void stylesheet_userAndAsterDialogs_haveBubbleRules() throws IOException {
        URL stylesheet = MainStylesheetTest.class.getResource("/css/main.css");
        assertNotNull(stylesheet, "the stylesheet must be on the class path");
        String css = readText(stylesheet);

        for (String styleClass : new String[] {DialogBox.USER_STYLE_CLASS, DialogBox.ASTER_STYLE_CLASS}) {
            String selector = "." + styleClass + " .bubble";
            assertTrue(css.contains(selector), "the stylesheet must style these messages with " + selector);
        }
    }

    /**
     * Returns the whole content of a text resource.
     *
     * @param resource the resource to read.
     * @return its content, read as UTF-8.
     */
    private static String readText(URL resource) throws IOException {
        try (InputStream in = resource.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
