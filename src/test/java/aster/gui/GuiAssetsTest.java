package aster.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

/**
 * Tests the pictures packaged for the chat window.
 *
 * <p>Aster's mark must be a square picture with a transparent background, so that it sits
 * cleanly on whatever colour is behind it, and it is drawn larger than it is shown, so that
 * it stays sharp on high-resolution screens. The user is shown without a picture, so none
 * is packaged for them.
 *
 * <p>The pictures are read as plain image files, so nothing here needs a display.
 */
class GuiAssetsTest {
    private static final int MARK_SIZE = 160;
    private static final int LAST_PIXEL = MARK_SIZE - 1;
    private static final int OPAQUE = 255;

    @Test
    void asterMark_packagedPicture_isTransparentSquareAt160Pixels() throws IOException {
        URL resource = GuiAssetsTest.class.getResource("/images/aster.png");
        assertNotNull(resource, "Aster's mark must be packaged");
        BufferedImage mark = ImageIO.read(resource);

        assertEquals(MARK_SIZE, mark.getWidth(), "the mark must be 160 pixels wide");
        assertEquals(MARK_SIZE, mark.getHeight(), "the mark must be 160 pixels high");
        assertTrue(mark.getColorModel().hasAlpha(), "the mark must have a transparency channel");
        int[][] corners = {{0, 0}, {LAST_PIXEL, 0}, {0, LAST_PIXEL}, {LAST_PIXEL, LAST_PIXEL}};
        for (int[] corner : corners) {
            assertEquals(0, alphaAt(mark, corner[0], corner[1]),
                    "the corner at " + corner[0] + "," + corner[1] + " must be transparent");
        }
        assertEquals(OPAQUE, alphaAt(mark, MARK_SIZE / 2, MARK_SIZE / 2), "the centre of the mark must be opaque");
    }

    @Test
    void userPicture_noLongerPackaged_isAbsent() {
        assertNull(GuiAssetsTest.class.getResource("/images/user.png"),
                "the user is shown without a picture, so none may be packaged");
    }

    /**
     * Returns how opaque one pixel of a picture is.
     *
     * @param image the picture.
     * @param x the pixel's column.
     * @param y the pixel's row.
     * @return the pixel's alpha, from 0 for fully transparent to 255 for fully opaque.
     */
    private static int alphaAt(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >>> 24;
    }
}
