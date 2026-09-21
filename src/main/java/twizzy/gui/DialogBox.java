package twizzy.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Represents one user or chatbot message in the conversation. */
public class DialogBox extends HBox {
    private static final double HORIZONTAL_MESSAGE_MARGIN = 64;

    private DialogBox(String text, String messageStyle, Pos alignment) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setPadding(new Insets(10));
        message.getStyleClass().addAll("message", messageStyle);
        message.maxWidthProperty().bind(widthProperty()
                .subtract(HORIZONTAL_MESSAGE_MARGIN));

        getStyleClass().add("dialog-box");
        setAlignment(alignment);
        setPadding(new Insets(6, 12, 6, 12));
        getChildren().add(message);
    }

    /** Creates a right-aligned dialog for a user message. */
    public static DialogBox user(String text) {
        return new DialogBox(text, "user-message", Pos.CENTER_RIGHT);
    }

    /** Creates a left-aligned dialog for a Twizzy response. */
    public static DialogBox bot(String text) {
        return new DialogBox(text, "bot-message", Pos.CENTER_LEFT);
    }

    /** Creates a left-aligned dialog that calls attention to an invalid command. */
    public static DialogBox error(String text) {
        return new DialogBox(text, "error-message", Pos.CENTER_LEFT);
    }
}
