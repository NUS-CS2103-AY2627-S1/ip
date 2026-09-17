package glennon.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Displays compact user commands, full-width replies, and distinct error cards.
 */
public class DialogBox extends HBox {
    @FXML
    private VBox message;
    @FXML
    private Label dialog;
    @FXML
    private Label messageType;

    private DialogBox(String text, String speaker) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Glennon could not load the dialog layout.", e);
        }
        dialog.setText(text);
        dialog.setAccessibleText(speaker + ": " + text);
    }

    /**
     * Creates a compact, right-aligned user command.
     *
     * @param text user input.
     * @return user dialog.
     */
    public static DialogBox createUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text.isBlank() ? "(empty command)" : text, "You");
        dialogBox.message.getStyleClass().add("user-card");
        dialogBox.message.maxWidthProperty().bind(dialogBox.widthProperty().multiply(0.82));
        return dialogBox;
    }

    /**
     * Creates a left-aligned Glennon response that uses the available reading width.
     *
     * @param text response text.
     * @return Glennon dialog.
     */
    public static DialogBox createGlennonDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, "Glennon");
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.message.getStyleClass().add("reply-card");
        dialogBox.message.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dialogBox.message, Priority.ALWAYS);
        return dialogBox;
    }

    /**
     * Creates an error response with both a colored accent and an explicit heading.
     *
     * @param text error explanation.
     * @return error dialog.
     */
    public static DialogBox createErrorDialog(String text) {
        DialogBox dialogBox = createGlennonDialog(text);
        dialogBox.message.getStyleClass().add("error-card");
        dialogBox.messageType.setManaged(true);
        dialogBox.messageType.setVisible(true);
        dialogBox.dialog.setAccessibleText("Error: " + text);
        return dialogBox;
    }
}
