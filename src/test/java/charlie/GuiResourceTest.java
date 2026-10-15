package charlie;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GuiResourceTest {
    @Test
    public void bundledGuiResources_allExistAndContainData() throws IOException {
        List<String> resourcePaths = List.of(
                "/styles.css",
                "/images/charlie-avatar.jpg",
                "/images/user-avatar.jpg");

        for (String resourcePath : resourcePaths) {
            try (InputStream resourceStream = Main.class.getResourceAsStream(resourcePath)) {
                assertNotNull(resourceStream, "Missing resource: " + resourcePath);
                assertTrue(resourceStream.readAllBytes().length > 0,
                        "Resource is empty: " + resourcePath);
            }
        }
    }
}
