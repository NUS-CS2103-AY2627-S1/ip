package bot67.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Draws Unicode Braille art as dots so missing font glyphs cannot corrupt the picture. */
public class PersonalityArt extends Canvas {
    private static final double DOT_SPACING = 2.5;
    private static final double DOT_SIZE = 1.8;
    // Unicode numbers the left column first, then the right, with the bottom dots last.
    private static final int[] DOT_COLUMNS = {0, 0, 0, 1, 1, 1, 0, 1};
    private static final int[] DOT_ROWS = {0, 1, 2, 0, 1, 2, 3, 3};

    /** Renders each Braille cell using its eight-bit dot pattern. */
    public PersonalityArt(String art) {
        String[] lines = art.split("\n");
        int columns = art.lines().mapToInt(String::length).max().orElse(0);
        setWidth(columns * DOT_SPACING * 2);
        setHeight(lines.length * DOT_SPACING * 4);
        setAccessibleText("Bot67 personality art: 67 with two small characters underneath.");
        GraphicsContext graphics = getGraphicsContext2D();
        graphics.setFill(Color.web("#913553"));
        for (int row = 0; row < lines.length; row++) {
            for (int column = 0; column < lines[row].length(); column++) {
                drawCell(graphics, lines[row].charAt(column), column, row);
            }
        }
    }

    /** Maps one Braille character to its dots, ignoring characters outside the Braille block. */
    private void drawCell(GraphicsContext graphics, char cell, int column, int row) {
        if (cell < '\u2800' || cell > '\u28ff') {
            return;
        }
        int pattern = cell - '\u2800';
        for (int bit = 0; bit < 8; bit++) {
            if ((pattern & (1 << bit)) != 0) {
                double x = (column * 2 + DOT_COLUMNS[bit]) * DOT_SPACING;
                double y = (row * 4 + DOT_ROWS[bit]) * DOT_SPACING;
                graphics.fillOval(x, y, DOT_SIZE, DOT_SIZE);
            }
        }
    }
}
