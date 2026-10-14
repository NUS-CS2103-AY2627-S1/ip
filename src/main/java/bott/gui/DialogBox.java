package bott.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A single chat bubble in the conversation: a speaker tag (icon and name)
 * above a wrapped-text message, both in a rounded, coloured box. Messages
 * the recruit (user) typed are aligned to the right, in khaki; Sergeant
 * Bott's replies are aligned to the left, in olive drab.
 */
public class DialogBox extends HBox {

    /** Colours distinguishing the two speakers, in Sergeant Bott's camo palette. */
    private static final Color COLOR_USER = Color.web("#f4ecd8");
    private static final Color COLOR_BOTT = Color.web("#9caf88");

    /** Monospaced "field report" font; falls back to the platform default where unavailable. */
    private static final String FONT_FAMILY = "Consolas";
    private static final double TAG_FONT_SIZE = 11;
    private static final double MESSAGE_FONT_SIZE = 13;

    private static final CornerRadii CORNER_RADII = new CornerRadii(10);
    private static final Insets BUBBLE_PADDING = new Insets(8);
    private static final double SPACING = 8;
    private static final double TAG_SPACING = 2;

    /** Caps the bubble width so long replies wrap instead of stretching the window. */
    private static final double MAX_BUBBLE_WIDTH = 280;

    private DialogBox(String tag, String text, Color color, Pos alignment) {
        Label tagLabel = new Label(tag);
        tagLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, TAG_FONT_SIZE));

        Label messageLabel = new Label(text);
        messageLabel.setFont(Font.font(FONT_FAMILY, MESSAGE_FONT_SIZE));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(MAX_BUBBLE_WIDTH);

        VBox bubble = new VBox(TAG_SPACING, tagLabel, messageLabel);
        bubble.setMaxWidth(MAX_BUBBLE_WIDTH);
        bubble.setPadding(BUBBLE_PADDING);
        bubble.setBackground(new Background(new BackgroundFill(color, CORNER_RADII, Insets.EMPTY)));

        setSpacing(SPACING);
        setPadding(BUBBLE_PADDING);
        setAlignment(alignment);
        getChildren().add(bubble);
    }

    /**
     * Returns a right-aligned bubble for a message the recruit (user) typed.
     *
     * @param text Text the user entered.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox("🫡 YOU", text, COLOR_USER, Pos.TOP_RIGHT);
    }

    /**
     * Returns a left-aligned bubble for one of Sergeant Bott's replies.
     *
     * @param text Text of Sergeant Bott's reply.
     */
    public static DialogBox getBottDialog(String text) {
        return new DialogBox("🎖 SARGE", text, COLOR_BOTT, Pos.TOP_LEFT);
    }
}
