package seedu.mattchatbot.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * One message in the conversation.
 * <p>
 * The two speakers are drawn differently on purpose. The conversation is
 * between a person and a program, not between two people, so showing both
 * sides as mirror-image bubbles would suggest a symmetry that is not there.
 * What the user typed is a short instruction, so it gets a compact bubble on
 * the right. What the chatbot says back is the content the window exists to
 * show -- often a whole task list -- so it runs the full width of the window
 * with no bubble around it and no avatar beside it, which leaves every pixel
 * of that width for the text.
 * <p>
 * Loads its own FXML with itself as both root and controller, so that a dialog
 * box can be created with {@code new} like any other control, rather than the
 * window having to know how it is put together.
 */
public class DialogBox extends HBox {

    /**
     * How much of the window's width one of the user's bubbles may fill.
     * <p>
     * Short of the whole width, so that a bubble still reads as one side of a
     * conversation rather than as a full-width banner, but a fraction rather
     * than a fixed number of pixels, so that it grows when the window does.
     */
    private static final double USER_WIDTH_FRACTION = 0.72;

    /** The text of the message. */
    @FXML
    private Label dialog;

    /**
     * Builds one dialog box.
     *
     * @param text the message to show
     */
    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load a dialog box.", e);
        }
        dialog.setText(text);
    }

    /**
     * Returns a box for something the user said: a compact bubble on the right.
     *
     * @param text what the user typed
     * @return the dialog box to add to the conversation
     */
    public static DialogBox forUser(String text) {
        DialogBox box = new DialogBox(text);
        box.getStyleClass().add("user-dialog");
        box.setAlignment(Pos.TOP_RIGHT);
        box.dialog.maxWidthProperty().bind(box.widthProperty().multiply(USER_WIDTH_FRACTION));
        return box;
    }

    /**
     * Returns a box for something the chatbot said, running the full width.
     *
     * @param text the chatbot's reply
     * @return the dialog box to add to the conversation
     */
    public static DialogBox forChatBot(String text) {
        return fullWidth(text, "bot-dialog");
    }

    /**
     * Returns a box for a complaint from the chatbot, marked so that it is not
     * mistaken for an ordinary reply.
     *
     * @param text what went wrong, written for the user
     * @return the dialog box to add to the conversation
     */
    public static DialogBox forError(String text) {
        return fullWidth(text, "error-dialog");
    }

    /**
     * Builds a box that fills the width of the conversation.
     *
     * @param text       the message to show
     * @param styleClass which of the chatbot's two looks to give it
     * @return the dialog box to add to the conversation
     */
    private static DialogBox fullWidth(String text, String styleClass) {
        DialogBox box = new DialogBox(text);
        box.getStyleClass().add(styleClass);
        box.setAlignment(Pos.TOP_LEFT);
        box.dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box.dialog, Priority.ALWAYS);
        return box;
    }
}
