package jelly;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * A reusable JavaFX component for displaying one conversation message.
 */
public class DialogBox extends HBox {

    @FXML
    private Label text;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a message box.
     *
     * @param message the message to display.
     * @param image the avatar image to display.
     */
    public DialogBox(String message, Image image) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml", e);
        }

        text.setText(message);
        displayPicture.setImage(image);
        HBox.setHgrow(text, Priority.ALWAYS);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        getChildren().setAll(tmp);
    }

    /**
     * Creates a dialog box for user input.
     *
     * @param message the user's message.
     * @param image the user's avatar.
     * @return a user-styled dialog box.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.getStyleClass().add("user-message");
        return dialogBox;
    }

    /**
     * Creates a dialog box for Jelly's response.
     *
     * @param message Jelly's response.
     * @param image Jelly's avatar.
     * @return a flipped Jelly-styled dialog box.
     */
    public static DialogBox getJellyDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("jelly-message");
        return dialogBox;
    }
}
