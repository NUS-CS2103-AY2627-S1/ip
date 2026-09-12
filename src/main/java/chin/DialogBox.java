package chin;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A styled dialog row with a coloured avatar and a padded label.
 * Used for both user and Chin messages in the chat.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_RADIUS = 16;

    private DialogBox(String text, Color avatarColor, Pos alignment) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(280);
        label.setStyle("-fx-background-color: #eef2f7; -fx-background-radius: 8; -fx-padding: 8;");

        Circle avatar = new Circle(AVATAR_RADIUS, avatarColor);

        setSpacing(8);
        setAlignment(alignment);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        getChildren().addAll(label, avatar);
        if (alignment == Pos.TOP_LEFT) {
            getChildren().setAll(avatar, label);
        }
    }

    /** Creates a dialog box for a user message (blue avatar, right-aligned). */
    public static DialogBox forUser(String text) {
        return new DialogBox(text, Color.web("#4a90e2"), Pos.TOP_RIGHT);
    }

    /** Creates a dialog box for a Chin reply (green avatar, left-aligned). */
    public static DialogBox forChin(String text) {
        return new DialogBox(text, Color.web("#7ed321"), Pos.TOP_LEFT);
    }
}
