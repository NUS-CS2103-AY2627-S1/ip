package bubba;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one message in Bubba's chat window.
 */
public class DialogBox extends HBox {
    private static final double MAXIMUM_MESSAGE_WIDTH = 320;
    private static final Insets MESSAGE_MARGIN = new Insets(4, 8, 4, 8);

    private DialogBox(String text, Pos alignment, String styleClass) {
        assert text != null : "Dialog text must not be null";

        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(MAXIMUM_MESSAGE_WIDTH);
        message.getStyleClass().add(styleClass);

        setAlignment(alignment);
        setPadding(MESSAGE_MARGIN);
        getChildren().add(message);
    }

    /**
     * Creates a right-aligned dialog for a command entered by the user.
     *
     * @param text User's command.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, Pos.TOP_RIGHT, "user-bubble");
    }

    /**
     * Creates a left-aligned dialog for Bubba's response.
     *
     * @param text Bubba's response.
     * @return Bubba dialog box.
     */
    public static DialogBox getBubbaDialog(String text) {
        return new DialogBox(text, Pos.TOP_LEFT, "bubba-bubble");
    }

    /**
     * Creates a left-aligned dialog that highlights an error.
     *
     * @param text Error message returned by Bubba.
     * @return Error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text, Pos.TOP_LEFT, "error-bubble");
    }
}
