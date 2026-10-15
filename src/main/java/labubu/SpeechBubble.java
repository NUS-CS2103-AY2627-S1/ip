package labubu;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** Represents one user or Labubu message in the conversation. */
public class SpeechBubble extends HBox {
    private static final String IMAGE_PATH = "/images/labubuPic.png";

    /** Creates a speech bubble for a user or Labubu message. */
    public SpeechBubble(String text, boolean fromUser) {
        this(text, fromUser, false);
    }

    /**
     * Creates a speech bubble with optional error styling.
     *
     * @param text Message to display.
     * @param fromUser Whether the message was sent by the user.
     * @param isError Whether the message represents an input error.
     */
    public SpeechBubble(String text, boolean fromUser, boolean isError) {
        Label bubble = new Label(text == null || text.isEmpty() ? "Done." : text);
        bubble.setWrapText(true);
        bubble.getStyleClass().add(fromUser ? "user-bubble" : "bot-bubble");
        if (isError) {
            bubble.getStyleClass().add("error-bubble");
        }
        getStyleClass().add(fromUser ? "user-row" : "bot-row");
        if (!fromUser) {
            ImageView avatar = new ImageView(new Image(getClass().getResourceAsStream(IMAGE_PATH)));
            avatar.setFitWidth(80);
            avatar.setFitHeight(80);
            avatar.setPreserveRatio(true);
            getChildren().add(0, avatar);
        }
        getChildren().add(bubble);
        if (isError) {
            Label warningIcon = new Label("⚠");
            warningIcon.getStyleClass().add("error-icon");
            getChildren().add(warningIcon);
        }
        setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
    }
}
