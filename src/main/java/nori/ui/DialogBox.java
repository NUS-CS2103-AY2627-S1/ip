package nori.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Displays compact user commands and full-width, wrapping responses with explicit error labels.
 */
public class DialogBox extends VBox {
    private DialogBox(String text, boolean isUser, boolean isError) {
        Label speaker = new Label(isUser ? "YOU" : isError ? "NEEDS ATTENTION" : "NORI");
        Label message = new Label(text);
        VBox card = new VBox(6, speaker, message);

        speaker.getStyleClass().add("speaker");
        message.getStyleClass().add("dialog-message");
        message.setWrapText(true);
        message.setMinWidth(0);
        message.setMaxWidth(Double.MAX_VALUE);
        message.setMinHeight(Region.USE_PREF_SIZE);
        card.setMinWidth(0);
        card.setMinHeight(Region.USE_PREF_SIZE);
        card.getStyleClass().addAll("message-card", isUser ? "user-message" : "nori-message");
        if (isError) {
            card.getStyleClass().add("error-message");
        }

        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setMinHeight(Region.USE_PREF_SIZE);
        card.maxWidthProperty().bind(widthProperty().multiply(isUser ? 0.85 : 1.0));
        getChildren().add(card);
    }

    /** Returns a dialog box containing a user message. */
    public static DialogBox createUserDialog(String text) {
        return new DialogBox(text, true, false);
    }

    /** Returns a dialog box containing a Nori response. */
    public static DialogBox createNoriDialog(String text) {
        return createNoriDialog(text, false);
    }

    /** Returns a Nori response, highlighting errors with both a label and a distinct colour. */
    public static DialogBox createNoriDialog(String text, boolean isError) {
        return new DialogBox(text, false, isError);
    }
}
