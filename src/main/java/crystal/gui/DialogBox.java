package crystal.gui;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Represents one messaging-app row containing a speaker, message, and circular avatar.
 */
public final class DialogBox extends HBox {
    /** Diameter of the black circular avatar frame. */
    private static final double AVATAR_FRAME_SIZE = 54;

    /** Diameter of the visible avatar image. */
    private static final double AVATAR_IMAGE_SIZE = 48;

    /** Maximum width of a text bubble before its contents wrap. */
    private static final double MESSAGE_MAX_WIDTH = 350;

    /** Text shown above messages sent by Crystal. */
    private static final String CRYSTAL_NAME = "Crystal";

    /** Text shown above messages sent by the user. */
    private static final String USER_NAME = "You";

    /** Message content column, retained so user dialogs can reverse their visual order. */
    private final VBox messageColumn;

    /** Framed circular avatar, retained so user dialogs can reverse their visual order. */
    private final StackPane avatarFrame;

    /** Label containing the body of the message. */
    private final Label messageLabel;

    /**
     * Creates a left-aligned dialog row before speaker-specific styling is applied.
     *
     * @param message message body.
     * @param speakerName name displayed above the message.
     * @param avatarImage image representing the speaker.
     */
    private DialogBox(String message, String speakerName, Image avatarImage) {
        Label speakerLabel = new Label(speakerName);
        speakerLabel.getStyleClass().add("speaker-name");

        messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(MESSAGE_MAX_WIDTH);
        messageLabel.getStyleClass().add("message-bubble");

        messageColumn = new VBox(4, speakerLabel, messageLabel);
        avatarFrame = createAvatarFrame(avatarImage, speakerName);

        setAlignment(Pos.TOP_LEFT);
        setSpacing(10);
        getStyleClass().add("dialog-row");
        getChildren().addAll(avatarFrame, messageColumn);
    }

    /**
     * Creates a right-aligned message sent by the user.
     *
     * @param message message body.
     * @param avatarImage user's avatar image.
     * @return styled user dialog.
     */
    public static DialogBox createUserDialog(String message, Image avatarImage) {
        DialogBox dialogBox = new DialogBox(message, USER_NAME, avatarImage);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.messageLabel.getStyleClass().add("user-bubble");
        dialogBox.messageColumn.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getChildren().setAll(dialogBox.messageColumn, dialogBox.avatarFrame);
        return dialogBox;
    }

    /**
     * Creates a left-aligned message sent by Crystal.
     *
     * @param message message body.
     * @param avatarImage Crystal's avatar image.
     * @return styled Crystal dialog.
     */
    public static DialogBox createCrystalDialog(String message, Image avatarImage) {
        DialogBox dialogBox = new DialogBox(message, CRYSTAL_NAME, avatarImage);
        dialogBox.getStyleClass().add("crystal-dialog");
        dialogBox.messageLabel.getStyleClass().add("crystal-bubble");
        return dialogBox;
    }

    /**
     * Creates a visually prominent left-aligned error message from Crystal.
     *
     * @param message error message body.
     * @param avatarImage Crystal's avatar image.
     * @return styled Crystal error dialog.
     */
    public static DialogBox createCrystalErrorDialog(String message, Image avatarImage) {
        DialogBox dialogBox = createCrystalDialog(message, avatarImage);
        dialogBox.messageLabel.getStyleClass().add("error-bubble");
        return dialogBox;
    }

    /**
     * Builds a black-bordered circular frame containing a square crop of the avatar.
     * The crop starts at the image's top edge so footer content is excluded.
     *
     * @param image source avatar image.
     * @param speakerName accessible description of the image.
     * @return circular avatar frame.
     */
    private static StackPane createAvatarFrame(Image image, String speakerName) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropLeft = (image.getWidth() - cropSize) / 2;

        ImageView avatar = new ImageView(image);
        avatar.setAccessibleText(speakerName + " avatar");
        avatar.setFitWidth(AVATAR_IMAGE_SIZE);
        avatar.setFitHeight(AVATAR_IMAGE_SIZE);
        avatar.setViewport(new Rectangle2D(cropLeft, 0, cropSize, cropSize));
        avatar.setClip(new Circle(
                AVATAR_IMAGE_SIZE / 2, AVATAR_IMAGE_SIZE / 2, AVATAR_IMAGE_SIZE / 2));

        StackPane frame = new StackPane(avatar);
        frame.setMinSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        frame.setPrefSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        frame.setMaxSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        frame.getStyleClass().add("avatar-frame");
        return frame;
    }
}
