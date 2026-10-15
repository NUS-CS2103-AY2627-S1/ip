package alexa;

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
import javafx.scene.layout.Priority;

/** Represents one chat message with text and an avatar image. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /** Creates a dialog box from its FXML view. */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a chat dialog.", exception);
        }

        assert dialog != null : "DialogBox.fxml must inject the dialog label.";
        assert displayPicture != null : "DialogBox.fxml must inject the avatar ImageView.";

        setMaxWidth(Double.MAX_VALUE);
        dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Flips this dialog so its avatar appears on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a right-aligned dialog for a user message.
     *
     * @param text The message text.
     * @param image The user's avatar image.
     * @return A user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a left-aligned dialog styled to highlight an Alexa error message.
     *
     * @param text The error message text.
     * @param image Alexa's avatar image.
     * @return An error dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getAlexaDialog(text, image);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    /**
     * Returns a left-aligned dialog for an Alexa message.
     *
     * @param text The message text.
     * @param image Alexa's avatar image.
     * @return An Alexa dialog box.
     */
    public static DialogBox getAlexaDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }
}