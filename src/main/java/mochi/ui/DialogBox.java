package mochi.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Displays one user or Mochi message in the chat window.
 */
public class DialogBox extends HBox {
    private DialogBox(String message, boolean isMochi, boolean isError) {
        Label speaker = new Label(isError ? "MOCHI · NEEDS A LITTLE FIX" : isMochi ? "MOCHI" : "YOU");
        speaker.getStyleClass().add("speaker");
        Label text = new Label(message);
        text.setWrapText(true);
        text.setMinHeight(Region.USE_PREF_SIZE);
        text.setMaxWidth(Double.MAX_VALUE);
        VBox bubble = new VBox(5, speaker, text);
        bubble.getStyleClass().add(isError ? "error-message" : isMochi ? "mochi-message" : "user-message");
        bubble.setMinWidth(0);
        bubble.setMinHeight(Region.USE_PREF_SIZE);
        // Bot replies use the available width; user commands remain compact and right aligned.
        bubble.maxWidthProperty().bind(widthProperty().multiply(isMochi ? 1.0 : 0.85));
        if (isMochi) {
            bubble.prefWidthProperty().bind(widthProperty());
        }
        setMinHeight(Region.USE_PREF_SIZE);
        setAlignment(isMochi ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
        getChildren().add(bubble);
    }

    /**
     * Creates a right-aligned user message.
     *
     * @param message message text
     * @return user dialog box
     */
    public static DialogBox user(String message) {
        return new DialogBox(message, false, false);
    }

    /**
     * Creates a left-aligned Mochi response.
     *
     * @param message response text
     * @return Mochi dialog box
     */
    public static DialogBox mochi(String message) {
        return new DialogBox(message, true, false);
    }

    /**
     * Creates an error response with both a text label and a distinct colour.
     *
     * @param message actionable error explanation
     * @return error dialog box
     */
    public static DialogBox error(String message) {
        return new DialogBox(message, true, true);
    }
}
