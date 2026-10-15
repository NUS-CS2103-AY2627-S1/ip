package pip.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/** A responsive message row with distinct user, assistant, and error treatments. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 28;
    /** Portion of the available message width used to keep user bubbles visually distinct. */
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.85;
    private static final Image USER_IMAGE = new Image(
            DialogBox.class.getResource("/images/DaUser.jpg").toExternalForm());
    private static final Image PIP_IMAGE = new Image(
            DialogBox.class.getResource("/images/DaPip.png").toExternalForm());
    @FXML private Label dialog;
    @FXML private Label speaker;
    @FXML private VBox message;

    /**
     * Loads and styles a message row. Callers must run on the JavaFX application thread.
     *
     * @param text The message to display.
     * @param user Whether the message belongs to the user rather than Pip.
     * @param error Whether to apply the assistant's error treatment.
     */
    private DialogBox(String text, boolean user, boolean error) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load dialog box", e);
        }
        dialog.setText(text);
        speaker.setText(error ? "Pip · Aw, nuts! Command error" : "Pip");
        speaker.setVisible(!user);
        speaker.setManaged(!user);
        if (user) {
            getChildren().add(createAvatar(USER_IMAGE, "Your profile picture"));
        } else {
            getChildren().add(0, createPipAvatar());
        }
        setAlignment(user ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getStyleClass().add(user ? "user-row" : "assistant-row");
        if (error) {
            message.getStyleClass().add("error-message");
        }
        // Reserve the avatar and gap before sizing the bubble, including at narrow window widths.
        message.maxWidthProperty().bind(user
                ? widthProperty().subtract(AVATAR_SIZE + getSpacing()).multiply(USER_BUBBLE_WIDTH_RATIO)
                : widthProperty().subtract(AVATAR_SIZE + getSpacing()));
        dialog.setMinWidth(0);
        dialog.setMinHeight(USE_PREF_SIZE);
    }

    /**
     * Center-crops a profile image into a compact circle without stretching it.
     * Each row receives its own image view and clip, while sharing the image data.
     *
     * @param image The loaded profile image.
     * @param accessibleText The description exposed to assistive technology.
     * @return A circular avatar sized to {@link #AVATAR_SIZE}.
     */
    private static ImageView createAvatar(Image image, String accessibleText) {
        ImageView picture = new ImageView(image);
        double side = Math.min(image.getWidth(), image.getHeight());
        picture.setViewport(new Rectangle2D((image.getWidth() - side) / 2,
                (image.getHeight() - side) / 2, side, side));
        picture.setFitWidth(AVATAR_SIZE);
        picture.setFitHeight(AVATAR_SIZE);
        picture.setSmooth(true);
        picture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
        picture.setAccessibleText(accessibleText);
        return picture;
    }

    /**
     * Creates Pip's avatar using the same size and circular crop as the user's avatar.
     *
     * @return A fresh image view for a chat row or the window header.
     */
    static ImageView createPipAvatar() {
        return createAvatar(PIP_IMAGE, "Pip's profile picture");
    }

    /**
     * Creates a right-aligned user bubble with a small circular profile picture.
     *
     * @param text The user's command text.
     * @return The message row, ready to add to the conversation on the JavaFX thread.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, true, false);
    }

    /**
     * Creates a wide assistant reply, labeling errors independently of their wording.
     *
     * @param text Pip's response text.
     * @param error Whether the response reports a failed command.
     * @return The message row, ready to add to the conversation on the JavaFX thread.
     */
    public static DialogBox getPipDialog(String text, boolean error) {
        return new DialogBox(text, false, error);
    }
}
