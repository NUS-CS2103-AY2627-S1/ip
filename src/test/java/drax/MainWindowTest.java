package drax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the responsive sizing calculations used by the JavaFX main window. */
public class MainWindowTest {
    private static final double DEFAULT_CONTENT_SCALE = 1;
    private static final double DEFAULT_CONTROL_SCALE = 1;
    private static final double DEFAULT_WINDOW_HEIGHT = 600;
    private static final double DEFAULT_WINDOW_WIDTH = 400;
    private static final double DOUBLE_WINDOW_HEIGHT = 1200;
    private static final double DOUBLE_WINDOW_WIDTH = 800;
    private static final double HALF_WINDOW_HEIGHT = 300;
    private static final double HALF_WINDOW_WIDTH = 200;
    private static final double LARGE_CONTENT_SCALE = 1.5;
    private static final double LARGE_CONTROL_SCALE = 1.25;
    private static final double LARGE_WINDOW_HEIGHT = 900;
    private static final double LARGE_WINDOW_WIDTH = 600;
    private static final double TOLERANCE = 0.0001;

    @Test
    public void calculateContentScale_defaultSize_returnsDefaultScale() {
        assertEquals(DEFAULT_CONTENT_SCALE,
                MainWindow.calculateContentScale(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT), TOLERANCE);
    }

    @Test
    public void calculateContentScale_proportionallyLarger_returnsProportionalScale() {
        assertEquals(LARGE_CONTENT_SCALE,
                MainWindow.calculateContentScale(LARGE_WINDOW_WIDTH, LARGE_WINDOW_HEIGHT), TOLERANCE);
    }

    @Test
    public void calculateContentScale_oneDimensionLarger_usesConstrainedDimension() {
        assertEquals(DEFAULT_CONTENT_SCALE,
                MainWindow.calculateContentScale(DOUBLE_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT), TOLERANCE);
        assertEquals(DEFAULT_CONTENT_SCALE,
                MainWindow.calculateContentScale(DEFAULT_WINDOW_WIDTH, DOUBLE_WINDOW_HEIGHT), TOLERANCE);
    }

    @Test
    public void calculateContentScale_smallerThanDefault_doesNotShrinkContent() {
        assertEquals(DEFAULT_CONTENT_SCALE,
                MainWindow.calculateContentScale(HALF_WINDOW_WIDTH, HALF_WINDOW_HEIGHT), TOLERANCE);
    }

    @Test
    public void calculateControlScale_defaultContentScale_returnsDefaultScale() {
        assertEquals(DEFAULT_CONTROL_SCALE,
                MainWindow.calculateControlScale(DEFAULT_CONTENT_SCALE), TOLERANCE);
    }

    @Test
    public void calculateControlScale_largerContentScale_appliesHalfOfGrowth() {
        assertEquals(LARGE_CONTROL_SCALE,
                MainWindow.calculateControlScale(LARGE_CONTENT_SCALE), TOLERANCE);
    }
}
