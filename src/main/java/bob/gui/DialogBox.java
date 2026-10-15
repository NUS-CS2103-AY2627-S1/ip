package bob.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays user commands and application responses using distinct visual styles.
 */
public final class DialogBox extends HBox {
    private static final double USER_MESSAGE_MAX_WIDTH = 480.0;
    private static final double RESPONSE_MESSAGE_MAX_WIDTH = 640.0;

    /**
     * Defines the supported conversation message variants.
     */
    private enum MessageType {
        USER,
        BOB,
        ERROR
    }

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private VBox messageContainer;
    @FXML
    private Label senderLabel;

    /**
     * Constructs and configures a dialog box for the specified message variant.
     *
     * @param text        the message text
     * @param image       the response avatar, or null for user messages
     * @param messageType the visual variant to apply
     */
    private DialogBox(String text, Image image, MessageType messageType) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DialogBox FXML layout", e);
        }

        dialog.setText(text);
        configurePresentation(text, image, messageType);
    }

    /**
     * Creates a user dialog box.
     *
     * @param text the user message
     * @return a DialogBox representing user input
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, null, MessageType.USER);
    }

    /**
     * Creates a Bob response dialog box.
     *
     * @param text  the Bob response message
     * @param image the Bob avatar image
     * @return a DialogBox representing Bob's response
     */
    public static DialogBox getBobDialog(String text, Image image) {
        return new DialogBox(text, image, MessageType.BOB);
    }

    /**
     * Creates an error response dialog box.
     *
     * @param text  the error response message
     * @param image the Bob avatar image
     * @return a DialogBox representing an error response
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        return new DialogBox(text, image, MessageType.ERROR);
    }

    /**
     * Applies the alignment, sizing, and accessibility settings for a message.
     *
     * @param text        the message text
     * @param image       the response avatar, or null for user messages
     * @param messageType the visual variant to apply
     */
    private void configurePresentation(String text, Image image, MessageType messageType) {
        switch (messageType) {
            case USER:
                setAlignment(Pos.TOP_RIGHT);
                getStyleClass().add("user-message");
                messageContainer.setMaxWidth(USER_MESSAGE_MAX_WIDTH);
                senderLabel.setManaged(false);
                senderLabel.setVisible(false);
                displayPicture.setManaged(false);
                displayPicture.setVisible(false);
                setAccessibleText("You: " + text);
                break;
            case BOB:
                configureResponse(image, "BOB | FOREMAN", "bot-message", "Bob: " + text);
                break;
            case ERROR:
                configureResponse(image, "BOB | HOLD UP", "error-message", "Bob warning: " + text);
                break;
            default:
                throw new AssertionError("Unhandled message type: " + messageType);
        }
    }

    /**
     * Applies the common presentation shared by normal and error responses.
     *
     * @param image          the Bob avatar image
     * @param sender         the visible sender or status label
     * @param styleClass     the CSS class for the response variant
     * @param accessibleText the text exposed to assistive technologies
     */
    private void configureResponse(Image image, String sender, String styleClass, String accessibleText) {
        assert image != null : "Response avatar image cannot be null";
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add(styleClass);
        messageContainer.setMaxWidth(RESPONSE_MESSAGE_MAX_WIDTH);
        senderLabel.setText(sender);
        displayPicture.setImage(image);
        setAccessibleText(accessibleText);
    }
}
