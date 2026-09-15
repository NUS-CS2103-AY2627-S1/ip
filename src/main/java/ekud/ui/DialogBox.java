package ekud.ui;

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
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

/**
 * A chat row: a text bubble, and for Ekud replies a small circular avatar.
 * User rows omit the avatar so the conversation does not look like two people
 * chatting in the same format.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 40;
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.76;
    private static final double AVATAR_RESERVED_WIDTH = 56;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the dialog box.", e);
        }

        dialog.setText(text);
        dialog.setMinHeight(Region.USE_PREF_SIZE);
        if (img == null) {
            hideAvatar();
        } else {
            displayPicture.setFitWidth(AVATAR_SIZE);
            displayPicture.setFitHeight(AVATAR_SIZE);
            displayPicture.setImage(img);
            clipAvatarToCircle();
        }
    }

    /**
     * Hides the avatar and drops it from layout so the bubble can use the space.
     */
    private void hideAvatar() {
        displayPicture.setVisible(false);
        displayPicture.setManaged(false);
    }

    /**
     * Crops the avatar to a circle so the square source image blends into the row.
     */
    private void clipAvatarToCircle() {
        double radius = AVATAR_SIZE / 2.0;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);
    }

    /**
     * Caps bubble width as a fraction of the parent so long replies wrap when
     * the window is resized, instead of stretching into unreadably long lines.
     *
     * @param hasAvatar whether this row still shows the circular avatar
     */
    private void bindBubbleMaxWidth(boolean hasAvatar) {
        parentProperty().addListener((_, _, newParent) -> {
            if (!(newParent instanceof Region parent)) {
                return;
            }
            dialog.maxWidthProperty().unbind();
            if (hasAvatar) {
                dialog.maxWidthProperty().bind(parent.widthProperty().subtract(AVATAR_RESERVED_WIDTH));
            } else {
                dialog.maxWidthProperty().bind(parent.widthProperty().multiply(USER_BUBBLE_WIDTH_RATIO));
            }
        });
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on
     * the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Tints the reply bubble based on the command that produced it.
     *
     * @param dialogStyle style chosen from {@link ekud.Ekud#getDialogStyle}
     */
    private void changeDialogStyle(DialogStyle dialogStyle) {
        if (dialogStyle == null || dialogStyle == DialogStyle.NONE) {
            return;
        }
        dialog.getStyleClass().add(dialogStyle.getCssClass());
    }

    /**
     * Returns a dialog box for a message sent by the user.
     * User rows have no avatar and use a compact right-aligned bubble.
     *
     * @param text message text
     * @return a right-aligned user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, null);
        dialogBox.dialog.getStyleClass().add("user-label");
        dialogBox.bindBubbleMaxWidth(false);
        return dialogBox;
    }

    /**
     * Returns a dialog box for a reply from Ekud.
     *
     * @param text        reply text
     * @param img         Ekud avatar
     * @param dialogStyle style used to tint the bubble
     * @return a flipped dialog box
     */
    public static DialogBox getEkudDialog(String text, Image img, DialogStyle dialogStyle) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        dialogBox.changeDialogStyle(dialogStyle);
        dialogBox.bindBubbleMaxWidth(true);
        return dialogBox;
    }
}
