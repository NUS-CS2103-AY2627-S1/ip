package seedu.goat.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * One message in the conversation, with its speaker named for assistive tools.
 * <p>
 * Both speakers share this class and differ only in which side they sit on, so the two
 * halves of the transcript can never drift apart in wording or spacing.
 */
public class DialogBox extends HBox {

    /** The message text. */
    @FXML
    private Label dialog;

    /**
     * Creates a message laid out for the user's side with an accessible speaker name.
     *
     * @param message the text to show.
     * @param speakerName the speaker name announced by assistive tools.
     */
    private DialogBox(String message, String speakerName) {
        try {
            // This class is both the root and the controller of its own FXML, which is
            // what lets a DialogBox be constructed and added like any other node.
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load the packaged dialog layout", e);
        }

        dialog.setText(message);
        dialog.setAccessibleText(speakerName + ": " + message);
    }

    /**
     * Returns a box showing one of the user's messages on the right.
     *
     * @param message the text to show.
     * @param speakerName the user label.
     * @return the box to add to the transcript.
     */
    public static DialogBox getUserDialog(String message, String speakerName) {
        return new DialogBox(message, speakerName);
    }

    /**
     * Returns a box showing one of GOAT's replies on the left.
     *
     * @param message the text to show.
     * @param speakerName the chatbot label.
     * @return the box to add to the transcript.
     */
    public static DialogBox getGoatDialog(String message, String speakerName) {
        DialogBox box = new DialogBox(message, speakerName);
        box.flip();
        return box;
    }

    /**
     * Aligns GOAT's replies to the left, on the opposite side of the
     * window from the user's messages.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
