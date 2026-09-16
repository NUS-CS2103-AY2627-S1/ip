package aider.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/** Represents one user or Aider message in the conversation. */
public final class DialogBox extends HBox {
    /** Creates a conversation bubble. */
    public DialogBox(String message, boolean isUser) {
        this(message, isUser, false);
    }

    /**
     * Creates a conversation bubble, optionally styled as an error.
     *
     * @param message the text to display
     * @param isUser whether the message was sent by the user
     * @param isError whether the message describes an error
     */
    public DialogBox(String message, boolean isUser, boolean isError) {
        Label text = new Label(message);
        text.setWrapText(true);
        text.setMaxWidth(560);
        text.setPadding(new Insets(10));
        text.getStyleClass().add("message-label");
        HBox.setHgrow(text, Priority.NEVER);
        getChildren().add(text);
        getStyleClass().add(isError ? "error-dialog" : isUser ? "user-dialog" : "aider-dialog");
        setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(4, 8, 4, 8));
    }
}
