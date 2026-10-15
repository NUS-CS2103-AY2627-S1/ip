package surveyprogram.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Animates reusable, expanding IMAGE_DEPTH layers at 30 logical steps per second.
 */
public class DepthBackground extends AnimationTimer {
    private static final double STEPS_PER_SECOND = 30.0;
    private static final double NANOS_PER_STEP = 1_000_000_000.0 / STEPS_PER_SECOND;
    private static final double MAXIMUM_FRAME_DELTA_NANOS = NANOS_PER_STEP * 2;
    private static final int SPAWN_INTERVAL_STEPS = 40;
    private static final int BIRTH_FADE_STEPS = 20;
    private static final int LAYER_POOL_SIZE = 8;
    private static final double EXPANSION_PER_STEP = 0.005;
    private static final double FADE_INCREMENT = 0.01;
    private static final double BASE_OPACITY = 0.5;
    private static final double PULSE_AMOUNT = 0.2;
    private static final double MAXIMUM_OSCILLATING_OPACITY = BASE_OPACITY + PULSE_AMOUNT;

    private final Pane backgroundPane;
    private final Image image;
    private final List<DepthLayer> layers = new ArrayList<>();

    private long previousFrameNanos;
    private double accumulatedNanos;
    private int stepsUntilSpawn;

    /**
     * Creates an IMAGE_DEPTH animator in the given background pane.
     *
     * @param backgroundPane pane that remains behind the app interface
     */
    public DepthBackground(Pane backgroundPane) {
        this.backgroundPane = backgroundPane;
        image = new Image(DepthBackground.class.getResourceAsStream("/images/IMAGE_DEPTH_BG.png"));
        backgroundPane.widthProperty().addListener(observable -> resizeLayers());
        backgroundPane.heightProperty().addListener(observable -> resizeLayers());
        createLayerPool();
        spawnLayer();
    }

    @Override
    public void start() {
        previousFrameNanos = 0;
        accumulatedNanos = 0;
        super.start();
    }

    @Override
    public void handle(long now) {
        if (previousFrameNanos == 0) {
            previousFrameNanos = now;
            renderLayers();
            return;
        }

        double frameDeltaNanos = Math.min(now - previousFrameNanos, MAXIMUM_FRAME_DELTA_NANOS);
        accumulatedNanos += frameDeltaNanos;
        previousFrameNanos = now;
        while (accumulatedNanos >= NANOS_PER_STEP) {
            advanceOneStep();
            accumulatedNanos -= NANOS_PER_STEP;
        }
        renderLayers();
    }

    private void advanceOneStep() {
        for (DepthLayer layer : layers) {
            if (!layer.isActive) {
                continue;
            }
            layer.advance();
            if (!layer.canAffectFutureFrames()) {
                layer.deactivate();
            }
        }

        stepsUntilSpawn--;
        if (stepsUntilSpawn <= 0) {
            spawnLayer();
        }
    }

    private void createLayerPool() {
        for (int index = 0; index < LAYER_POOL_SIZE; index++) {
            DepthLayer layer = new DepthLayer(image);
            layers.add(layer);
            backgroundPane.getChildren().add(layer.imageView);
        }
    }

    private void spawnLayer() {
        for (DepthLayer layer : layers) {
            if (!layer.isActive) {
                layer.activate();
                layer.imageView.toFront();
                break;
            }
        }
        stepsUntilSpawn = SPAWN_INTERVAL_STEPS;
    }

    private void resizeLayers() {
        for (DepthLayer layer : layers) {
            resizeLayer(layer);
        }
    }

    private void resizeLayer(DepthLayer layer) {
        double paneWidth = backgroundPane.getWidth();
        double paneHeight = backgroundPane.getHeight();
        if (paneWidth <= 0 || paneHeight <= 0) {
            return;
        }

        double coverScale = Math.max(paneWidth / image.getWidth(), paneHeight / image.getHeight());
        double width = image.getWidth() * coverScale;
        double height = image.getHeight() * coverScale;
        layer.imageView.setFitWidth(width);
        layer.imageView.setFitHeight(height);
        layer.imageView.relocate((paneWidth - width) / 2, (paneHeight - height) / 2);
    }

    private void renderLayers() {
        double stepProgress = accumulatedNanos / NANOS_PER_STEP;
        for (DepthLayer layer : layers) {
            if (layer.isActive) {
                layer.render(stepProgress);
            }
        }
    }

    /** Stores the independently changing state and reusable view of one background layer. */
    private static class DepthLayer {
        private final ImageView imageView;

        private boolean isActive;
        private int ageInSteps;
        private double scale = 1;
        private double fadeInAdjustment = -PULSE_AMOUNT;
        private double fadeOutAdjustment;

        private DepthLayer(Image image) {
            imageView = new ImageView(image);
            imageView.setManaged(false);
            imageView.setMouseTransparent(true);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            imageView.setOpacity(0);
        }

        private void activate() {
            isActive = true;
            ageInSteps = 0;
            scale = 1;
            fadeInAdjustment = -PULSE_AMOUNT;
            fadeOutAdjustment = 0;
            imageView.setScaleX(1);
            imageView.setScaleY(1);
            imageView.setOpacity(0);
        }

        private void deactivate() {
            isActive = false;
            imageView.setOpacity(0);
        }

        private void advance() {
            ageInSteps++;
            scale += EXPANSION_PER_STEP;
            if (fadeInAdjustment < 0) {
                fadeInAdjustment = Math.min(0, fadeInAdjustment + FADE_INCREMENT);
            }
            if (scale > 2) {
                fadeOutAdjustment -= FADE_INCREMENT;
            }
        }

        private void render(double stepProgress) {
            double renderedScale = scale + EXPANSION_PER_STEP * stepProgress;
            imageView.setScaleX(renderedScale);
            imageView.setScaleY(renderedScale);
            imageView.setOpacity(calculateOpacity(stepProgress));
        }

        private double calculateOpacity(double stepProgress) {
            double interpolatedAge = ageInSteps + stepProgress;
            double pulse = Math.sin(interpolatedAge / 34.0) * PULSE_AMOUNT;
            double interpolatedFadeIn = fadeInAdjustment;
            if (fadeInAdjustment < 0) {
                interpolatedFadeIn = Math.min(0, fadeInAdjustment + FADE_INCREMENT * stepProgress);
            }

            double interpolatedFadeOut = fadeOutAdjustment;
            if (scale > 2) {
                interpolatedFadeOut -= FADE_INCREMENT * stepProgress;
            }
            double opacity = BASE_OPACITY + pulse + interpolatedFadeIn + interpolatedFadeOut;
            double birthFade = Math.min(1, interpolatedAge / BIRTH_FADE_STEPS);
            return Math.max(0, Math.min(1, opacity * birthFade));
        }

        private boolean canAffectFutureFrames() {
            return scale <= 2 || fadeOutAdjustment > -MAXIMUM_OSCILLATING_OPACITY;
        }
    }
}
