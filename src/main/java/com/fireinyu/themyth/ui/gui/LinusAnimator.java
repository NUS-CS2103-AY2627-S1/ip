package com.fireinyu.themyth.ui.gui;

import java.util.Random;
import java.util.function.DoubleSupplier;

import javafx.animation.AnimationTimer;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Controller and animator for the linus.png overlay.
 * Bounces Linus around the main window background like the classic DVD screensaver logo,
 * and shakes Linus violently for a short duration whenever a dialog box is created.
 */
public class LinusAnimator {

    /** Default travel speed in pixels per second. */
    public static final double DEFAULT_SPEED = 60.0;

    /** Duration of the violent shake in nanoseconds (600 milliseconds). */
    public static final long SHAKE_DURATION_NANOS = 600_000_000L;

    /** Target fit width and height of the Linus image. */
    public static final double DEFAULT_FIT_SIZE = 200.0;

    static final double FALLBACK_WIDTH = 400.0;
    static final double FALLBACK_HEIGHT = 557.0;

    private static final double INITIAL_POSITION = 40.0;
    private static final double INITIAL_ANGLE_DEGREES = 35.0;
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;
    private static final double MAX_FRAME_SECONDS = 0.1;
    private static final double SHAKE_BASE_INTENSITY = 4.0;
    private static final double SHAKE_EXTRA_INTENSITY = 12.0;
    private static final double SHAKE_MAX_ROTATION = 15.0;

    private final ImageView linusView;
    private final DoubleSupplier widthSupplier;
    private final DoubleSupplier heightSupplier;
    private final Random random;

    private double x = INITIAL_POSITION;
    private double y = INITIAL_POSITION;
    private double vx = DEFAULT_SPEED * Math.cos(Math.toRadians(INITIAL_ANGLE_DEGREES));
    private double vy = DEFAULT_SPEED * Math.sin(Math.toRadians(INITIAL_ANGLE_DEGREES));

    private long shakeUntilNanos = 0;
    private long lastTimeNanos = 0;
    private AnimationTimer animationTimer;

    /**
     * Constructs a LinusAnimator with the given view and dimension suppliers.
     *
     * @param linusView the ImageView displaying Linus.
     * @param widthSupplier supplier providing the current boundary width.
     * @param heightSupplier supplier providing the current boundary height.
     */
    public LinusAnimator(ImageView linusView, DoubleSupplier widthSupplier, DoubleSupplier heightSupplier) {
        this(linusView, widthSupplier, heightSupplier, new Random());
    }

    /**
     * Constructs a LinusAnimator with a custom random generator for testing.
     *
     * @param linusView the ImageView displaying Linus.
     * @param widthSupplier supplier providing current boundary width.
     * @param heightSupplier supplier providing current boundary height.
     * @param random random instance used for shake calculations.
     */
    public LinusAnimator(ImageView linusView, DoubleSupplier widthSupplier, DoubleSupplier heightSupplier,
            Random random) {
        this.linusView = linusView;
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
        this.random = random;
    }

