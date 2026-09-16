package downtowngurl.ui;

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
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing message text and an avatar image.
     *
     * @param text text to show in the dialog box.
     * @param image avatar image to show beside the message.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load DialogBox.fxml.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box such that the avatar is on the left and the text is on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        this.getChildren().setAll(tmp);
        this.setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for messages sent by the user.
     *
     * @param message text to show in the dialog box.
     * @param image avatar image to show beside the message.
     * @return dialog box for a user message.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a dialog box for messages sent by Downtown Gurl.
     *
     * @param message text to show in the dialog box.
     * @param image avatar image to show beside the message.
     * @return dialog box for a Downtown Gurl message.
     */
    public static DialogBox getDowntownGurlDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.getStyleClass().add("downtown-gurl-dialog");
        dialogBox.flip();
        return dialogBox;
    }
}
