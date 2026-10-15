package clearblue.gui;

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
import javafx.scene.shape.Circle;

/**
 * A single chat bubble: an avatar image paired with a label of text from
 * either the user or Clearblue.
 */
public class DialogBox extends HBox {
    // Clearblue keeps its full-size avatar (personality matters more on its
    // side), but the user doesn't need a big picture of themselves, so their
    // avatar is shrunk. This also gives the two sides a visibly different
    // shape rather than just a mirrored, recolored copy of each other.
    private static final double BOT_AVATAR_DIAMETER = 99.0;
    private static final double USER_AVATAR_DIAMETER = 56.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image, double avatarDiameter) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        double avatarRadius = avatarDiameter / 2;
        dialog.setText(text);
        displayPicture.setImage(image);
        displayPicture.setFitWidth(avatarDiameter);
        displayPicture.setFitHeight(avatarDiameter);
        displayPicture.setClip(new Circle(avatarRadius, avatarRadius, avatarRadius));
    }

    /**
     * Mirrors the dialog box so the avatar is on the left and text on the
     * right. Callers add whichever label style class (reply vs. error) fits
     * the message.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for a message the user typed.
     *
     * @param text message text
     * @param image user's avatar
     * @return the dialog box, aligned to the right
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image, USER_AVATAR_DIAMETER);
    }

    /**
     * Creates a dialog box for Clearblue's reply, flipped to the left with
     * the reply style applied.
     *
     * @param text reply text
     * @param image Clearblue's avatar
     * @return the dialog box, aligned to the left
     */
    public static DialogBox getClearblueDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, BOT_AVATAR_DIAMETER);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add("reply-label");
        return dialogBox;
    }

    /**
     * Creates a dialog box for an error message, flipped to the left like a
     * normal reply but styled to stand out from one.
     *
     * @param text error message text
     * @param image Clearblue's avatar
     * @return the dialog box, aligned to the left
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, BOT_AVATAR_DIAMETER);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }
}
