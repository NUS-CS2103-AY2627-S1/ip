package trackie.gui;

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

/** Represents one chat message and the avatar of its speaker. */
public class DialogBox extends HBox {
    private static final String ERROR_PREFIX = "Oops!";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load a dialog box", exception);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog for a user message.
     *
     * @param text message to display
     * @param image user's avatar
     * @return configured user dialog
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog for Trackie's response.
     *
     * @param text message to display
     * @param image Trackie's avatar
     * @return configured Trackie dialog
     */
    public static DialogBox getTrackieDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        if (text.startsWith(ERROR_PREFIX)) {
            dialogBox.dialog.getStyleClass().add("error-label");
        }
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
