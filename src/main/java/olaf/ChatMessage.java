package olaf;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one chat response together with the profile picture of its speaker.
 */
public final class ChatMessage extends HBox {
    private static final String FXML_PATH = "/view/ChatMessage.fxml";
    private static final double MAXIMUM_MESSAGE_WIDTH_RATIO = 0.72;

    @FXML
    private Label messageLabel;
    @FXML
    private ImageView profilePicture;

    private ChatMessage(String message, Image image) {
        loadView();
        messageLabel.setText(Objects.requireNonNull(message));
        messageLabel.maxWidthProperty().bind(widthProperty().multiply(MAXIMUM_MESSAGE_WIDTH_RATIO));
        profilePicture.setImage(Objects.requireNonNull(image));
    }

    /**
     * Creates a right-aligned message sent by the user.
     *
     * @param message message text
     * @param image user profile picture
     * @return configured user message
     */
    public static ChatMessage createUserMessage(String message, Image image) {
        ChatMessage chatMessage = new ChatMessage(message, image);
        chatMessage.messageLabel.getStyleClass().add("user-message-bubble");
        return chatMessage;
    }

    /**
     * Creates a left-aligned message sent by Olaf.
     *
     * @param message message text
     * @param image Olaf profile picture
     * @return configured Olaf message
     */
    public static ChatMessage createOlafMessage(String message, Image image) {
        ChatMessage chatMessage = new ChatMessage(message, image);
        chatMessage.getChildren().setAll(chatMessage.profilePicture, chatMessage.messageLabel);
        chatMessage.setAlignment(Pos.TOP_LEFT);
        chatMessage.messageLabel.getStyleClass().add("olaf-message-bubble");
        return chatMessage;
    }

    private void loadView() {
        URL resource = ChatMessage.class.getResource(FXML_PATH);
        if (resource == null) {
            throw new IllegalStateException("Required chat-message FXML is missing: " + FXML_PATH);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the chat-message component.", exception);
        }
    }
}
