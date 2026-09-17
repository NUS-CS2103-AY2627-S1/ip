package potatobot.ui.gui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * Adds a short decelerating glide to scroll input anywhere over a conversation.
 */
final class SmoothScroll {
    private static final Duration GLIDE_DURATION = Duration.millis(180);

    private final ScrollPane pane;
    private final Timeline glide = new Timeline();
    private double target;

    /**
     * Captures scroll events before child controls handle them, regardless of
     * keyboard focus.
     *
     * @param pane Scroll pane whose conversation content should scroll smoothly.
     */
    SmoothScroll(ScrollPane pane) {
        this.pane = pane;
        pane.addEventFilter(ScrollEvent.SCROLL, this::scroll);
        pane.addEventFilter(ScrollEvent.SCROLL_STARTED, event -> stop());
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> stop());
        pane.addEventFilter(KeyEvent.KEY_PRESSED, event -> stop());
        pane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> stop());
    }

    /**
     * Stops pending motion so scrollbar dragging, keyboard input and new replies
     * take priority.
     */
    void stop() {
        glide.stop();
    }

    /**
     * Accumulates quick scroll movements and lets the platform's native inertia
     * pass through directly.
     *
     * @param event Scroll input containing the pixel delta and native inertia flag.
     */
    private void scroll(ScrollEvent event) {
        double overflow = pane.getContent().getLayoutBounds().getHeight() - pane.getViewportBounds().getHeight();
        if (event.getDeltaY() == 0 || overflow <= 0 || event.isControlDown()) {
            return;
        }

        double current = pane.getVvalue();
        double pending = glide.getStatus() == Animation.Status.RUNNING ? target : current;
        target = calculateTarget(current, pending, event.getDeltaY(), overflow, pane.getVmin(), pane.getVmax());
        stop();

        if (event.isInertia()) {
            // Native touchpad momentum already decelerates; do not add another animation on
            // top.
            pane.setVvalue(target);
        } else {
            glide.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(pane.vvalueProperty(), current)),
                    new KeyFrame(GLIDE_DURATION,
                            new KeyValue(pane.vvalueProperty(), target, Interpolator.EASE_OUT)));
            glide.playFromStart();
        }
        event.consume();
    }

    /**
     * Calculates the target scroll position from a vertical pixel delta.
     *
     * <p>
     * Accumulates movement toward the pending target when scrolling in the same
     * direction.
     * When the direction reverses, starts from the current position to respond
     * immediately.
     * If the content fits within the viewport, returns the current position
     * unchanged.
     *
     * @param current  Current vertical scroll position in the scroll pane's value
     *                 range.
     * @param pending  Target of the active glide, or {@code current} if no glide is
     *                 active.
     * @param delta    Vertical scroll delta in pixels; positive values scroll
     *                 toward the top.
     * @param overflow Content height minus viewport height, in pixels.
     * @param min      Minimum vertical scroll value, representing the top of the
     *                 content.
     * @param max      Maximum vertical scroll value, representing the bottom of the
     *                 content.
     * @return Target clamped to {@code [min, max]}, or {@code current} when
     *         {@code overflow <= 0}.
     */
    static double calculateTarget(double current, double pending, double delta,
            double overflow, double min, double max) {
        if (overflow <= 0) {
            return current;
        }
        double movement = -delta / overflow * (max - min);
        double start = (pending - current) * movement < 0 ? current : pending;
        return Math.clamp(start + movement, min, max);
    }
}
