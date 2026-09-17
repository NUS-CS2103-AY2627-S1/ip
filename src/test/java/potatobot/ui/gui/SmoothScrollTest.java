package potatobot.ui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks scroll distance, accumulated motion and boundaries without requiring a
 * display server.
 */
public class SmoothScrollTest {
    @Test
    public void calculateTarget_pixelDelta_scaledToScrollRange() {
        assertEquals(0.6, SmoothScroll.calculateTarget(0.5, 0.5, -100, 1000, 0, 1), 0.00001);
        assertEquals(16, SmoothScroll.calculateTarget(15, 15, -100, 1000, 10, 20), 0.00001);
    }

    @Test
    public void calculateTarget_repeatedScroll_accumulatesDistance() {
        assertEquals(0.8, SmoothScroll.calculateTarget(0.5, 0.7, -100, 1000, 0, 1), 0.00001);
    }

    @Test
    public void calculateTarget_reversedScroll_changesDirectionImmediately() {
        assertEquals(0.4, SmoothScroll.calculateTarget(0.5, 0.7, 100, 1000, 0, 1), 0.00001);
        assertEquals(0.6, SmoothScroll.calculateTarget(0.5, 0.3, -100, 1000, 0, 1), 0.00001);
    }

    @Test
    public void calculateTarget_largeDelta_staysWithinBounds() {
        assertEquals(0, SmoothScroll.calculateTarget(0.5, 0.5, 2000, 1000, 0, 1));
        assertEquals(1, SmoothScroll.calculateTarget(0.5, 0.5, -2000, 1000, 0, 1));
    }

    @Test
    public void calculateTarget_contentFits_doesNotMove() {
        assertEquals(0.5, SmoothScroll.calculateTarget(0.5, 0.5, -100, 0, 0, 1));
        assertEquals(0.5, SmoothScroll.calculateTarget(0.5, 0.5, -100, -100, 0, 1));
    }
}
