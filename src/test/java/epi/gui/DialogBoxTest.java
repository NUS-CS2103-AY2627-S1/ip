package epi.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.geometry.Rectangle2D;

/** Tests the responsive layout calculations without starting a graphical session. */
class DialogBoxTest {
    @Test
    void calculateContentWidth_botReply_reservesAvatarAndGap() {
        assertEquals(354, DialogBox.calculateContentWidth(400, false));
    }

    @Test
    void calculateContentWidth_userCommand_leavesSpaceOnLeft() {
        assertEquals(362 * 0.82, DialogBox.calculateContentWidth(400, true), 0.001);
        assertTrue(DialogBox.calculateContentWidth(400, true) < DialogBox.calculateContentWidth(400, false));
    }

    @Test
    void calculateContentWidth_initialOrTinyRow_neverReturnsNegativeWidth() {
        for (double width : new double[] {0, 10, 38}) {
            assertEquals(0, DialogBox.calculateContentWidth(width, false));
            assertEquals(0, DialogBox.calculateContentWidth(width, true));
        }
        assertEquals(0, DialogBox.calculateContentWidth(46, false));
    }

    @Test
    void calculateContentWidth_resizedRow_growsBeyondOldFixedLimit() {
        assertEquals(954, DialogBox.calculateContentWidth(1000, false));
        assertEquals(454, DialogBox.calculateContentWidth(500, false));
        assertEquals(962 * 0.82, DialogBox.calculateContentWidth(1000, true), 0.001);
    }

    @Test
    void calculateAvatarViewport_portrait_cropsTopAndBottomEqually() {
        assertEquals(new Rectangle2D(0, 150, 300, 300), DialogBox.calculateAvatarViewport(300, 600));
    }

    @Test
    void calculateAvatarViewport_landscape_cropsLeftAndRightEqually() {
        assertEquals(new Rectangle2D(150, 0, 300, 300), DialogBox.calculateAvatarViewport(600, 300));
    }

    @Test
    void calculateAvatarViewport_square_preservesWholeImage() {
        assertEquals(new Rectangle2D(0, 0, 300, 300), DialogBox.calculateAvatarViewport(300, 300));
    }

    @Test
    void calculateAvatarViewport_fractionalDimensions_preservesCentre() {
        assertEquals(new Rectangle2D(0.25, 0, 36, 36), DialogBox.calculateAvatarViewport(36.5, 36));
    }
}
