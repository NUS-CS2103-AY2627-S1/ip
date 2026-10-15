package jeff.gui;

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
 * Displays a chat message together with an avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Loads a dialog box and supplies its message and avatar.
     *
     * @param message Message to display.
     * @param image Avatar to display.
     */
    private DialogBox(String message, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        dialog.setText(message);
        displayPicture.setImage(image);
    }

    /**
     * Places the avatar on the left and the text on the right.
     */
    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box for a user command.
     *
     * @param message Command to display.
     * @param image User avatar.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Returns a dialog box for a Jeff response.
     *
     * @param message Response to display.
     * @param image Jeff avatar.
     * @return Jeff dialog box.
     */
    public static DialogBox getJeffDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }
}
