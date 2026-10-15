package remy.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import javafx.geometry.Rectangle2D;

class DialogBoxTest {
    @Test
    void calculateSquareViewport_landscapeImage_returnsHorizontallyCenteredSquare() {
        Rectangle2D viewport = DialogBox.calculateSquareViewport(600, 500);

        assertEquals(new Rectangle2D(50, 0, 500, 500), viewport);
    }

    @Test
    void calculateSquareViewport_portraitImage_returnsVerticallyCenteredSquare() {
        Rectangle2D viewport = DialogBox.calculateSquareViewport(400, 600);

        assertEquals(new Rectangle2D(0, 100, 400, 400), viewport);
    }

    @Test
    void calculateSquareViewport_squareImage_returnsWholeImage() {
        Rectangle2D viewport = DialogBox.calculateSquareViewport(320, 320);

        assertEquals(new Rectangle2D(0, 0, 320, 320), viewport);
    }
}
