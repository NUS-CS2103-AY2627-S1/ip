package crack.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * One message in the chat.
 * Crack speaks from the left behind an avatar and a name label, while the user
 * speaks from the right as a bare bubble, so neither side reads as the other.
 * Anything that went wrong gets its own tinted bubble.
 */
public class DialogBox extends HBox {
    /** Share of the row width a bubble may fill before its text wraps. */
    private static final double BUBBLE_WIDTH_RATIO = 0.78;

    private static final String CRACK_NAME = "Crack";
    private static final String CRACK_GLYPH = "C";
    private static final String ERROR_GLYPH = "!";

    private DialogBox(Pos alignment) {
        getStyleClass().add("dialog-row");
        setAlignment(alignment);
    }

    /**
     * Returns a box holding something the user typed.
     *
     * @param text The line the user sent.
     * @return The box to add to the chat.
     */
    public static DialogBox forUser(String text) {
        DialogBox box = new DialogBox(Pos.TOP_RIGHT);
        box.getChildren().add(box.createBubble(text, "bubble-user"));
        return box;
    }

    /**
     * Returns a box holding something Crack said back.
     *
     * @param text The reply to show.
     * @return The box to add to the chat.
     */
    public static DialogBox forCrack(String text) {
        return createFromCrack(text, "bubble-crack", CRACK_GLYPH, false);
    }

    /**
     * Returns a box holding a reply that reports a problem.
     * It is tinted and rule-marked so the user cannot skim past it.
     *
     * @param text The complaint to show.
     * @return The box to add to the chat.
     */
    public static DialogBox forError(String text) {
        return createFromCrack(text, "bubble-error", ERROR_GLYPH, true);
    }

    /**
     * Builds one of Crack's messages: avatar on the left, name above the bubble.
     *
     * @param text The reply to show.
     * @param bubbleStyle Style class deciding how the bubble is painted.
     * @param glyph The single character shown inside the avatar.
     * @param isError Whether the avatar should be painted as a warning.
     * @return The box to add to the chat.
     */
    private static DialogBox createFromCrack(String text, String bubbleStyle, String glyph, boolean isError) {
        DialogBox box = new DialogBox(Pos.TOP_LEFT);

        Label avatar = new Label(glyph);
        avatar.getStyleClass().add("avatar");
        if (isError) {
            avatar.getStyleClass().add("avatar-error");
        }

        Label name = new Label(CRACK_NAME);
        name.getStyleClass().add("speaker-name");

        VBox column = new VBox(name, box.createBubble(text, bubbleStyle));
        column.setAlignment(Pos.TOP_LEFT);

        box.getChildren().addAll(avatar, column);
        return box;
    }

    /**
     * Builds the rounded panel the message text sits in.
     *
     * @param text The message to show.
     * @param bubbleStyle Style class deciding how the bubble is painted.
     * @return The label to drop into the row.
     */
    private Label createBubble(String text, String bubbleStyle) {
        Label bubble = new Label(text);
        bubble.getStyleClass().addAll("bubble", bubbleStyle);
        bubble.setWrapText(true);
        bubble.maxWidthProperty().bind(widthProperty().multiply(BUBBLE_WIDTH_RATIO));
        return bubble;
    }
}
