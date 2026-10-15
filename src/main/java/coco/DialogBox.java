package coco;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A single row in the chat log. The user's own messages are shown as plain
 * text - they already know what they typed, so a bubble would just repeat
 * it back with no new information. Coco's replies get a bubble and a small
 * avatar, since Coco is the one fixed "other party" in the conversation and
 * benefits from a visual identity the user's side doesn't need.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 14;

    private DialogBox(String text, String styleClass, Pos alignment, boolean showAvatar) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.getStyleClass().add(styleClass);
        // Bind to this row's own width (which tracks the window's width,
        // via the VBox/ScrollPane it's placed in) instead of a fixed pixel
        // cap, so replies reflow to use the available space when the
        // window is resized rather than wrapping at the same point always.
        message.maxWidthProperty().bind(widthProperty().multiply(0.72));

        setAlignment(alignment);
        setSpacing(8);
        if (showAvatar) {
            getChildren().addAll(createAvatar(), message);
        } else {
            getChildren().add(message);
        }
    }

    /**
     * Creates a dialog box for a message the user typed: plain right-
     * aligned text, no bubble, no avatar.
     *
     * @param text Message text.
     * @return The dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "user-message", Pos.CENTER_RIGHT, false);
    }

    /**
     * Creates a dialog box for one of Coco's replies: a bubble with a small
     * avatar, left-aligned.
     *
     * @param text Message text.
     * @return The dialog box.
     */
    public static DialogBox getCocoDialog(String text) {
        return new DialogBox(text, "coco-bubble", Pos.TOP_LEFT, true);
    }

    /**
     * Builds Coco's avatar: a small solid-color circle with a palm tree
     * glyph. Drawn as a vector shape rather than an image file, so there's
     * no rectangular background to clash with the window behind it, and no
     * binary asset to track in version control.
     */
    private static StackPane createAvatar() {
        Circle circle = new Circle(AVATAR_RADIUS, Color.web("#FF8A5B"));
        Label glyph = new Label("🌴");
        glyph.getStyleClass().add("avatar-glyph");
        return new StackPane(circle, glyph);
    }
}
