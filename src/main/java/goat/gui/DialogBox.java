package goat.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * One turn of the conversation.
 *
 * The three kinds do not look alike, on purpose. This is not two people
 * talking: one side types short commands, the other answers with anything from
 * a word to a twenty-line task list, and sometimes with a complaint. Showing
 * all three in the same bubble would waste the window's width on the short ones
 * and cramp the long ones.
 *
 * So a user turn is a compact filled bubble on the right, capped at part of the
 * width; a reply is plain monospaced text on the left taking the full width,
 * which is what keeps a task listing's numbers in column; and an error is that
 * same text marked out, because a complaint the user scrolls past is a
 * complaint wasted.
 *
 * There are no profile pictures. The conversation has exactly two participants
 * and they alternate, so a picture beside every line tells the reader nothing
 * they cannot see from the alignment, while costing about fifty pixels of the
 * width that the replies actually need.
 */
public class DialogBox extends HBox {

    /** How much of the window's width a user's own message may take. */
    private static final double USER_WIDTH_FRACTION = 0.78;

    @FXML
    private Label dialog;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            System.err.println("Could not load a dialog box: " + e.getMessage());
        }
        dialog.setText(text);
    }

    /**
     * Returns a turn showing what the user typed.
     *
     * @param text the command the user entered
     * @return the bubble, ready to be added to the conversation
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.setAlignment(Pos.TOP_RIGHT);
        box.dialog.getStyleClass().add("user-bubble");
        // Capped rather than fixed, so the cap still means the same thing after
        // the window is resized.
        box.dialog.maxWidthProperty().bind(box.widthProperty().multiply(USER_WIDTH_FRACTION));
        return box;
    }

    /**
     * Returns a turn showing one of Goat's replies.
     *
     * @param text the reply, possibly spanning many lines
     * @return the bubble, ready to be added to the conversation
     */
    public static DialogBox getGoatDialog(String text) {
        return goatTurn(text, "reply-text");
    }

    /**
     * Returns a turn showing a complaint, marked so it is not scrolled past.
     *
     * @param text the complaint
     * @return the bubble, ready to be added to the conversation
     */
    public static DialogBox getErrorDialog(String text) {
        return goatTurn(text, "error-text");
    }

    /** Builds a left-aligned, full-width turn with the given style. */
    private static DialogBox goatTurn(String text, String styleClass) {
        DialogBox box = new DialogBox(text);
        box.setAlignment(Pos.TOP_LEFT);
        box.dialog.getStyleClass().add(styleClass);
        // A reply gets the whole width: task listings are the longest thing
        // Goat says, and wrapping them early is what makes them hard to read.
        box.dialog.maxWidthProperty().bind(box.widthProperty());
        return box;
    }
}
