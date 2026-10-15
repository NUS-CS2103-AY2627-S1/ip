package min.ui.gui;

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
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    /**
     * Width taken up by the avatar, the spacing beside it, and the row padding.
     * Subtracting it keeps a bubble from stretching across the whole window.
     */
    private static final double AVATAR_ALLOWANCE = 90.0;

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
            throw new IllegalStateException(
                    "Unable to load DialogBox.fxml.", e);
        }

        assert this.dialog != null : "dialog must be injected from FXML.";
        assert this.displayPicture != null
                : "displayPicture must be injected from FXML.";

        this.getStyleClass().add("dialog-box");
        this.dialog.getStyleClass().add("dialog-text");
        this.dialog.maxWidthProperty().bind(this.widthProperty().subtract(AVATAR_ALLOWANCE));

        this.dialog.setText(text);
        this.displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> children =
                FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
        this.setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for the user's message.
     *
     * @param text The message to display.
     * @param image The user's profile image.
     * @return The created dialog box.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a dialog box for Min's response.
     *
     * @param text The response to display.
     * @param image Min's profile image.
     * @return The created dialog box.
     */
    public static DialogBox createMinDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("min-bubble");
        dialogBox.flip();
        return dialogBox;
    }
}
