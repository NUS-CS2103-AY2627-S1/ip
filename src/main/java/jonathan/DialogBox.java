package jonathan;

import java.io.IOException;
import java.util.Collections;

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

/**
 * Represents a chat message and its speaker image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Loads the FXML layout for one dialog box.
     *
     * @param text message to display
     * @param image speaker image to display
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));

            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load DialogBox.fxml", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        dialog.getStyleClass().add("message-bubble");
    }

    /**
     * Moves the image to the left and the message to the right.
     */
    private void flip() {
        ObservableList<Node> reversedChildren =
                FXCollections.observableArrayList(getChildren());

        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * Creates a dialog box for the user.
     *
     * @param text user message
     * @param image user image
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-message");
        return dialogBox;
    }

    /**
     * Creates a dialog box for the chatbot.
     *
     * @param text chatbot response
     * @param image chatbot image
     * @return chatbot dialog box
     */
    public static DialogBox getJonathanDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add("griddel-message");
        return dialogBox;
    }
}
