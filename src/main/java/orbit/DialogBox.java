package orbit;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one message in the conversation.
 */
public class DialogBox extends HBox {
    private final Label message = new Label();

    private DialogBox(String text) {
        message.setText(text);
        message.setWrapText(true);
        message.maxWidthProperty().bind(widthProperty().multiply(0.9));
        getChildren().add(message);
    }

    /** @return A right-aligned dialog containing the user's message. */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialog = new DialogBox(text);
        dialog.setAlignment(Pos.TOP_RIGHT);
        dialog.message.getStyleClass().add("user-message");
        return dialog;
    }

    /** @return A left-aligned dialog containing Orbit's response. */
    public static DialogBox getOrbitDialog(String text) {
        DialogBox dialog = new DialogBox(text);
        dialog.setAlignment(Pos.TOP_LEFT);
        dialog.message.getStyleClass().add("orbit-message");
        if (text.contains("OOPS!!!")) {
            dialog.message.getStyleClass().add("error-message");
        }
        return dialog;
    }
}
