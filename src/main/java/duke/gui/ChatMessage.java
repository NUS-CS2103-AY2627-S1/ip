package duke.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Presents one conversation entry, keeping message styling separate from command execution. */
final class ChatMessage extends HBox {
    /** Identifies the speaker and whether an assistant reply needs error emphasis. */
    enum Role {
        USER, ASSISTANT, ERROR
    }

    /** Builds an accessible, wrapping message bubble with a compact speaker symbol. */
    ChatMessage(String message, Role role) {
        this(createTextBody(message), role);
    }

    /** Creates a normal assistant bubble containing structured command help. */
    static ChatMessage help() {
        return new ChatMessage(new HelpView(), Role.ASSISTANT);
    }

    /** Creates a task reply whose dates and times align using layout columns, not spaces. */
    static ChatMessage tasks(TaskView body) {
        return new ChatMessage(body, Role.ASSISTANT);
    }

    /** Applies the same speaker and error styling to plain replies and structured help. */
    private ChatMessage(Region body, Role role) {
        super(10);
        boolean isUser = role == Role.USER;
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("message-row");

        VBox bubble = createBubble(body, role);
        MessageIcon icon = new MessageIcon(isUser);
        if (isUser) {
            getChildren().addAll(bubble, icon);
        } else {
            getChildren().addAll(icon, bubble);
        }
    }

    /** Creates a wrapping bubble whose speaker label and metadata distinguish errors and user messages. */
    private VBox createBubble(Region body, Role role) {
        boolean isUser = role == Role.USER;
        VBox bubble = new VBox(6);
        bubble.setMinWidth(0);
        bubble.maxWidthProperty().bind(Bindings.max(0,
                Bindings.min(isUser ? 560 : 780, widthProperty().subtract(60))));
        bubble.getStyleClass().add(isUser ? "user-bubble" : "assistant-bubble");
        if (role == Role.ERROR) {
            bubble.getStyleClass().add("error-bubble");
        }
        Label speaker = new Label(isUser ? "YOU" : "WANGSA");
        speaker.getStyleClass().add("message-speaker");
        body.setMinWidth(0);
        body.setMaxWidth(Double.MAX_VALUE);
        body.getStyleClass().add("message-body");
        bubble.getChildren().addAll(speaker, body);
        if (role == Role.ERROR) {
            Label metadata = new Label("ACTION NEEDED");
            metadata.getStyleClass().add("message-metadata");
            bubble.getChildren().add(metadata);
        }
        return bubble;
    }

    /** Keeps ordinary replies as literal text so task descriptions are never interpreted as markup. */
    private static Label createTextBody(String message) {
        Label body = new Label(message);
        body.setWrapText(true);
        return body;
    }
}
