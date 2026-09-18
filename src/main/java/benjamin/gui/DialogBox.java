package benjamin.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * One turn of the conversation.
 *
 * <p>The two sides look deliberately different, because the conversation is
 * not between two people. What the user typed appears as a short bubble on the
 * right. Benjamin's replies fill the width on the left in a fixed-width font,
 * so that numbered task lists line up instead of drifting. A complaint gets a
 * style of its own again, so a mistake is never mistaken for an answer.
 *
 * <p>There are no profile pictures. The conversation only ever has the same
 * two participants, so a picture on every line would spend width that the
 * reply itself can use, on information the user already has.
 */
public class DialogBox extends HBox {
    /** How much of the window width one of the user's own bubbles may take. */
    private static final double USER_WIDTH_FRACTION = 0.75;

    private final Label text;

    private DialogBox(String message, Pos alignment, String styleClass) {
        text = new Label(message);
        text.setWrapText(true);
        text.getStyleClass().add(styleClass);

        this.setAlignment(alignment);
        this.getChildren().add(text);
        this.getStyleClass().add("dialog-row");
    }

    /**
     * Returns a compact bubble on the right holding what the user typed.
     *
     * @param message the command the user entered.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox box = new DialogBox(message, Pos.TOP_RIGHT, "user-bubble");
        // Binding rather than fixing the width stops a long command spanning
        // the window, while still letting the bubble shrink when it is resized.
        box.text.maxWidthProperty().bind(box.widthProperty().multiply(USER_WIDTH_FRACTION));

        return box;
    }

    /**
     * Returns a full width panel on the left holding one of Benjamin's replies.
     *
     * @param message the reply to show.
     */
    public static DialogBox getBenjaminDialog(String message) {
        return fillWidth(new DialogBox(message, Pos.TOP_LEFT, "bot-panel"));
    }

    /**
     * Returns a panel styled to stand out, for a reply that reports a problem.
     *
     * @param message the complaint to show.
     */
    public static DialogBox getProblemDialog(String message) {
        return fillWidth(new DialogBox(message, Pos.TOP_LEFT, "problem-panel"));
    }

    private static DialogBox fillWidth(DialogBox box) {
        box.text.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box.text, Priority.ALWAYS);

        return box;
    }
}
