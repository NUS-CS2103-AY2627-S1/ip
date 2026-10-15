package zabud.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one user or Zabud message with its speaker avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box from its FXML view.
     */
    private DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load dialog box", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog for user input.
     *
     * @param text user message.
     * @param image user avatar.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Zabud's response.
     *
     * @param text Zabud response.
     * @param image Zabud avatar.
     * @param isError whether the response reports invalid input.
     * @return Zabud dialog box.
     */
    public static DialogBox getZabudDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        return dialogBox;
    }

    /**
     * Moves the avatar to the left side for Zabud responses.
     */
    private void flip() {
        getChildren().setAll(displayPicture, dialog);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("zabud-dialog");
    }
}
