package alex.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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
 * Represents a dialog containing a user's or Alex's message.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box.", e);
        }

        dialog.setText(text);
        boolean hasImage = image != null;
        displayPicture.setManaged(hasImage);
        displayPicture.setVisible(hasImage);
        displayPicture.setImage(image);
        dialog.maxWidthProperty().bind(Bindings.max(
                160.0,
                Bindings.min(widthProperty().multiply(0.88), widthProperty().subtract(60.0))));
    }

    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());

        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog representing a message from the user.
     *
     * @param text message to display.
     * @return dialog containing the user's message.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, null);
    }

    /**
     * Creates a dialog representing a message from Alex.
     *
     * @param text message to display.
     * @param image image representing Alex.
     * @return dialog containing Alex's message.
     */
    public static DialogBox getAlexDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a dialog representing an error from Alex.
     *
     * @param text error message to display.
     * @param image image representing Alex.
     * @return dialog containing Alex's error message.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getAlexDialog(text, image);
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }
}
