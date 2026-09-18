package epi.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/** Represents one message bubble in the Epi conversation. */
public class DialogBox extends HBox {
    private static final double EPI_AVATAR_SIZE = 36;
    private static final double USER_AVATAR_SIZE = 28;
    private static final double AVATAR_GAP = 10;
    private static final double USER_WIDTH_RATIO = 0.82;

    /**
     * Builds one message row with responsive text wrapping and a speaker-specific avatar.
     *
     * @param message Complete message text, including any line breaks.
     * @param fromUser Whether the message belongs to the user rather than Epi.
     * @param error Whether to apply error styling; callers use this only for Epi replies.
     */
    private DialogBox(String message, boolean fromUser, boolean error) {
        setSpacing(AVATAR_GAP);
        setMinWidth(0);
        setFillHeight(false);
        getStyleClass().add(fromUser ? "user-box" : "epi-box");

        String botLabel = error ? "Epi - needs attention" : "Epi";
        Label speaker = new Label(fromUser ? "You" : botLabel);
        speaker.getStyleClass().add("speaker");
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add("message-text");

        VBox content = new VBox(4, speaker, label);
        content.setMinWidth(0);
        content.setMaxHeight(Region.USE_PREF_SIZE);
        content.getStyleClass().add(fromUser ? "user-message" : "epi-message");
        if (error) {
            content.getStyleClass().add("error-message");
        }
        content.maxWidthProperty().bind(
                Bindings.createDoubleBinding(() -> calculateContentWidth(getWidth(), fromUser), widthProperty()));
        // Bot replies use the available space; short user commands stay compact.
        HBox.setHgrow(content, fromUser ? Priority.NEVER : Priority.ALWAYS);

        ImageView avatar = createAvatar(fromUser);
        if (fromUser) {
            getChildren().addAll(content, avatar);
        } else {
            getChildren().addAll(avatar, content);
        }
    }

    /**
     * Creates a small, centred, rounded-square crop without modifying the source photograph.
     *
     * @param fromUser Whether to load the user's avatar rather than Epi's.
     * @return An image view sized and clipped for the corresponding speaker.
     */
    private ImageView createAvatar(boolean fromUser) {
        double avatarSize = fromUser ? USER_AVATAR_SIZE : EPI_AVATAR_SIZE;
        String imagePath = fromUser
                ? "/epi/gui/images/user.png"
                : "/epi/gui/images/epi.png";
        Image image = new Image(getClass().getResourceAsStream(imagePath),
                avatarSize * 2, avatarSize * 2, true, true);
        ImageView avatar = new ImageView(image);
        avatar.setViewport(calculateAvatarViewport(image.getWidth(), image.getHeight()));
        avatar.setFitWidth(avatarSize);
        avatar.setFitHeight(avatarSize);
        avatar.setPreserveRatio(true);
        avatar.setSmooth(true);
        Rectangle clip = new Rectangle(avatarSize, avatarSize);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        avatar.setClip(clip);
        return avatar;
    }

    /**
     * Reserves space for the avatar and gap, even during the initial zero-width layout pass.
     *
     * @param rowWidth Finite row width in pixels.
     * @param fromUser Whether to reserve the user avatar size and apply the narrower user-message ratio.
     * @return A nonnegative maximum content width in pixels.
     */
    static double calculateContentWidth(double rowWidth, boolean fromUser) {
        double avatarSize = fromUser ? USER_AVATAR_SIZE : EPI_AVATAR_SIZE;
        double availableWidth = Math.max(0, rowWidth - avatarSize - AVATAR_GAP);
        return availableWidth * (fromUser ? USER_WIDTH_RATIO : 1);
    }

    /**
     * Finds the largest centred square inside a portrait, landscape, or square photograph.
     *
     * @param imageWidth Nonnegative image width in pixels.
     * @param imageHeight Nonnegative image height in pixels.
     * @return The square crop in source-image coordinates, centred on both axes.
     * @throws IllegalArgumentException If either dimension is negative.
     */
    static Rectangle2D calculateAvatarViewport(double imageWidth, double imageHeight) {
        double side = Math.min(imageWidth, imageHeight);
        return new Rectangle2D((imageWidth - side) / 2, (imageHeight - side) / 2, side, side);
    }

    /**
     * Creates a compact, right-aligned user message.
     *
     * @param message Complete user input to display.
     * @return A message row containing the user input and avatar.
     */
    public static DialogBox user(String message) {
        return new DialogBox(message, true, false);
    }

    /**
     * Creates a left-aligned Epi response card that can use the full available width.
     *
     * @param message Complete response, including any line breaks.
     * @return A single message row containing the entire response and Epi's avatar.
     */
    public static DialogBox epi(String message) {
        return new DialogBox(message, false, false);
    }

    /**
     * Creates a labelled error card; the wording is supplied by the backend, not used to detect errors.
     *
     * @param message Complete error response, including any line breaks.
     * @return A left-aligned Epi message row with error styling and an attention label.
     */
    public static DialogBox error(String message) {
        return new DialogBox(message, false, true);
    }
}
