package alice;

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
 * A single message in the conversation.
 *
 * <p>The two sides are deliberately not mirror images of each other: the
 * user's messages are right-aligned accent-coloured bubbles with no
 * picture, while Alice's are left-aligned with a small avatar. Because the
 * conversation only ever has these two participants, the side and colour
 * are enough to tell them apart, and dropping the user's picture gives the
 * text noticeably more room in a narrow window.
 */
public class DialogBox extends HBox {
    /** Share of the window width a bubble may occupy before it wraps. */
    private static final double MAX_WIDTH_FRACTION = 0.78;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // The FXML file is bundled as a resource, so this should only
            // happen if it is accidentally deleted or renamed.
            throw new IllegalStateException("Could not load DialogBox.fxml", e);
        }

        dialog.setText(text);
        // Capping the bubble at a fraction of the window keeps long replies
        // readable instead of letting them stretch the full width, and
        // because it is a binding it re-wraps as the window is resized.
        dialog.maxWidthProperty().bind(widthProperty().multiply(MAX_WIDTH_FRACTION));
    }

    /**
     * Creates a dialog box for a message the user typed.
     *
     * @param text the user's message.
     * @return a dialog box styled for the user (right-aligned, no picture).
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.hideDisplayPicture();
        dialogBox.dialog.getStyleClass().add("bubble-user");
        return dialogBox;
    }

    /**
     * Creates a dialog box for one of Alice's replies.
     *
     * @param text Alice's reply.
     * @param img Alice's display picture.
     * @return a dialog box styled for Alice (left-aligned, with avatar).
     */
    public static DialogBox getAliceDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.displayPicture.setImage(img);
        dialogBox.dialog.getStyleClass().add("bubble-alice");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a dialog box for a reply that reports a failure, styled so it
     * stands out from Alice's ordinary replies.
     *
     * @param text the error message.
     * @param img Alice's display picture.
     * @return a dialog box styled to signal an error.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.displayPicture.setImage(img);
        dialogBox.dialog.getStyleClass().add("bubble-error");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Removes the display picture from the layout entirely, so it takes up
     * no space rather than leaving a 26px gap where a picture would be.
     */
    private void hideDisplayPicture() {
        displayPicture.setVisible(false);
        displayPicture.setManaged(false);
    }

    /**
     * Flips this dialog box so the display picture is on the left and the
     * text is on the right, instead of the default (user) layout.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
        this.setAlignment(Pos.TOP_LEFT);
    }
}
