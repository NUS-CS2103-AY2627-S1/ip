package arin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

/**
 * Checks that the welcome window's artwork is available as a bundled resource.
 */
class MainResourcesTest {
    @Test
    void robotArtwork_classpathResource_decodesAsImage() throws Exception {
        try (InputStream input = Main.class.getResourceAsStream("/images/arin-avatar.jpg")) {
            assertNotNull(input, "The robot image must be included in the application resources.");
            BufferedImage image = ImageIO.read(input);

            assertNotNull(image, "The robot resource must contain a supported image.");
            assertTrue(image.getWidth() > 0);
            assertTrue(image.getHeight() > 0);
        }
    }
}
