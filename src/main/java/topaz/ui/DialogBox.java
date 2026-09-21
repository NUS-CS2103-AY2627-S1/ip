package topaz.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Represents one message in the Gronk conversation.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private VBox message;

    @FXML
    private Label heading;

    @FXML
    private Label errorHeading;

    /**
     * Creates a dialog box containing the given message.
     *
     * @param text the message displayed in the dialog box
     */
    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
    }

    /**
     * Configures a full-width, left-aligned application response card.
     */
    private void configureResponse() {
        setAlignment(Pos.TOP_LEFT);
        message.getStyleClass().add("gronk-dialog");
        HBox.setHgrow(message, Priority.ALWAYS);
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param text the user's message
     * @return the created dialog box
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.heading.setVisible(false);
        dialogBox.heading.setManaged(false);
        dialogBox.message.getStyleClass().add("user-dialog");
        dialogBox.message.maxWidthProperty().bind(dialogBox.widthProperty().multiply(0.8));
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Topaz response.
     *
     * @param text Topaz's response
     * @return the created dialog box
     */
    public static DialogBox getTopazDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.configureResponse();
        dialogBox.heading.setText(Ui.CATCHPHRASE);
        // Present the leading battle cry as a heading, preserving task text verbatim.
        String prefix = Ui.CATCHPHRASE + System.lineSeparator();
        if (text.startsWith(prefix)) {
            dialogBox.dialog.setText(text.substring(prefix.length()));
        }
        return dialogBox;
    }

    /**
     * Creates an error card with both a text heading and a distinct color.
     *
     * @param text the error explanation
     * @return the created error card
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getTopazDialog(text);
        dialogBox.errorHeading.setVisible(true);
        dialogBox.errorHeading.setManaged(true);
        dialogBox.message.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
