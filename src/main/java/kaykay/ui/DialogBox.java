package kaykay.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one message in Kaykay's conversation area.
 *
 * <p>Kaykay responses use a small mascot for visual identity, while user
 * messages rely on their compact right-aligned presentation.</p>
 */
public final class DialogBox extends HBox {
    private static final String KAYKAY_AVATAR_PATH = "/images/kaykay-avatar.png";
    private static final String SUCCESS_PREFIX = "\u2713  ";
    private static final String WARNING_PREFIX = "\u26A0  ";
    private static final double USER_MESSAGE_MAX_WIDTH_RATIO = 0.72;
    private static final double KAYKAY_MESSAGE_MAX_WIDTH_RATIO = 0.82;
    private static final Image KAYKAY_AVATAR = new Image(Objects.requireNonNull(
            DialogBox.class.getResource(KAYKAY_AVATAR_PATH)).toExternalForm());

    /** Text displayed in the dialog box. */
    @FXML
    private Label text;

    /** Avatar displayed beside the dialog text. */
    @FXML
    private ImageView avatar;

    /**
     * Creates a dialog box for a message from Kaykay or the user.
     *
     * @param message message to display.
     * @param isUserMessage whether the message came from the user.
     */
    public DialogBox(String message, boolean isUserMessage) {
        this(message, isUserMessage, false, false);
    }

    /** Creates a dialog box with optional success or error presentation. */
    private DialogBox(String message, boolean isUserMessage, boolean isSuccessMessage, boolean isErrorMessage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a Kaykay dialog box.", exception);
        }

        String messagePrefix = "";
        if (isSuccessMessage) {
            messagePrefix = SUCCESS_PREFIX;
        } else if (isErrorMessage) {
            messagePrefix = WARNING_PREFIX;
        }
        text.setText(messagePrefix + message);
        text.setWrapText(true);
        String dialogStyleClass = "kaykay-dialog";
        if (isUserMessage) {
            dialogStyleClass = "user-dialog";
        } else if (isSuccessMessage) {
            dialogStyleClass = "success-dialog";
        } else if (isErrorMessage) {
            dialogStyleClass = "error-dialog";
        }
        text.getStyleClass().add(dialogStyleClass);
        double messageMaxWidthRatio = isUserMessage
                ? USER_MESSAGE_MAX_WIDTH_RATIO
                : KAYKAY_MESSAGE_MAX_WIDTH_RATIO;
        text.maxWidthProperty().bind(widthProperty().multiply(messageMaxWidthRatio));
        setAlignment(isUserMessage ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setSpacing(8.0);

        if (isUserMessage) {
            getChildren().setAll(text);
        } else {
            avatar.setImage(KAYKAY_AVATAR);
            avatar.setAccessibleText("Kaykay");
            getChildren().setAll(avatar, text);
        }
    }

    /**
     * Creates a dialog box aligned as a user message.
     *
     * @param message message to display.
     * @return a user-aligned dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message, true);
    }

    /**
     * Creates a dialog box aligned as a Kaykay message.
     *
     * @param message message to display.
     * @return a Kaykay-aligned dialog box.
     */
    public static DialogBox getKaykayDialog(String message) {
        return new DialogBox(message, false);
    }

    /**
     * Creates a dialog box that confirms a successful action by Kaykay.
     *
     * @param message success message to display.
     * @return a Kaykay-aligned success dialog box.
     */
    public static DialogBox getKaykaySuccessDialog(String message) {
        return new DialogBox(message, false, true, false);
    }

    /**
     * Creates a dialog box that highlights an error reported by Kaykay.
     *
     * @param message error message to display.
     * @return a Kaykay-aligned error dialog box.
     */
    public static DialogBox getKaykayErrorDialog(String message) {
        return new DialogBox(message, false, false, true);
    }
}
