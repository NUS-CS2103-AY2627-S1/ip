package pavanmaxxer;

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
 * Displays one chat message beside its speaker image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String message, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.",
                    exception);
        }
        dialog.setText(message);
        displayPicture.setImage(image);
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(
                getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog shown on the user's side.
     *
     * @param message Message text.
     * @param image User image.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a flipped dialog shown on Pavanmaxxer's side.
     *
     * @param message Message text.
     * @param image Pavanmaxxer image.
     * @return Pavanmaxxer dialog box.
     */
    public static DialogBox getPavanmaxxerDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }
}
