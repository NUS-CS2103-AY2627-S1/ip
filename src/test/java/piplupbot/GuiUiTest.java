package piplupbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

import org.junit.jupiter.api.Test;

/**
 * Tests the one part of {@link GuiUi} that works without a window:
 * {@link GuiUi#getResourceUrl}, which finds the files that come with the
 * program.
 *
 * <p>The rest of {@link GuiUi}, and all of {@link DialogBox}, needs JavaFX to be
 * running with a screen to draw on, which a test machine such as GitHub's does
 * not have. Those parts are checked by hand, in the window itself. This method
 * is only a lookup, and calling it starts nothing, so it can be checked here
 * like any other.</p>
 */
public class GuiUiTest {

    @Test
    public void getResourceUrl_fileThatComesWithTheProgram_returnsWhereItIs() {
        URL url = GuiUi.getResourceUrl("/view/MainWindow.fxml");

        assertTrue(url.getPath().endsWith("/view/MainWindow.fxml"), "Unexpected address: " + url);
    }

    /**
     * A missing file is named in the error. Without the check, a missing
     * picture would fail with a bare {@code NullPointerException} that names no
     * file at all.
     */
    @Test
    public void getResourceUrl_missingFile_exceptionNamesThePath() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                GuiUi.getResourceUrl("/images/no-such-picture.png"));

        assertEquals("Missing resource: /images/no-such-picture.png", exception.getMessage());
    }
}
