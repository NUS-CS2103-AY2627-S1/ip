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
 * A single chat row: a wrapping text bubble, and optionally a small circular avatar.
 * User rows sit on the right without a picture; Kiwi and error rows sit on the left.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 32;
    private static final double AVATAR_SPACING = 8;
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.76;
    private static final double REPLY_BUBBLE_WIDTH_RATIO = 0.9;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image, String boxStyle, String bubbleStyle, Pos alignment,
            double widthRatio, boolean hasAvatar) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.getStyleClass().add(bubbleStyle);
        getStyleClass().add(boxStyle);
        setAlignment(alignment);
        setMinWidth(0);

        if (hasAvatar && image != null) {
            applyCircularAvatar(image);
        } else {
            displayPicture.setVisible(false);
            displayPicture.setManaged(false);
        }

        bindBubbleWidth(widthRatio, hasAvatar);
    }

    /**
     * Returns a compact, right-aligned bubble for the user's command.
     * No avatar is shown: the speaker is obvious, and the extra picture would waste width.
     *
     * @param text the user's input.
     * @return a dialog row aligned for the user.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, null, "user-dialog", "user-bubble", Pos.TOP_RIGHT,
                USER_BUBBLE_WIDTH_RATIO, false);
    }

    /**
     * Returns a left-aligned reply row with Kiwi's small circular picture.
     *
     * @param text Kiwi's reply.
     * @param image Kiwi's display picture.
     * @return a dialog row aligned for Kiwi.
     */
    public static DialogBox getKiwiDialog(String text, Image image) {
        return new DialogBox(text, image, "kiwi-dialog", "kiwi-bubble", Pos.TOP_LEFT,
                REPLY_BUBBLE_WIDTH_RATIO, true);
    }

    /**
     * Returns a left-aligned error row, styled to stand out from ordinary replies.
     *
     * @param text the error message.
     * @param image Kiwi's display picture.
     * @return a dialog row styled as an error.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        return new DialogBox(text, image, "error-dialog", "error-bubble", Pos.TOP_LEFT,
                REPLY_BUBBLE_WIDTH_RATIO, true);
    }

    /**
     * Crops {@code image} to a square and clips it to a circle so the small avatar reads cleanly.
     */
    private void applyCircularAvatar(Image image) {
        displayPicture.setImage(image);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            double side = Math.min(image.getWidth(), image.getHeight());
            // DaKiwi.png has the subject on the left; crop from that side.
            double x = 0;
            double y = (image.getHeight() - side) / 2;
            displayPicture.setViewport(new Rectangle2D(x, y, side, side));
            displayPicture.setPreserveRatio(false);
        }
        double radius = AVATAR_SIZE / 2.0;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Caps the bubble width as a fraction of this row so text wraps when the window is resized.
     */
    private void bindBubbleWidth(double widthRatio, boolean hasAvatar) {
        double reserved = hasAvatar ? AVATAR_SIZE + AVATAR_SPACING : 0;
        widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double rowWidth = newWidth.doubleValue();
            if (rowWidth <= 0) {
                return;
            }
            dialog.setMaxWidth(Math.max(0, rowWidth * widthRatio - reserved));
        });
    }
}
