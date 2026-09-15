package nova;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * One message in the transcript, built from DialogBox.fxml.
 *
 * <p>The conversation is between a person and an application rather than
 * between two people, so the two sides are deliberately not drawn alike.
 * A user message is a short line of speech: a compact tinted bubble on the
 * right that hugs its text. A Nova reply is program output: a block of
 * fixed-width text spanning the window, marked by a rule down its left edge.
 * The fixed-width font is not only decoration -- a numbered task list only
 * lines up in columns if every character is the same width.
 *
 * <p>Neither side carries an avatar. The user needs no portrait of
 * themselves, and Nova's badge earns its keep better as the window icon than
 * as an image repeated beside every reply, where it would cost width that a
 * long reply needs for text.
 *
 * <p>Colours, fonts and padding live in {@code /css/main.css}; this class
 * decides only the layout differences, which CSS cannot express.
 */
public class DialogBox extends HBox {
    /** Fraction of the window width a user bubble may fill before it wraps. */
    private static final double USER_BUBBLE_WIDTH_FRACTION = 0.82;

    @FXML
    private Label dialog;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load DialogBox.fxml", e);
        }

        // Injected by name: the fx:id in DialogBox.fxml must match this field
        // name. Renaming one side only leaves the field null, and the NPE
        // below would not say why.
        assert dialog != null : "DialogBox.fxml is missing fx:id=\"dialog\"";

        dialog.setText(text);
    }

    /**
     * Returns a dialog box for a command the user typed.
     *
     * @param text text the user entered.
     * @return the dialog box, styled as a right-aligned bubble.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.applyUserStyle();
        return box;
    }

    /**
     * Returns a dialog box for one of Nova's replies.
     *
     * @param text text of Nova's reply.
     * @return the dialog box, styled as a left-aligned block of output.
     */
    public static DialogBox getNovaDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.applyNovaStyle();
        return box;
    }

    /**
     * Styles this box as something the user said: a bubble against the right
     * edge, sized to its text.
     */
    private void applyUserStyle() {
        setAlignment(Pos.TOP_RIGHT);
        dialog.getStyleClass().add("user-bubble");

        // A short command should hug its text rather than stretch across the
        // window; a long one wraps instead of pushing the bubble off the edge.
        // Bound rather than fixed so the cap follows the window as it resizes.
        HBox.setHgrow(dialog, Priority.NEVER);
        dialog.maxWidthProperty().bind(widthProperty().multiply(USER_BUBBLE_WIDTH_FRACTION));
    }

    /**
     * Styles this box as something Nova reported: a block of output taking the
     * full width, so a wrapped task list is broken as few times as possible.
     */
    private void applyNovaStyle() {
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("nova-panel");

        HBox.setHgrow(dialog, Priority.ALWAYS);
        dialog.setMaxWidth(Double.MAX_VALUE);
    }
}
