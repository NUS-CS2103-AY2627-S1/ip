package vermithor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** A single chat row pairing an avatar with a message bubble, mirrored for bot vs user messages. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 40;

    private DialogBox(String text, Image avatar, boolean isUser, boolean isError) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(420);
        bubble.setPadding(new Insets(10, 14, 10, 14));
        String background = isError ? "#f6d9d9" : (isUser ? "#dce7e4" : "#e7f0ee");
        bubble.setStyle("-fx-background-color: " + background + "; -fx-background-radius: 12;"
                + " -fx-font-size: 13px;");

        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(AVATAR_SIZE);
        avatarView.setFitHeight(AVATAR_SIZE);
        avatarView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        setSpacing(10);
        setPadding(new Insets(4, 12, 4, 12));
        if (isUser) {
            setAlignment(Pos.CENTER_RIGHT);
            getChildren().addAll(bubble, avatarView);
        } else {
            setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(avatarView, bubble);
        }
    }

    /** Creates a left-aligned dialog row for a message from Vermithor. */
    public static DialogBox botMessage(String text, Image avatar) {
        return new DialogBox(text, avatar, false, text.startsWith("OOPS"));
    }

    /** Creates a right-aligned dialog row for a message typed by the user. */
    public static DialogBox userMessage(String text, Image avatar) {
        return new DialogBox(text, avatar, true, false);
    }
}
