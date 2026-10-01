package bany.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

/** Checks that the images needed by the chat window are packaged and readable. */
class GuiResourcesTest {

    @Test
    void chatWindowImages_areAvailableAndDecodable() throws IOException {
        for (String resource : List.of("/bany/gui/images/DaUser.png",
                "/bany/gui/images/BanyLogo.png", "/bany/gui/images/bg2.jpeg")) {
            try (InputStream stream = GuiResourcesTest.class.getResourceAsStream(resource)) {
                assertNotNull(stream, "Missing GUI image: " + resource);
                assertNotNull(ImageIO.read(stream), "Unreadable GUI image: " + resource);
            }
        }
    }
}
