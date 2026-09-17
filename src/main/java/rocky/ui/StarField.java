package rocky.ui;

import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The starry sky painted behind the chat transcript, so the window reads as a
 * view out of a spacecraft rather than a plain black box.
 *
 * <p>Each star is stored as a fraction of the window's width and height rather
 * than as a pixel position, so the same stars simply spread out when the window
 * is resized instead of being reshuffled into a new pattern. The stars are
 * drawn from a fixed random seed, so the sky also looks the same every run.
 */
public class StarField extends Canvas {
    private static final int STAR_COUNT = 140;
    private static final double MIN_RADIUS = 0.4;
    private static final double MAX_RADIUS = 1.5;
    private static final double MIN_OPACITY = 0.2;
    private static final double MAX_OPACITY = 0.9;
    /** Fixed, so the sky is the same on every run instead of changing each time. */
    private static final long STAR_SEED = 2035;
    private static final Color STAR_COLOR = Color.WHITE;

    private final double[] xFractions = new double[STAR_COUNT];
    private final double[] yFractions = new double[STAR_COUNT];
    private final double[] radii = new double[STAR_COUNT];
    private final double[] opacities = new double[STAR_COUNT];

    /** Scatters the stars and repaints them whenever the canvas is given a new size. */
    public StarField() {
        Random random = new Random(STAR_SEED);
        for (int i = 0; i < STAR_COUNT; i++) {
            xFractions[i] = random.nextDouble();
            yFractions[i] = random.nextDouble();
            radii[i] = MIN_RADIUS + random.nextDouble() * (MAX_RADIUS - MIN_RADIUS);
            opacities[i] = MIN_OPACITY + random.nextDouble() * (MAX_OPACITY - MIN_OPACITY);
        }

        // Clicks and scrolls should reach the transcript in front of the sky.
        setMouseTransparent(true);
        widthProperty().addListener(observable -> draw());
        heightProperty().addListener(observable -> draw());
    }

    /** Repaints the whole sky at the canvas's current size. */
    private void draw() {
        GraphicsContext context = getGraphicsContext2D();
        context.clearRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < STAR_COUNT; i++) {
            context.setFill(STAR_COLOR.deriveColor(0, 1, 1, opacities[i]));
            double diameter = radii[i] * 2;
            context.fillOval(xFractions[i] * getWidth(), yFractions[i] * getHeight(), diameter, diameter);
        }
    }
}
