package sozius.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** A compact message row with distinct user, assistant and error styles. */
public class DialogBox extends HBox {
    private DialogBox(String text, boolean isUser, boolean isError) {
        getStyleClass().add("message-row");
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        setMinWidth(0);

        Label speaker = new Label(isUser ? "YOU" : isError ? "SOZIUS · ERROR" : "SOZIUS");
        speaker.getStyleClass().add("message-speaker");
        Label dialog = new Label(text);
        dialog.getStyleClass().add("message-text");
        dialog.setWrapText(true);
        dialog.setMinWidth(0);
        dialog.setMaxWidth(Double.MAX_VALUE);
        dialog.setMinHeight(USE_PREF_SIZE);

        VBox card = new VBox(4, speaker, dialog);
        card.getStyleClass().addAll("message-card",
                isUser ? "user-message" : isError ? "error-message" : "assistant-message");
        card.setMinWidth(0);
        card.setMinHeight(USE_PREF_SIZE);
        // User commands stay compact; replies use the available reading width.
        card.maxWidthProperty().bind(widthProperty().multiply(isUser ? 0.85 : 1.0));
        if (!isUser) {
            card.prefWidthProperty().bind(widthProperty());
        }
        getChildren().add(card);
    }

    /** Creates a user message. The image argument is retained for existing callers. */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, true, false);
    }

    /** Creates an assistant reply. The image argument is retained for existing callers. */
    public static DialogBox getSoziusDialog(String text, Image img) {
        return new DialogBox(text, false, false);
    }

    /** Creates a labelled error reply, distinguishable without relying on colour. */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text, false, true);
    }
}
