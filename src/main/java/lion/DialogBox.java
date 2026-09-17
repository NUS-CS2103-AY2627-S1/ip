package lion;

import java.io.IOException;

import javafx.beans.binding.Bindings;
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
 * Represents one chat bubble: a text label paired with a small avatar.
 *
 * <p>The bot's avatar is its display picture, cropped to a circle. User
 * messages are kept as right-aligned bubbles without an extra avatar.
 */
public class DialogBox extends HBox {

    private static final double AVATAR_SIZE = 36.0;

    private static final double AVATAR_CROP_INSET_FRACTION = 0.02;

    private static final double AVATAR_CROP_UPWARD_SHIFT_FRACTION = 0.04;

    /** Fraction of this bubble's available width a message may use before wrapping. */
    private static final double MAX_WIDTH_FRACTION = 0.72;

    /** Absolute cap on bubble width, so it does not grow unreasonably wide on large windows. */
    private static final double MAX_WIDTH_CAP = 480.0;

    @FXML
    private Label dialog;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bound (rather than a fixed FXML value) so the bubble stays a sensible
        // size as the window is resized, instead of clipping on a narrow window
        // or leaving awkward empty space on a wide one.
        dialog.maxWidthProperty().bind(
                Bindings.min(widthProperty().multiply(MAX_WIDTH_FRACTION), MAX_WIDTH_CAP));

        dialog.getStyleClass().setAll("dialog-label", "user-bubble");
        dialog.setText(text);
    }

    /**
     * Switches this dialog box to the bot's left-aligned, gray bubble style
     * and prepends a small circular avatar.
     *
     * @param avatar image to crop into a circular avatar.
     */
    private void useBotStyle(Image avatar) {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().setAll("dialog-label", "bot-bubble");

        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(AVATAR_SIZE);
        avatarView.setFitHeight(AVATAR_SIZE);
        avatarView.setPreserveRatio(false);
        avatarView.setViewport(getCenteredAvatarCrop(avatar));
        avatarView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        getChildren().add(0, avatarView);
    }

    /**
     * Returns a centered square crop that removes part of the image padding.
     *
     * @param avatar image to crop.
     * @return centered square viewport for the avatar image.
     */
    private Rectangle2D getCenteredAvatarCrop(Image avatar) {
        double size = Math.min(avatar.getWidth(), avatar.getHeight());
        double cropInset = size * AVATAR_CROP_INSET_FRACTION;
        double cropSize = size - cropInset * 2;
        double cropX = (avatar.getWidth() - cropSize) / 2;
        double cropY = Math.max(0, (avatar.getHeight() - cropSize) / 2
                - size * AVATAR_CROP_UPWARD_SHIFT_FRACTION);

        return new Rectangle2D(cropX, cropY, cropSize, cropSize);
    }

    /**
     * Creates a dialog box showing the user's message as a right-aligned bubble.
     *
     * @param text message text to display.
     * @return dialog box in the user's default (unflipped) layout.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text);
    }

    /**
     * Creates a dialog box showing Lion's reply, flipped to the left with a
     * small avatar, styled as an error or task-list bubble where applicable.
     *
     * @param text message text to display.
     * @param avatar Lion's display picture.
     * @param isError true if this reply should be highlighted as an error.
     * @param isTaskList true if this reply is a numbered task list, which is given
     *     a more compact, scan-friendly style than an ordinary conversational reply.
     * @return dialog box flipped so its avatar and text sit on the left.
     */
    public static DialogBox getLionDialog(String text, Image avatar, boolean isError, boolean isTaskList) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.useBotStyle(avatar);

        if (isError) {
            dialogBox.dialog.getStyleClass().add("error-bubble");
        }
        if (isTaskList) {
            dialogBox.dialog.getStyleClass().add("task-list-bubble");
        }

        return dialogBox;
    }
}
