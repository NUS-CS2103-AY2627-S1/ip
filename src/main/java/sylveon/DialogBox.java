package sylveon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import java.io.IOException;

/**
 * Represents one message in the Sylveon conversation.
 */
public class DialogBox extends HBox {
    @FXML private Label dialog;
    @FXML private ImageView displayPicture;

    /**
     * Creates a message with text and an avatar.
     *
     * @param message the message text
     * @param image the avatar image
     */
    public DialogBox(String message, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load DialogBox.fxml", e);
        }
        dialog.setText(message);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(36, 36, 36));
    }

    /** Returns a dialog box positioned on the right for the user. */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /** Returns a dialog box positioned on the left for Sylveon's response. */
    public static DialogBox getSylveonDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /** Moves the avatar to the left and the message to the right. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
    }
}
