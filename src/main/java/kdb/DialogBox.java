package kdb;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

/** Displays one chatbot or user message with an optional circular avatar. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 40.0;

    /** Creates a message box without error styling. */
    public DialogBox(String message, Image image, boolean isUser) {
        this(message, image, isUser, false, false);
    }

    /** Creates a styled message box. */
    public DialogBox(String message, Image image, boolean isUser, boolean isError) {
        this(message, image, isUser, isError, false);
    }

    /** Creates a styled message box with optional monospaced ASCII art. */
    public DialogBox(String message, Image image, boolean isUser, boolean isError,
            boolean isAsciiArt) {
        Label text = new Label(message);
        text.setWrapText(true);
        text.setMaxWidth(isUser ? 450.0 : Double.MAX_VALUE);
        text.setMinHeight(Region.USE_PREF_SIZE);
        text.setPadding(new Insets(10.0));
        if (isError) {
            text.getStyleClass().add("error-message");
        } else if (isUser) {
            text.getStyleClass().add("user-message");
        } else {
            text.getStyleClass().add("bot-message");
        }
        if (isAsciiArt) {
            text.getStyleClass().add("ascii-message");
        }

        if (!isUser) {
            text.setPrefWidth(560.0);
            text.setMaxWidth(560.0);
        }

        ImageView avatar = createAvatar(image);
        setSpacing(8.0);
        setPadding(new Insets(5.0));
        setMaxWidth(Double.MAX_VALUE);

        if (isUser) {
            setAlignment(Pos.CENTER_RIGHT);
            getChildren().add(text);
            if (avatar != null) {
                getChildren().add(avatar);
            }
        } else {
            setAlignment(Pos.CENTER_LEFT);
            if (avatar != null) {
                getChildren().add(avatar);
            }
            getChildren().add(text);
        }
    }

    /** Creates a circular image view for a message avatar. */
    private ImageView createAvatar(Image image) {
        if (image == null) {
            return null;
        }
        ImageView avatar = new ImageView(image);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);

        double scale = Math.max(AVATAR_SIZE / image.getWidth(), AVATAR_SIZE / image.getHeight());
        double viewportWidth = AVATAR_SIZE / scale;
        double viewportHeight = AVATAR_SIZE / scale;
        double viewportX = (image.getWidth() - viewportWidth) / 2;
        double viewportY = (image.getHeight() - viewportHeight) / 2;
        avatar.setViewport(new Rectangle2D(viewportX, viewportY, viewportWidth, viewportHeight));
        avatar.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        return avatar;
    }
}
