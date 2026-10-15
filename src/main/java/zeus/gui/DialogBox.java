package zeus.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Represents one user or Zeus message in the conversation. */
public class DialogBox extends HBox {
    /** Text shown inside the dialog bubble. */
    @FXML
    private Label dialog;

    /** Compact text avatar identifying the speaker. */
    @FXML
    private Label avatar;

    /**
     * Creates a dialog containing the supplied text.
     *
     * @param text Message to display.
     */
    private DialogBox(String text) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }
        assert dialog != null && avatar != null
                : "DialogBox.fxml must inject the dialog and avatar labels.";
        dialog.setText(text);
    }

    /**
     * Creates a right-aligned dialog for a user command.
     *
     * @param text User command to display.
     * @return New user dialog.
     */
    public static DialogBox createUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.dialog.getStyleClass().add("user-dialog");
        dialogBox.avatar.getStyleClass().add("user-avatar");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a response from Zeus.
     *
     * @param text Zeus response to display.
     * @return New Zeus dialog.
     */
    public static DialogBox createZeusDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.getChildren().setAll(dialogBox.avatar, dialogBox.dialog);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.dialog.getStyleClass().add("zeus-dialog");
        dialogBox.avatar.getStyleClass().add("zeus-avatar");
        dialogBox.avatar.setText("⚡");
        return dialogBox;
    }

    /**
     * Creates a visually highlighted dialog for an error response from Zeus.
     *
     * @param text Error response to display.
     * @return New error dialog.
     */
    public static DialogBox createErrorDialog(String text) {
        DialogBox dialogBox = createZeusDialog(text);
        dialogBox.dialog.getStyleClass().add("error-dialog");
        dialogBox.avatar.getStyleClass().add("error-avatar");
        dialogBox.avatar.setText("!");
        return dialogBox;
    }
}
