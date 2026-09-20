package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

/** Tests that GUI image resources retain the dimensions and transparency required by the layout. */
class BkxssGuiAssetTest {
    private static final String USER_AVATAR_PATH = "/images/cat-avatar.png";
    private static final String BOT_AVATAR_PATH = "/images/dog-avatar.png";
    private static final String BACKGROUND_PATH = "/images/chatbot_background.png";
    private static final String MAIN_VIEW_PATH = "/view/MainWindow.fxml";

    @Test
    void avatarAssets_squarePngs_haveTransparentCornersAndOpaqueCenters() throws IOException {
        assertCircularImage(USER_AVATAR_PATH);
        assertCircularImage(BOT_AVATAR_PATH);
    }

    @Test
    void backgroundAsset_loadsAsVisibleImage() throws IOException {
        BufferedImage background = loadImage(BACKGROUND_PATH);

        assertTrue(background.getWidth() > 0);
        assertTrue(background.getHeight() > 0);
        assertTrue(alphaAt(background, background.getWidth() / 2, background.getHeight() / 2) > 0);
    }

    @Test
    void mainViewResource_definesControllerAndRequiredChatControls() throws IOException {
        String mainView = loadText(MAIN_VIEW_PATH);

        assertTrue(mainView.contains("fx:controller=\"bkxss.MainWindow\""));
        assertTrue(mainView.contains("fx:id=\"conversationScroll\""));
        assertTrue(mainView.contains("fx:id=\"conversation\""));
        assertTrue(mainView.contains("fx:id=\"commandInput\""));
        assertTrue(mainView.contains("fx:id=\"sendButton\""));
        assertTrue(mainView.contains("onAction=\"#sendCommand\""));
    }

    /** Checks the shape and alpha values needed for an avatar with a transparent circular crop. */
    private static void assertCircularImage(String resourcePath) throws IOException {
        BufferedImage avatar = loadImage(resourcePath);

        assertEquals(avatar.getWidth(), avatar.getHeight());
        assertEquals(0, alphaAt(avatar, 0, 0));
        assertEquals(0, alphaAt(avatar, avatar.getWidth() - 1, 0));
        assertEquals(0, alphaAt(avatar, 0, avatar.getHeight() - 1));
        assertEquals(0, alphaAt(avatar, avatar.getWidth() - 1, avatar.getHeight() - 1));
        assertTrue(alphaAt(avatar, avatar.getWidth() / 2, avatar.getHeight() / 2) > 0);
    }

    /** Loads a bundled image and fails clearly when the resource is missing or unreadable. */
    private static BufferedImage loadImage(String resourcePath) throws IOException {
        URL resource = BkxssGuiAssetTest.class.getResource(resourcePath);
        assertNotNull(resource);
        BufferedImage image = ImageIO.read(resource);
        assertNotNull(image);
        return image;
    }

    /** Loads a UTF-8 text resource and fails clearly when it is missing. */
    private static String loadText(String resourcePath) throws IOException {
        try (InputStream resource = BkxssGuiAssetTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(resource);
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** Returns the unsigned alpha value at one pixel. */
    private static int alphaAt(BufferedImage image, int x, int y) {
        return (image.getRGB(x, y) >>> 24) & 0xff;
    }
}
