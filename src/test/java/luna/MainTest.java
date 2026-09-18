package luna;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests resources required by the JavaFX application.
 */
public class MainTest {
    @Test
    public void resources_applicationStarts_areAvailable() throws IOException {
        URL stylesheetUrl = MainTest.class.getResource("/luna.css");
        assertNotNull(stylesheetUrl);
        assertNotNull(MainTest.class.getResource("/images/user-avatar.png"));

        try (InputStream stylesheet = stylesheetUrl.openStream()) {
            String stylesheetContent = new String(stylesheet.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(stylesheetContent.contains(".app-header"));
        }
    }
}
