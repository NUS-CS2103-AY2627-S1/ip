package sine.gui;

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
 * Represents one user or chatbot message and its speaker avatar.
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
        } catch (IOException exception) {
            throw new AssertionError("Unable to load dialog box layout", exception);
        }

        dialog.setText(text);
        // Reserve room for the avatar, gap, and padding as the window resizes.
        dialog.maxWidthProperty().bind(widthProperty().subtract(60));
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog with the user's avatar on the right.
     *
     * @param text User's message.
     * @param image User's avatar.
     * @return Dialog box for the user.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog with Sine's avatar on the left.
     *
     * @param text Sine's response.
     * @param image Sine's avatar.
     * @return Dialog box for Sine.
     */
    public static DialogBox getSineDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
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
