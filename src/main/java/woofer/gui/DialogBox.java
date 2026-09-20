package woofer.gui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Displays a labelled message with different layouts for commands, replies, and errors.
 */
public class DialogBox extends VBox {
    /** Wrapping message text, sized relative to the available conversation width. */
    @FXML
    private Label dialog;

    /** Text identifying the speaker and, when needed, the error state. */
    @FXML
    private Label speaker;

    /**
     * Loads a message layout and applies its presentation style.
     *
     * @param text message content.
     * @param heading visible speaker or status label.
     * @param styleClass CSS class identifying the message type.
     * @param isUser whether to use a compact, right-aligned command bubble.
     */
    private DialogBox(String text, String heading, String styleClass, boolean isUser) {
        URL fxmlResource = DialogBox.class.getResource("/view/DialogBox.fxml");
        if (fxmlResource == null) {
            throw new IllegalStateException("Unable to find the dialog box resource.");
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(text);
        speaker.setText(heading);
        getStyleClass().add(styleClass);
        setFillWidth(false);
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        // Bot responses use the full width; commands leave a small visual indent.
        dialog.maxWidthProperty().bind(widthProperty().multiply(isUser ? 0.85 : 1.0));
        if (!isUser) {
            dialog.setPrefWidth(Double.MAX_VALUE);
        }
    }

    /**
     * Creates a compact, right-aligned user command.
     *
     * @param text user message text.
     * @return the user dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, "YOU", "user-message", true);
    }

    /**
     * Creates a full-width Woofer response.
     *
     * @param text Woofer response text.
     * @return the response dialog box.
     */
    public static DialogBox getWooferDialog(String text) {
        return new DialogBox(text, "WOOFER", "woofer-message", false);
    }

    /**
     * Creates a visually distinct error with a textual status for accessibility.
     *
     * @param text explanation of the problem.
     * @return the error dialog box.
     */
    public static DialogBox getErrorDialog(String text) {
        return new DialogBox(text, "WOOFER · ATTENTION", "error-message", false);
    }
}
