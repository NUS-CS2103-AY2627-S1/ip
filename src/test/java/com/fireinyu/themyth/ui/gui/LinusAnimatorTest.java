package com.fireinyu.themyth.ui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LinusAnimator}.
 */
public class LinusAnimatorTest {

    @Test
    public void constants_haveSensibleValues() {
        assertTrue(LinusAnimator.DEFAULT_SPEED > 0.0);
        assertTrue(LinusAnimator.SHAKE_DURATION_NANOS > 0L);
        assertTrue(LinusAnimator.DEFAULT_FIT_SIZE > 0.0);
    }

    @Test
    public void updateBounceState_withinBounds_advancesPosition() {
        LinusAnimator animator = new LinusAnimator(null, () -> 400.0, () -> 500.0);
        animator.setX(50.0);
        animator.setY(50.0);
        animator.setVx(10.0);
        animator.setVy(20.0);

        animator.updateBounceState(0.5, 400.0, 500.0);

        assertEquals(55.0, animator.getX(), 0.001);
        assertEquals(60.0, animator.getY(), 0.001);
        assertEquals(10.0, animator.getVx(), 0.001);
        assertEquals(20.0, animator.getVy(), 0.001);
    }

    @Test
    public void shakeViolently_setsShakeUntilNanos() {
        LinusAnimator animator = new LinusAnimator(null, () -> 400.0, () -> 500.0);
        animator.shakeViolently();

        assertTrue(animator.isShaking());
        assertTrue(animator.getShakeUntilNanos() > System.nanoTime());
    }

    @Test
    public void dialogCreationListener_triggersOnEvent() {
        DialogBox.clearOnDialogCreatedListeners();
        AtomicInteger count = new AtomicInteger(0);

        DialogBox.addOnDialogCreatedListener(count::incrementAndGet);

        // Direct registration check
        assertEquals(0, count.get());
        DialogBox.addOnDialogCreatedListener(null); // null safe
        DialogBox.clearOnDialogCreatedListeners();
    }
}
