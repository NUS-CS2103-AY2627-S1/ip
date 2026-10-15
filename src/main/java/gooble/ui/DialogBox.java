package gooble.ui;

import java.util.Objects;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Displays one compact message in the Gooble conversation. */
public class DialogBox extends HBox {
    /** Creates a message styled as either a user or Gooble response. */
    public DialogBox(String message, boolean isUserMessage) {
        this(message, isUserMessage, false);
    }

    /** Creates a message with optional error styling. */
    public DialogBox(String message, boolean isUserMessage, boolean isError) {
        this(message, isUserMessage, isError, false);
    }

    /** Creates a message with optional error or help styling. */
    public DialogBox(String message, boolean isUserMessage, boolean isError, boolean isHelp) {
        this(message, isUserMessage, isError, isHelp, false);
    }

    /** Creates a message with optional error, help, or invalid-command image styling. */
    public DialogBox(String message, boolean isUserMessage, boolean isError, boolean isHelp,
            boolean showInvalidCommandImage) {
        String displayMessage = isUserMessage ? message
                : isError ? "⚠ " + message : isHelp ? message : "Gooble: " + message;
        Label text = new Label(displayMessage);
        text.setWrapText(true);
        text.setMaxWidth(680);
        text.getStyleClass().add("chat-message");
        text.getStyleClass().add(isError ? "error-message" : isHelp ? "help-message"
                : isUserMessage ? "command-line" : "response-line");
        VBox content = createContent(text, showInvalidCommandImage);
        if (showInvalidCommandImage) {
            text.getStyleClass().remove("error-message");
            text.getStyleClass().add("error-message-text");
            content.getStyleClass().add("error-message");
        }
        setSpacing(8);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(isUserMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        getChildren().add(content);
        HBox.setHgrow(content, Priority.NEVER);
        getStyleClass().add(isError ? "error-dialog" : isHelp ? "help-dialog"
                : isUserMessage ? "user-dialog" : "gooble-dialog");
    }

    private VBox createContent(Label message, boolean showInvalidCommandImage) {
        if (!showInvalidCommandImage) {
            return new VBox(message);
        }

        Image image = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/invalid-command.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(260);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return new VBox(8, message, imageView);
    }
}
