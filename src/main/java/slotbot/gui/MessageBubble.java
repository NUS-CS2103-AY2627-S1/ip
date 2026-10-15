package slotbot.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays one user or SlotBot message in an aligned, wrapping chat bubble.
 */
public class MessageBubble extends HBox {
    private static final double MAXIMUM_CONTENT_WIDTH_RATIO = 0.82;

    /**
     * Creates a message bubble with styling and alignment for its sender.
     *
     * @param text Message to display.
     * @param isUser Whether the user sent the message.
     * @param isError Whether the message reports a command error.
     */
    public MessageBubble(String text, boolean isUser, boolean isError) {
        assert !isUser || !isError : "A user message cannot be an error response";

        Label author = createAuthorLabel(isUser, isError);
        Label message = createMessageLabel(text, isUser, isError);
        VBox content = new VBox(5, author, message);

        content.setFillWidth(true);
        content.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        content.getStyleClass().add("message-content");
        content.maxWidthProperty().bind(widthProperty().multiply(MAXIMUM_CONTENT_WIDTH_RATIO));

        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().addAll("message-row", isUser ? "user-row" : "bot-row");
        getChildren().add(content);
    }

    /**
     * Creates the label that identifies a message's sender.
     */
    private Label createAuthorLabel(boolean isUser, boolean isError) {
        Label author = new Label(isUser ? "YOU" : "SLOTBOT");
        author.getStyleClass().addAll("author", isUser ? "user-author" : "bot-author");
        if (isError) {
            author.getStyleClass().add("error-author");
        }
        return author;
    }

    /**
     * Creates a wrapping label styled for a user or SlotBot message.
     */
    private Label createMessageLabel(String text, boolean isUser, boolean isError) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMinWidth(0);
        message.setMaxWidth(Double.MAX_VALUE);
        message.getStyleClass().addAll("message-text", isUser ? "user-message" : "bot-message");
        if (isError) {
            message.getStyleClass().add("error-message");
        }
        return message;
    }
}
