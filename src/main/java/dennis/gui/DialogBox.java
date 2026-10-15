package dennis.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One chat bubble: an {@link HBox} holding a message label and, for Dennis's
 * replies, a circular avatar.
 *
 * <p>The layout comes from {@code DialogBox.fxml}, loaded with the
 * {@code fx:root} technique so this object is both the controller and the
 * root node. User bubbles keep the FXML's right alignment and no avatar;
 * Dennis's bubbles are {@link #alignLeft() moved} to the left and
 * {@link #showAvatar() show} his picture. An error reply is
 * {@link #markAsError() marked} with a distinct style so it stands out from a
 * normal message.</p>
 */
public class DialogBox extends HBox {
    /** Dennis's avatar picture, loaded once and shared by every bubble. */
    private static final Image DENNIS_AVATAR =
            new Image(DialogBox.class.getResourceAsStream("/images/dennis.jpg"));

    /** Radius of the circular avatar, in pixels. */
    private static final double AVATAR_RADIUS = 18.0;

    /** Style class applied to a bubble that is one of Dennis's replies. */
    private static final String DENNIS_STYLE_CLASS = "dennis-label";

    /** Style class applied to a bubble whose message is an error. */
    private static final String ERROR_STYLE_CLASS = "error-label";

    /** Shows this bubble's message text, wrapping onto multiple lines. */
    @FXML
    private Label dialog;

    /** Shows Dennis's avatar; hidden for the user's own bubbles. */
    @FXML
    private ImageView displayPicture;

    /**
     * Builds a bubble showing the given text, with no avatar until
     * {@link #showAvatar()} is called.
     *
     * @param text the message to display
     */
    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load /view/DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setVisible(false);
        displayPicture.setManaged(false);
    }

    /** Moves this bubble to the left edge, marking it as one of Dennis's replies. */
    private void alignLeft() {
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Shows Dennis's avatar, cropped to a circle. The source picture need not
     * be square: the crop takes the largest centered square that fits inside
     * it, so the same logic works whichever side is longer.
     */
    private void showAvatar() {
        double side = Math.min(DENNIS_AVATAR.getWidth(), DENNIS_AVATAR.getHeight());
        double x = (DENNIS_AVATAR.getWidth() - side) / 2;
        double y = (DENNIS_AVATAR.getHeight() - side) / 2;

        displayPicture.setImage(DENNIS_AVATAR);
        displayPicture.setViewport(new Rectangle2D(x, y, side, side));
        displayPicture.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
        displayPicture.setVisible(true);
        displayPicture.setManaged(true);
    }

    /** Marks this bubble's message as an error, so it renders in the error style. */
    private void markAsError() {
        dialog.getStyleClass().add(ERROR_STYLE_CLASS);
    }

    /** Marks this bubble as one of Dennis's replies, so it renders in Dennis's style. */
    private void markAsDennis() {
        dialog.getStyleClass().add(DENNIS_STYLE_CLASS);
    }

    /**
     * Creates a right-aligned bubble for something the user typed.
     *
     * @param text the user's message
     * @return the bubble
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text);
    }

    /**
     * Creates a left-aligned bubble for one of Dennis's replies, with his
     * avatar shown alongside it.
     *
     * @param text    Dennis's message
     * @param isError whether the message reports an error, which is shown in
     *                a visually distinct style
     * @return the bubble
     */
    public static DialogBox getDennisDialog(String text, boolean isError) {
        DialogBox box = new DialogBox(text);
        box.alignLeft();
        box.showAvatar();
        box.markAsDennis();
        if (isError) {
            box.markAsError();
        }
        return box;
    }
}
