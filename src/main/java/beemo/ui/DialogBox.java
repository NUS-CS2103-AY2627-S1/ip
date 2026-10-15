package beemo.ui;

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
import javafx.scene.shape.Rectangle;

/**
 * Displays one chat message beside its speaker's avatar.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 48.0;
    private static final double AVATAR_CORNER_RADIUS = 16.0;

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
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        applyRoundedAvatarClip();
    }

    /**
     * Clips the avatar to a compact square with rounded corners.
     */
    private void applyRoundedAvatarClip() {
        Rectangle avatarClip = new Rectangle(AVATAR_SIZE, AVATAR_SIZE);
        avatarClip.setArcWidth(AVATAR_CORNER_RADIUS);
        avatarClip.setArcHeight(AVATAR_CORNER_RADIUS);
        displayPicture.setClip(avatarClip);
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog spoken by the user.
     *
     * @param text Message text.
     * @param image User avatar.
     * @return Dialog box for the user.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /**
     * Creates a dialog spoken by Beemo.
     *
     * @param text Message text.
     * @param image Beemo avatar.
     * @return Dialog box for Beemo.
     */
    public static DialogBox getBeemoDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates an error dialog spoken by Beemo.
     *
     * @param text Error message text.
     * @param image Beemo avatar.
     * @return Error dialog box for Beemo.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getBeemoDialog(text, image);
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }
}
