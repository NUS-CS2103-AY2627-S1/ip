package nori.ui;

import java.net.URL;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Displays Nori's photographic penguin avatar.
 */
public final class BotAvatar extends StackPane {
    private static final String IMAGE_PATH = "/images/pingu-background.png";
    private static final Rectangle2D FACE_VIEWPORT = new Rectangle2D(190, 285, 430, 430);
    private static final Image BOT_IMAGE = loadBotImage();

    /**
     * Creates a circular Nori avatar at the requested size.
     *
     * @param size the width and height of the avatar in pixels.
     */
    public BotAvatar(double size) {
        ImageView portrait = new ImageView(BOT_IMAGE);
        portrait.setFitWidth(size);
        portrait.setFitHeight(size);
        portrait.setViewport(FACE_VIEWPORT);
        portrait.setClip(new Circle(size / 2, size / 2, size / 2));

        Circle border = new Circle(size / 2);
        border.getStyleClass().add("bot-avatar-border");

        getChildren().addAll(portrait, border);
        setMinSize(size, size);
        setPrefSize(size, size);
        setMaxSize(size, size);
        setAccessibleText("Nori penguin avatar");
        setMouseTransparent(true);
    }

    /**
     * Returns Nori's face alone, cropped square for use as the window and taskbar icon.
     *
     * The window icon is cut from the same portrait as the in-conversation
     * avatar, so the penguin a user sees in their task bar is the one they
     * are about to talk to.
     *
     * @return the square portrait crop.
     */
    public static Image createWindowIcon() {
        return new WritableImage(BOT_IMAGE.getPixelReader(),
                (int) FACE_VIEWPORT.getMinX(), (int) FACE_VIEWPORT.getMinY(),
                (int) FACE_VIEWPORT.getWidth(), (int) FACE_VIEWPORT.getHeight());
    }

    /** Loads the bundled portrait and reports a clear error if it cannot be read. */
    private static Image loadBotImage() {
        URL imageUrl = BotAvatar.class.getResource(IMAGE_PATH);
        if (imageUrl == null) {
            throw new IllegalStateException("Unable to find Nori's avatar image: " + IMAGE_PATH);
        }

        Image portrait = new Image(imageUrl.toExternalForm());
        if (portrait.isError()) {
            throw new IllegalStateException("Unable to read Nori's avatar image: " + IMAGE_PATH,
                    portrait.getException());
        }
        return portrait;
    }
}
