package choo.gui;

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
 * Represents one message and the avatar of its speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        this.dialog.setText(text);
        this.displayPicture.setImage(image);
        this.dialog.maxWidthProperty().bind(Bindings.max(
                180.0, widthProperty().multiply(0.78).subtract(52.0)));
    }

    /**
     * Creates a right-aligned message from the user.
     *
     * @param text Message text.
     * @param image User avatar.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned message from CHOO.
     *
     * @param text Message text.
     * @param image CHOO avatar.
     * @return CHOO dialog box.
     */
    public static DialogBox getChooDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("choo-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a visually prominent error response from CHOO.
     *
     * @param text Error response text.
     * @param image CHOO avatar.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getChooDialog(text, image);
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