    /**
     * Starts the DVD bounce animation loop and prepares the Linus image view.
     */
    public void start() {
        initializeView();
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onFrame(now);
            }
        };
        animationTimer.start();
    }

    /**
     * Loads and configures the overlay image when a view is available.
     */
    private void initializeView() {
        if (linusView == null) {
            return;
        }
        if (linusView.getImage() == null) {
            var stream = getClass().getResourceAsStream("/images/linus.png");
            if (stream != null) {
                linusView.setImage(new Image(stream));
            }
        }
        linusView.setFitWidth(DEFAULT_FIT_SIZE);
        linusView.setFitHeight(DEFAULT_FIT_SIZE);
        linusView.setPreserveRatio(true);
        linusView.setMouseTransparent(true);
        linusView.setEffect(new DropShadow(8.0, Color.rgb(0, 0, 0, 0.3)));
        linusView.setLayoutX(x);
        linusView.setLayoutY(y);
    }

    /**
     * Stops the animation loop.
     */
    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    /**
     * Triggers a violent shake of Linus for a short duration.
     * Multiple triggers will extend the shake duration from the moment of the latest trigger.
     */
    public void shakeViolently() {
        shakeUntilNanos = System.nanoTime() + SHAKE_DURATION_NANOS;
    }

    /**
     * Returns whether Linus is currently shaking.
     *
     * @return true if currently within the violent shake duration.
     */
    public boolean isShaking() {
        return System.nanoTime() < shakeUntilNanos;
    }

    /**
     * Handles one frame of the animation loop.
     *
     * @param now timestamp in nanoseconds.
     */
    void onFrame(long now) {
        if (lastTimeNanos == 0) {
            lastTimeNanos = now;
            return;
        }

        double elapsedSeconds = (now - lastTimeNanos) / NANOS_PER_SECOND;
        lastTimeNanos = now;

        // Limit movement after paused threads or lag spikes.
        if (elapsedSeconds > MAX_FRAME_SECONDS) {
            elapsedSeconds = MAX_FRAME_SECONDS;
        }

        double width = widthSupplier != null ? widthSupplier.getAsDouble() : FALLBACK_WIDTH;
        double height = heightSupplier != null ? heightSupplier.getAsDouble() : FALLBACK_HEIGHT;

        updateBounceState(elapsedSeconds, width, height);
        updateView(now);
    }

    /**
     * Applies the current position and shake offsets to the overlay when a view is available.
     */
    private void updateView(long now) {
        if (linusView == null) {
            return;
        }
        linusView.setLayoutX(x);
        linusView.setLayoutY(y);

        if (now < shakeUntilNanos) {
            double remainingRatio = (shakeUntilNanos - now) / (double) SHAKE_DURATION_NANOS;
            double intensity = SHAKE_BASE_INTENSITY + SHAKE_EXTRA_INTENSITY * remainingRatio;
            double jitterX = (random.nextDouble() * 2.0 - 1.0) * intensity;
            double jitterY = (random.nextDouble() * 2.0 - 1.0) * intensity;
            double jitterRotate = (random.nextDouble() * 2.0 - 1.0) * (SHAKE_MAX_ROTATION * remainingRatio);

            linusView.setTranslateX(jitterX);
            linusView.setTranslateY(jitterY);
            linusView.setRotate(jitterRotate);
        } else if (linusView.getTranslateX() != 0 || linusView.getTranslateY() != 0
                || linusView.getRotate() != 0) {
            linusView.setTranslateX(0);
            linusView.setTranslateY(0);
            linusView.setRotate(0);
        }
    }

    /**
     * Updates Linus's (x, y) coordinates and reflects velocity when bouncing off walls.
     *
     * @param elapsedSeconds delta time in seconds.
     * @param width boundary width.
     * @param height boundary height.
     */
    public void updateBounceState(double elapsedSeconds, double width, double height) {
        double linusWidth = (linusView != null && linusView.getFitWidth() > 0)
                ? linusView.getFitWidth() : DEFAULT_FIT_SIZE;
        double linusHeight = (linusView != null && linusView.getFitHeight() > 0)
                ? linusView.getFitHeight() : DEFAULT_FIT_SIZE;

        double maxX = Math.max(0.0, width - linusWidth);
        double maxY = Math.max(0.0, height - linusHeight);

        x += vx * elapsedSeconds;
        y += vy * elapsedSeconds;

        if (x <= 0.0) {
            x = 0.0;
            vx = Math.abs(vx);
        } else if (maxX > 0.0 && x >= maxX) {
            x = maxX;
            vx = -Math.abs(vx);
        }

        if (y <= 0.0) {
            y = 0.0;
            vy = Math.abs(vy);
        } else if (maxY > 0.0 && y >= maxY) {
            y = maxY;
            vy = -Math.abs(vy);
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public long getShakeUntilNanos() {
        return shakeUntilNanos;
    }

    public void setShakeUntilNanos(long shakeUntilNanos) {
        this.shakeUntilNanos = shakeUntilNanos;
    }
}
