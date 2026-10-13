package baymax.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/** Tests optional resource failures without removing or overwriting packaged resources. */
public class UiAssetsTest {
    @Test
    public void loadFont_missingResource_returnsSystemFont() {
        assertEquals(Font.getDefault(), UiAssets.loadFont("/missing-font.ttf"));
    }

    @Test
    public void loadFont_invalidFontData_returnsSystemFont() {
        assertEquals(Font.getDefault(), UiAssets.loadFont("/view/MainWindow.fxml"));
    }

    @Test
    public void loadFont_validResource_preservesCustomFont() {
        assertEquals("Orbitron", UiAssets.loadFont("/fonts/Orbitron.ttf").getFamily());
    }

    @Test
    public void loadAvatar_missingResource_returnsVisiblePlaceholder() {
        Image image = UiAssets.loadAvatar("/missing-avatar.jpg");
        assertFalse(image.isError());
        assertEquals(48, image.getWidth());
        assertEquals(48, image.getHeight());
        assertEquals(Color.LIGHTGRAY, image.getPixelReader().getColor(0, 0));
        assertEquals(Color.DARKSLATEGRAY, image.getPixelReader().getColor(15, 22));
    }

    @Test
    public void loadAvatar_invalidImageData_returnsPlaceholder() {
        Image image = UiAssets.loadAvatar("/view/MainWindow.fxml");
        assertFalse(image.isError());
        assertNotNull(image.getPixelReader());
        assertEquals(48, image.getWidth());
    }

    @Test
    public void loadAvatar_validResource_loadsPackagedImage() {
        Image image = UiAssets.loadAvatar("/image/bot.jpg");
        assertFalse(image.isError());
        assertTrue(image.getWidth() > 48);
        assertTrue(image.getHeight() > 48);
    }
}
