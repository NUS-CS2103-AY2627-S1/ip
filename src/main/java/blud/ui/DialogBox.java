package blud.ui;

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

/** Displays one speaker's message and avatar in the conversation. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /** Creates and initializes one message row from the reusable FXML template. */
    private DialogBox(String text, Image image, boolean isError) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a conversation message.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.TOP_RIGHT);
        if (isError) {
            dialog.getStyleClass().add("error-message");
        }
    }

    /** Returns a right-aligned message representing the user. */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, false);
    }

    /** Returns a left-aligned message representing Blud. */
    public static DialogBox getBludDialog(String text, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, image, isError);
        dialogBox.flip();
        return dialogBox;
    }

    /** Reverses the message children so the avatar appears before the text. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
