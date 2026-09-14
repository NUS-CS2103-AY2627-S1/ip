package groot.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Presents one message with a compact speaker badge and wrapping bubble.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label speakerBadge;

    /**
     * Loads a message bubble and its speaker badge.
     */
    private DialogBox(String text, String speaker) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load the dialog box layout", error);
        }
        dialog.setText(text.strip());
        speakerBadge.setText(speaker);
        dialog.maxWidthProperty().bind(widthProperty().multiply(0.83).subtract(48));
    }

    /**
     * Creates a right-aligned user message.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text, "You");
        box.getChildren().setAll(box.dialog, box.speakerBadge);
        box.setAlignment(Pos.TOP_RIGHT);
        box.dialog.getStyleClass().add("user-label");
        box.speakerBadge.getStyleClass().add("user-badge");
        return box;
    }

    /**
     * Creates a left-aligned Groot reply.
     */
    public static DialogBox getGrootDialog(String text) {
        DialogBox box = new DialogBox(text, "G");
        box.dialog.getStyleClass().add("reply-label");
        return box;
    }

    /**
     * Creates an error reply distinguished by both text and color.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox box = getGrootDialog("Error: " + text.strip());
        box.dialog.getStyleClass().add("error-label");
        return box;
    }
}
