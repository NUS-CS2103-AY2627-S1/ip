package bean.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/** Renders a resizable dotted background with a soft, monochrome aero gradient. */
public class DottedBackground extends Canvas {

    /** Creates and draws a background that redraws when its dimensions change. */
    public DottedBackground() {
        widthProperty().addListener(observable -> draw());
        heightProperty().addListener(observable -> draw());

        draw();
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        GraphicsContext gc = getGraphicsContext2D();

        LinearGradient backgroundGradient = new LinearGradient(
                0, 0, 0, 1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 102, 219)),
                new Stop(1, Color.rgb(41, 141, 255))
        );
        gc.setFill(backgroundGradient);
        gc.fillRect(0, 0, width, height);

        double spacing = 18;
        double radius = 2.2;

        for (double y = 0; y < height; y += spacing) {
            for (double x = 0; x < width; x += spacing) {
                double progress = (x / width + y / height) / 2.0;
                double opacity = 0.24 * (1 - progress) + 0.08 * progress;

                boolean isHighlightDot = ((int) (x / spacing) + (int) (y / spacing)) % 2 == 0;
                Color dotColor = isHighlightDot ? Color.rgb(218, 241, 255, opacity)
                        : Color.rgb(3, 92, 196, opacity * 0.55);
                gc.setFill(dotColor);
                gc.fillOval(
                        x - radius,
                        y - radius,
                        radius * 2,
                        radius * 2
                );

            }
        }
    }
}
