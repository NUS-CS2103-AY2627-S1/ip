package clarry.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Displays compact user commands and wider Clarry response cards. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label speaker;
    @FXML
    private VBox message;

    /** Loads a message card whose width follows the available conversation width. */
    private DialogBox(String text, boolean isUser, boolean isError) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new AssertionError("Unable to load a dialog box", e);
        }
        assert dialog != null && speaker != null && message != null : "FXML must inject the message controls";
        dialog.setText(text);
        speaker.setText(isUser ? "YOU" : isError ? "ERROR" : "CLARRY");
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        message.getStyleClass().add(isUser ? "user-bubble" : isError ? "error-bubble" : "clarry-bubble");
        message.maxWidthProperty().bind(widthProperty().multiply(isUser ? 0.85 : 1.0));
        if (!isUser) {
            message.prefWidthProperty().bind(message.maxWidthProperty());
        }
    }

    /**
     * Creates a right-aligned user command.
     *
     * @param text user's message
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, true, false);
    }

    /**
     * Creates a regular Clarry reply.
     *
     * @param text Clarry's response
     * @return Clarry dialog box
     */
    public static DialogBox getClarryDialog(String text) {
        return getClarryDialog(text, false);
    }

    /**
     * Creates a Clarry reply with a distinct label and colour for errors.
     *
     * @param text Clarry's response
     * @param isError whether the command failed
     * @return Clarry response card
     */
    public static DialogBox getClarryDialog(String text, boolean isError) {
        return new DialogBox(text, false, isError);
    }
}
