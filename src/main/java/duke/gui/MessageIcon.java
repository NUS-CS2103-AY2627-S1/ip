package duke.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polyline;

/** Identifies each side of the conversation using a simple, code-drawn arrow. */
final class MessageIcon extends StackPane {
    /** Creates an arrow pointing from the speaker's side towards the message. */
    MessageIcon(boolean isUser) {
        setMinSize(36, 36);
        setPrefSize(36, 36);
        setMaxSize(36, 36);
        getStyleClass().addAll("message-icon", isUser ? "user-icon" : "assistant-icon");
        setAccessibleText(isUser ? "Your message" : "Wangsa reply");

        Polyline arrow = new Polyline(-4, -6, 2, 0, -4, 6);
        arrow.setRotate(isUser ? 180 : 0);
        arrow.getStyleClass().add("message-arrow");
        getChildren().add(arrow);
    }
}
