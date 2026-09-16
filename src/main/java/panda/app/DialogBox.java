package panda.app;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents one user or Panda message in the chat history.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 58;

    /**
     * Creates a styled message bubble.
     *
     * @param text message text
     * @param fromPanda whether the message is from Panda
     */
    public DialogBox(String text, Image avatar, boolean fromPanda) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(fromPanda ? "panda-bubble" : "user-bubble");
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(AVATAR_SIZE);
        avatarView.setFitHeight(AVATAR_SIZE);
        avatarView.setPreserveRatio(true);
        avatarView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        avatarView.getStyleClass().add("avatar");
        setAlignment(fromPanda ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
        getStyleClass().add(fromPanda ? "panda-dialog" : "user-dialog");
        if (fromPanda) {
            getChildren().addAll(avatarView, label);
        } else {
            getChildren().addAll(label, avatarView);
        }
    }
}
