package mouse.gui;

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
 * A dialog row with optional face image and a text bubble.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog row and loads its FXML layout.
     *
     * @param text Bubble text.
     * @param img Face image to show, if the avatar stays visible.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load DialogBox.fxml", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box so the image is on the left.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Crops the avatar to a circle so it uses less space than a square photo.
     */
    private void clipAvatar() {
        double radius = AVATAR_SIZE / 2;
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Hides the avatar. User crumbs do not need a large profile photo.
     */
    private void hideAvatar() {
        displayPicture.setVisible(false);
        displayPicture.setManaged(false);
    }

    /**
     * Returns a compact right-aligned bubble for the user's message.
     *
     * @param text User text.
     * @param img Unused user picture; kept so callers stay simple.
     * @return User dialog row.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.hideAvatar();
        dialogBox.dialog.getStyleClass().add("user-label");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        return dialogBox;
    }

    /**
     * Returns a dialog box for Mouse's reply.
     *
     * @param text Reply text.
     * @param img Mouse display picture.
     * @return Mouse dialog row.
     */
    public static DialogBox getMouseDialog(String text, Image img) {
        return getMouseDialog(text, img, false);
    }

    /**
     * Returns a Mouse reply, using an error style when {@code isError} is set.
     *
     * @param text Reply text.
     * @param img Mouse display picture.
     * @param isError Whether to highlight the bubble as an error.
     * @return Mouse dialog row.
     */
    public static DialogBox getMouseDialog(String text, Image img, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        dialogBox.clipAvatar();
        dialogBox.dialog.getStyleClass().add(isError ? "error-label" : "reply-label");
        return dialogBox;
    }
}
