package peter;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one line of the conversation. The user's input is shown as a
 * narrow chat bubble on the right; Peter's replies are shown as a wide
 * response panel on the left instead of a mirrored bubble with an avatar,
 * since the conversation is between a person and an app - not two people -
 * and there are only ever two fixed participants, so a repeated profile
 * picture on every line added visual noise without telling the reader
 * anything a bubble's side and colour don't already say. Error messages get
 * their own amber-accented style so mistakes are easy to spot at a glance.
 */
public class DialogBox extends HBox {
    private static final double USER_BUBBLE_WIDTH_FRACTION = 0.72;
    private static final double PETER_PANEL_WIDTH_FRACTION = 0.94;

    @FXML
    private Label dialog;

    private DialogBox(String text, double maxWidthFraction) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
        dialog.getStyleClass().add("dialog-label");
        dialog.maxWidthProperty().bind(widthProperty().multiply(maxWidthFraction));
    }

    /**
     * Creates a dialog box for the user's input, styled as a chat bubble
     * aligned to the right.
     *
     * @param text the command entered by the user
     * @return the user's dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, USER_BUBBLE_WIDTH_FRACTION);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a dialog box for one of Peter's normal replies, styled as a
     * response panel aligned to the left.
     *
     * @param text Peter's response text
     * @return Peter's dialog box
     */
    public static DialogBox getPeterDialog(String text) {
        return peterStyledDialog(text, "peter-card");
    }

    /**
     * Creates a dialog box for an error message (e.g. an unrecognised
     * command or invalid input), styled to visually stand out from Peter's
     * normal replies.
     *
     * @param text the error message
     * @return the error dialog box
     */
    public static DialogBox getErrorDialog(String text) {
        return peterStyledDialog(text, "error-card");
    }

    /**
     * Creates a left-aligned dialog box for one of Peter's messages,
     * shared by {@link #getPeterDialog} and {@link #getErrorDialog}, which
     * otherwise differ only in which style class they apply.
     *
     * @param text the message text
     * @param styleClass the CSS style class distinguishing a normal reply from an error
     * @return the styled dialog box
     */
    private static DialogBox peterStyledDialog(String text, String styleClass) {
        DialogBox dialogBox = new DialogBox(text, PETER_PANEL_WIDTH_FRACTION);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.dialog.getStyleClass().add(styleClass);
        return dialogBox;
    }
}
