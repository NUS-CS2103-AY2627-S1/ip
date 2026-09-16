package miku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

/** Checks the resources needed for the default chat window appearance. */
class MainWindowResourceTest {
    @Test
    void mainWindow_defaultSize_matchesBackgroundAspectRatio() throws IOException {
        try (InputStream layoutStream = MainWindowResourceTest.class.getResourceAsStream("/view/MainWindow.fxml");
                InputStream imageStream = MainWindowResourceTest.class.getResourceAsStream("/images/bg.jpg")) {
            assertNotNull(layoutStream);
            assertNotNull(imageStream);
            String layout = new String(layoutStream.readAllBytes(), StandardCharsets.UTF_8);
            BufferedImage background = ImageIO.read(imageStream);

            assertTrue(layout.contains("prefHeight=\"660.0\""));
            assertTrue(layout.contains("prefWidth=\"465.0\""));
            assertNotNull(background);
            assertEquals((double) background.getWidth() / background.getHeight(), 465.0 / 660.0, 0.001);
        }
    }

    @Test
    void backgroundStylesheet_referencesPackagedJpeg() throws IOException {
        try (InputStream stylesStream = MainWindowResourceTest.class.getResourceAsStream("/css/main.css");
                InputStream imageStream = MainWindowResourceTest.class.getResourceAsStream("/images/bg.jpg")) {
            assertNotNull(stylesStream);
            assertNotNull(imageStream);
            String styles = new String(stylesStream.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(styles.contains("url(\"../images/bg.jpg\")"));
            assertNotNull(ImageIO.read(imageStream));
        }
    }
}
