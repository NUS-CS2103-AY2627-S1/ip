package probe.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Represents one message in the JavaFX chatbot conversation.
 */
public class DialogBox extends HBox {
    /**
     * Creates a message bubble for the specified speaker.
     *
     * @param speaker Name of the message sender.
     * @param message Message text.
     * @param fromUser Whether the message was sent by the user.
     */
    public DialogBox(String speaker, String message, boolean fromUser) {
        this(speaker, message, fromUser, false);
    }

    /**
     * Creates a message bubble with optional error styling.
     *
     * @param speaker Name of the message sender.
     * @param message Message text.
     * @param fromUser Whether the message was sent by the user.
     * @param isError Whether the message represents an error.
     */
    public DialogBox(String speaker, String message, boolean fromUser, boolean isError) {
        Label speakerLabel = new Label(speaker);
        Label messageLabel = new Label(message);
        speakerLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;"
                + " -fx-text-fill: #64748b;");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #172033;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(440);
        VBox content = new VBox(4, speakerLabel, messageLabel);
        content.setPadding(new Insets(10, 14, 10, 14));
        content.setStyle(isError
                ? "-fx-background-color: #fee2e2; -fx-border-color: #f87171;"
                + " -fx-border-radius: 10; -fx-background-radius: 10;"
                : fromUser
                ? "-fx-background-color: #dbeafe; -fx-background-radius: 10;"
                : "-fx-background-color: #ffffff; -fx-border-color: #e2e8f0;"
                + " -fx-border-radius: 10; -fx-background-radius: 10;");
        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(2, 4, 2, 4));
        setAlignment(isError ? Pos.CENTER_LEFT
                : fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        getChildren().add(content);
    }
}
