package esther.gui;

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
 * Represents a chat message with text and a speaker image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label messageLabel;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load the dialog box view.", exception);
        }

        getStyleClass().add("dialog-box");
        messageLabel.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog aligned as a user message.
     *
     * @param text Message text.
     * @param image User avatar.
     * @return User dialog box.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog aligned as an Esther response.
     *
     * @param text Message text.
     * @param image Esther avatar.
     * @return Esther dialog box.
     */
    public static DialogBox createEstherDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a visually distinct dialog for an error response.
     *
     * @param text Error message text.
     * @param image Esther avatar.
     * @return Error dialog box.
     */
    public static DialogBox createErrorDialog(String text, Image image) {
        DialogBox dialogBox = createEstherDialog(text, image);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(
                getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("esther-dialog");
    }
}
