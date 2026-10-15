package charlie.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Displays one wrapped message with a compact avatar identifying its sender.
 */
public class DialogBox extends HBox {
    /** Size of each square profile picture in pixels. */
    private static final double AVATAR_SIZE = 54;

    /** Width of an avatar frame, including its border. */
    private static final double AVATAR_FRAME_SIZE = 60;

    /** Maximum proportion of the dialog row occupied by its message. */
    private static final double MESSAGE_WIDTH_RATIO = 0.76;

    /** Picture displayed beside Charlie's messages. */
    private static final Image CHARLIE_AVATAR = loadImage("/images/charlie-avatar.jpg");

    /** Picture displayed beside the user's messages. */
    private static final Image USER_AVATAR = loadImage("/images/user-avatar.jpg");

    /**
     * Creates a responsive message bubble with the requested alignment and avatar.
     *
     * @param text Message to display.
     * @param alignment Side on which to place the message.
     * @param avatarImage Picture identifying the sender.
     * @param isUser Whether the sender is the user.
     */
    private DialogBox(String text, Pos alignment, Image avatarImage, boolean isUser) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMinWidth(0);
        message.maxWidthProperty().bind(widthProperty().multiply(MESSAGE_WIDTH_RATIO));
        message.getStyleClass().addAll("dialog-text", isUser ? "user-dialog" : "charlie-dialog");

        StackPane avatar = createAvatar(avatarImage);

        setAlignment(alignment);
        setSpacing(8);
        setPadding(new Insets(3, 0, 3, 0));
        if (isUser) {
            getChildren().addAll(message, avatar);
        } else {
            getChildren().addAll(avatar, message);
        }
    }

    /**
     * Returns a right-aligned dialog for text entered by the user.
     *
     * @param text User's message.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, Pos.TOP_RIGHT, USER_AVATAR, true);
    }

    /**
     * Returns a left-aligned dialog for Charlie's response.
     *
     * @param text Charlie's message.
     * @return Charlie dialog box.
     */
    public static DialogBox getCharlieDialog(String text) {
        return new DialogBox(text, Pos.TOP_LEFT, CHARLIE_AVATAR, false);
    }

    /**
     * Creates a circular view of the center of the supplied image.
     *
     * @param image Image to crop and display.
     * @return Framed profile picture.
     */
    private static StackPane createAvatar(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = (image.getHeight() - cropSize) / 2;

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(AVATAR_SIZE);
        imageView.setFitHeight(AVATAR_SIZE);
        imageView.setSmooth(true);
        imageView.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        imageView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        StackPane avatarFrame = new StackPane(imageView);
        avatarFrame.setMinSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setPrefSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setMaxSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.getStyleClass().add("avatar-frame");
        return avatarFrame;
    }

    /**
     * Loads an image bundled with the application.
     *
     * @param resourcePath Classpath location of the image.
     * @return Loaded image.
     */
    private static Image loadImage(String resourcePath) {
        InputStream resourceStream = Objects.requireNonNull(
                DialogBox.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath);
        try (resourceStream) {
            return new Image(resourceStream);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load image resource: " + resourcePath, e);
        }
    }
}
